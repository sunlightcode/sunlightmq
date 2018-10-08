package sunlightMQ.processor;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Hashtable;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import SunlightFrame.config.AppConfig;
import SunlightFrame.database.DBConnectionPool;
import SunlightFrame.database.DBProxy;
import SunlightFrame.log.AppLogger;
import sunlightMQ.AppKeys;
import sunlightMQ.bean.CatchExceptionBean;
import sunlightMQ.jms.CatchExcption;
import sunlightMQ.jms.MessageConsume;

public class ReceiverProcessor implements MessageListener, ExceptionListener {
	private String msgProcessorID;
	private String mqServerUrl;
	private String mqServerUserName;
	private String mqServerPassword;

	private String msgCenterName;
	private String queueName;
	private String queueType;
	private String name;
	private int startThreadNumber;
	private String callUrl;
	private String callRequestTypeID;
	private String callbackMethodTypeID;
	private String callbackQueueID;
	private String callbackQueueName;
	private String callbackUrl;
	private String callbackeRequestTypeID;
	boolean autoRunning;
	private String status; // 0 停止 1 开启
	private CatchExceptionBean bean;
	private javax.jms.Connection jmsConnection;
	private Session session;
	private MessageConsumer consumer = null;

	private int maxNumConn = 50;// 接口应用的最大并发数量
	private Integer receiverNumber = Integer
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("threadPoolExecutor.receiverNumber"));
	private Integer corePoolSize = Integer
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("threadPoolExecutor.corePoolSize"));
	private int discardCounter = 0;


	public ReceiverProcessor(String msgCenterName, String msgProcessorID) {
		this.msgCenterName = msgCenterName;
		this.msgProcessorID = msgProcessorID;
		this.status = "0";

		init();
	}

	public void onException(JMSException exception) {
		AppLogger.getInstance().errorLog("异常监听器接收到异常：" + exception);
		stop();
		start();
	}

	public void onMessage(javax.jms.Message message) {
		int activeSize = ReceiverThreadPool.getInstance().getActiveSize();
		int activeCounter = ReceiverThreadPool.getInstance().getQueueCounter(queueName);
		if (corePoolSize > activeSize && maxNumConn > activeCounter) {
			MessageConsume consume = new MessageConsume(session, queueName, callUrl, callRequestTypeID,
					callbackMethodTypeID, callbackQueueName, callbackUrl, callbackeRequestTypeID, message, bean);
			ReceiverThreadPool.getInstance().execute(consume);			
		} else {
			discardCounter = discardCounter + 1;
			System.out.println("discard the message, i=" + discardCounter);
			System.out.println("corePoolSize = activeSize, activeSize=" + activeSize);
			System.out.println("maxNumConn = activeCounter, activeCounter=" + activeCounter);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String expHMS = "";
			try {
				expHMS = formatter.format(message.getJMSExpiration());
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			AppLogger.getInstance()
					.infoLog("因未获得可用线程，或并发数已经达到最大值，异步消息未手动签收，MQ准备重试: " + "activeSize=" + activeSize
							+ ", activeCounter=" + activeCounter + ", 消息过期时间为=" + expHMS + ", 这是第"
							+ discardCounter + "个被抛弃的消息");
		}

	}

	public void init() {
		Connection con = null;
		try {
			con = DBConnectionPool.getInstance().getConnection();
			Hashtable<String, String> k = new Hashtable<String, String>();
			k.put("msgProcessorID", msgProcessorID);
			Hashtable<String, String> data = DBProxy.query(con, "msgProcessor_V", k).get(0);
			if (!data.get("validFlag").equals("1")) {
				bean.setExceptionContent("消息中心[" + msgCenterName + "]消息处理器[" + data.get("name") + "]状态为无效");
				CatchExcption.log2DB(bean);
				throw new Exception("消息中心[" + msgCenterName + "]消息处理器[" + data.get("name") + "]状态为无效");
			}

			this.mqServerUrl = data.get("url");
			this.mqServerUserName = data.get("userName");
			this.mqServerPassword = data.get("password");

			this.msgCenterName = data.get("msgCenterName");
			this.queueName = data.get("queueName");
			this.queueType = data.get("queueType");

			this.name = data.get("name");
			this.startThreadNumber = Integer.parseInt(data.get("startThreadNumber"));
			this.callUrl = data.get("callUrl");
			this.callRequestTypeID = data.get("callRequestTypeID");
			this.callRequestTypeID = data.get("callRequestTypeID");
			this.callbackMethodTypeID = data.get("callbackMethodTypeID");
			this.callbackQueueID = data.get("callbackQueueID");
			this.callbackQueueName = data.get("callbackQueueName");
			this.callbackUrl = data.get("callbackUrl");
			this.callbackeRequestTypeID = data.get("callbackeRequestTypeID");
			this.autoRunning = data.get("autoRunning").equals("1");
			CatchExceptionBean bean = new CatchExceptionBean();
			bean.setProcessExceptionReportModeID(data.get("processExceptionReportModeID"));
			bean.setProcessExceptionUrl(data.get("processExceptionUrl"));
			bean.setMsgCenterName(data.get("msgCenterName"));
			bean.setMsgProcessorName(data.get("name"));
			bean.setMsgProcessorIP("");
			this.bean = bean;
			
			if (0 < this.startThreadNumber) {
				this.maxNumConn = this.startThreadNumber;
				this.maxNumConn = this.maxNumConn / this.receiverNumber;
				System.out.println("MAX_NUM_CONN = startThreadNumber MAX_NUM_CONN=" + this.maxNumConn);
			} else {
				this.maxNumConn = this.maxNumConn / this.receiverNumber;
			}
			ReceiverThreadPool.getInstance().getQueueThreadCounterMap().put(queueName, 0);

			AppLogger.getInstance().infoLog("消息中心[" + msgCenterName + "]消息处理器[" + name + "]初始化完成");
		} catch (Exception e) {
			AppLogger.getInstance().errorLog("消息中心[" + msgCenterName + "]消息处理器[" + name + "]初始化异常" + e);
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	public void start() {
		try {
			jmsConnection = ConnectionManager.getConnection(mqServerUserName, mqServerPassword, mqServerUrl);
			jmsConnection.start();

			session = jmsConnection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);

			if (queueType.equals("topic")) {
				Topic topic = new ActiveMQTopic(queueName + "?consumer.prefetchSize=" + AppKeys.MQ_PREFETCHSIZE);
				consumer = session.createConsumer(topic);
			} else {
				Queue queue = new ActiveMQQueue(queueName + "?consumer.prefetchSize=" + AppKeys.MQ_PREFETCHSIZE);
				consumer = session.createConsumer(queue);
			}

			consumer.setMessageListener(this);

			this.status = "1";

			AppLogger.getInstance()
					.infoLog("消息中心[" + msgCenterName + "]消息处理器[" + name + "]启动成功，启动线程数：" + startThreadNumber);
		} catch (Exception e) {
			bean.setExceptionContent("消息中心[" + msgCenterName + "]消息处理器[" + name + "]启动失败");
			CatchExcption.log2DB(bean);
		}

	}

	public void stop() {
		if (consumer != null) {
			try {
				consumer.close();
			} catch (JMSException ex) {
				AppLogger.getInstance().errorLog("生产者关闭失败：" + ex);
			}
		}
		if (session != null) {
			try {
				session.close();
			} catch (JMSException ex) {
				AppLogger.getInstance().errorLog("会话关闭失败：" + ex);
			}
		}
		if (jmsConnection != null) {
			try {
				jmsConnection.close();
			} catch (JMSException ex) {
				AppLogger.getInstance().errorLog("连接关闭失败：" + ex);
			}
		}
		this.status = "0";
	}

	public String getMqServerUrl() {
		return mqServerUrl;
	}

	public void setMqServerUrl(String mqServerUrl) {
		this.mqServerUrl = mqServerUrl;
	}

	public String getMqServerUserName() {
		return mqServerUserName;
	}

	public void setMqServerUserName(String mqServerUserName) {
		this.mqServerUserName = mqServerUserName;
	}

	public String getMqServerPassword() {
		return mqServerPassword;
	}

	public void setMqServerPassword(String mqServerPassword) {
		this.mqServerPassword = mqServerPassword;
	}

	public String getMsgCenterName() {
		return msgCenterName;
	}

	public void setMsgCenterName(String msgCenterName) {
		this.msgCenterName = msgCenterName;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStartThreadNumber() {
		return startThreadNumber;
	}

	public void setStartThreadNumber(int startThreadNumber) {
		this.startThreadNumber = startThreadNumber;
	}

	public String getCallUrl() {
		return callUrl;
	}

	public void setCallUrl(String callUrl) {
		this.callUrl = callUrl;
	}

	public String getCallRequestTypeID() {
		return callRequestTypeID;
	}

	public void setCallRequestTypeID(String callRequestTypeID) {
		this.callRequestTypeID = callRequestTypeID;
	}

	public String getCallbackMethodTypeID() {
		return callbackMethodTypeID;
	}

	public void setCallbackMethodTypeID(String callbackMethodTypeID) {
		this.callbackMethodTypeID = callbackMethodTypeID;
	}

	public String getCallbackQueueID() {
		return callbackQueueID;
	}

	public void setCallbackQueueID(String callbackQueueID) {
		this.callbackQueueID = callbackQueueID;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getCallbackeRequestTypeID() {
		return callbackeRequestTypeID;
	}

	public void setCallbackeRequestTypeID(String callbackeRequestTypeID) {
		this.callbackeRequestTypeID = callbackeRequestTypeID;
	}

	public boolean isAutoRunning() {
		return autoRunning;
	}

	public void setAutoRunning(boolean autoRunning) {
		this.autoRunning = autoRunning;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public javax.jms.Connection getJmsConnection() {
		return jmsConnection;
	}

	public void setJmsConnection(javax.jms.Connection jmsConnection) {
		this.jmsConnection = jmsConnection;
	}
}
