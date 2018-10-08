package sunlightMQ.jms;

import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import SunlightFrame.log.AppLogger;
import sunlightMQ.AppKeys;
import sunlightMQ.bean.CatchExceptionBean;

public class JMSConsumer extends Thread {
	private Session session;
	private String queueName;
	private String queueType;
	private String callUrl;
	private String callRequestTypeID;
	private String callbackMethodTypeID;
	private String callbackQueueName;
	private String callbackUrl;
	private String callbackeRequestTypeID;
	private CatchExceptionBean bean;

	public JMSConsumer(Session session, String queueName, String queueType, String callUrl, String callRequestTypeID,
			String callbackMethodTypeID, String callbackQueueName, String callbackUrl, String callbackeRequestTypeID,
			CatchExceptionBean bean) {
		this.session = session;
		this.queueName = queueName;
		this.queueType = queueType;
		this.callUrl = callUrl;
		this.callRequestTypeID = callRequestTypeID;
		this.callbackMethodTypeID = callbackMethodTypeID;
		this.callbackQueueName = callbackQueueName;
		this.callbackUrl = callbackUrl;
		this.callbackeRequestTypeID = callbackeRequestTypeID;
		this.bean = bean;
	}

	public void run() {

		AppLogger.getInstance().infoLog("receMsg begin");

		MessageConsumer consumer = null;
		try {
			if (queueType.equals("topic")) {
				Topic topic = new ActiveMQTopic(queueName + "?consumer.prefetchSize=" + AppKeys.MQ_PREFETCHSIZE);
				consumer = session.createConsumer(topic);
			} else {
				Queue queue = new ActiveMQQueue(queueName + "?consumer.prefetchSize=" + AppKeys.MQ_PREFETCHSIZE);
				consumer = session.createConsumer(queue);
			}

			consumer.setMessageListener(new ConsumerListener(session, queueName, callUrl, callRequestTypeID,
					callbackMethodTypeID, callbackQueueName, callbackUrl, callbackeRequestTypeID, bean));

		} catch (Exception e) {
			AppLogger.getInstance().errorLog("JMSReceiver error", e);
		}
	}

}