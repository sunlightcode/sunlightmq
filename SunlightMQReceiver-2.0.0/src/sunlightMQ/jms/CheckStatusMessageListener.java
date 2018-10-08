package sunlightMQ.jms;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;

import SunlightFrame.config.AppConfig;
import SunlightFrame.log.AppLogger;
import flowController.jmx.ActiveMQJMXThread;
import sunlightMQ.bean.CatchExceptionBean;
import sunlightMQ.processor.MsgProcessor;

public class CheckStatusMessageListener implements MessageListener {
	Connection con;
	Session session;
	MsgProcessor msgProcessor;
	CatchExceptionBean bean;

	private static Boolean needResendMessage = Boolean.valueOf(
			AppConfig.getInstance().getParameterConfig().getParameter("checkStatusMessageListener.needResendMessage"));

	// private static String jmxServiceURL =
	// AppConfig.getInstance().getParameterConfig()
	// .getParameter("flowController.jmxServiceURL");

	// private static String objectName =
	// AppConfig.getInstance().getParameterConfig()
	// .getParameter("flowController.objectName");

	public CheckStatusMessageListener(MsgProcessor msgProcessor, Connection con, Session session,
			CatchExceptionBean bean) {
		this.msgProcessor = msgProcessor;
		this.con = con;
		this.session = session;
		this.bean = bean;
	}

	public void onMessage(Message message) {
		try {
			if (null != message && Session.CLIENT_ACKNOWLEDGE == session.getAcknowledgeMode()) {
				message.acknowledge();
			}

			if (message.getJMSReplyTo() != null) {
				TextMessage response = session.createTextMessage();
				response.setText(msgProcessor.getStatus().equals("1") ? "running" : "stop");
				response.setJMSCorrelationID(message.getJMSCorrelationID());

				MessageProducer replyProducer = session.createProducer(null);
				replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				replyProducer.send(message.getJMSReplyTo(), response);
			}
			/*
			 * if (0 < getMyQueueSize(jmxServiceURL, objectName,
			 * msgProcessor.getQueueName())) { msgProcessor.restart(); }
			 */

			if (Boolean.TRUE == needResendMessage && null != ActiveMQJMXThread.getInstance() && null != msgProcessor) {
				if (0 < ActiveMQJMXThread.getInstance().getQueueSize(msgProcessor.getQueueName())) {
					AppLogger.getInstance()
							.infoLog("SunlightMQManager: ActiveMQJMXThread getQueueSize. 队列【" + msgProcessor.getQueueName()
									+ "】剩余消息数 QueueSize=" + ActiveMQJMXThread.getInstance()
											.getQueueSize(msgProcessor.getQueueName()));

					AppLogger.getInstance().infoLog("【" + msgProcessor.getQueueName() + "】"
							+ "队列中存在剩余消息(QueueSize>0), 消息处理器重启, msgProcessor restart!");

					// 采用重启consumer方式(重启session)，重发消息
					msgProcessor.restart();

					// 采用recover方式重发消息
					// msgProcessor.getSession().recover();
				}
			}

		} catch (Exception e) {
			AppLogger.getInstance().errorLog(
					"消息中心[" + msgProcessor.getMsgCenterName() + "]消息处理器[" + msgProcessor.getName() + "]处理状态检查信息失败", e);
		}

	}

	@SuppressWarnings("unused")
	private Long getMyQueueSize(String jmxServiceURL, String objectName, String myQueueName) throws Exception {

		JMXServiceURL url = new JMXServiceURL(jmxServiceURL);// "service:jmx:rmi:///jndi/rmi://localhost:2011/jmxrmi"
		JMXConnector connector = JMXConnectorFactory.connect(url, null);
		connector.connect();
		MBeanServerConnection connection = connector.getMBeanServerConnection();
		// 需要注意的是，这里的my-broker必须和配置的名称相同
		ObjectName name = new ObjectName(objectName);// "my-broker:brokerName=localhost,type=Broker"
		BrokerViewMBean mBean = (BrokerViewMBean) MBeanServerInvocationHandler.newProxyInstance(connection, name,
				BrokerViewMBean.class, true);
		// System.out.println(mBean.getBrokerName());

		for (ObjectName queueName : mBean.getQueues()) {
			QueueViewMBean queueMBean = (QueueViewMBean) MBeanServerInvocationHandler.newProxyInstance(connection,
					queueName, QueueViewMBean.class, true);
			if (queueMBean.getName().equals(myQueueName))// "tienstest1"
			{
				Long queueSize = queueMBean.getQueueSize();

				connector.close();

				return queueSize;
			}
		}

		connector.close();

		return (long) -1;
	}
}
