package sunlightMQ.processor;

import java.sql.Connection;
import java.util.Hashtable;

import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import SunlightFrame.config.AppConfig;
import SunlightFrame.database.DBConnectionPool;
import SunlightFrame.database.DBProxy;
import SunlightFrame.log.AppLogger;
import sunlightMQ.AppKeys;
import sunlightMQ.bean.CatchExceptionBean;
import sunlightMQ.jms.CatchExcption;
import sunlightMQ.jms.CheckStatusMessageListener;
import sunlightMQ.jms.JMSConsumer;
import sunlightMQ.jms.JMSReceiver;

public class MsgProcessor {
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
	private static Boolean CLIENT_ACKNOWLEDGE = Boolean
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("CLIENT_ACKNOWLEDGE"));// //消息手动签收功能

	public MsgProcessor(String msgCenterName, String msgProcessorID) throws Exception {
		this.msgCenterName = msgCenterName;
		this.msgProcessorID = msgProcessorID;
		this.status = "0";
		init();
		startListenCheckStatusQueue();
	}

	public void init() throws Exception {
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
			AppLogger.getInstance().infoLog("消息中心[" + msgCenterName + "]消息处理器[" + name + "]初始化完成");
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	public void start() throws Exception {
		if (status.equals("1")) {
			return;
		}

		try {
			if (null == jmsConnection) {
				jmsConnection = ConnectionManager.getPoolConnection(mqServerUserName, mqServerPassword, mqServerUrl);
				// 创建持久订阅的时候,必须要设置client,否则会报错:
				// javax.jms.JMSException: You cannot create a durable
				// subscriber
				// without specifying a unique clientID on a Connection

				// 如果clientID重复(已经存在相同id的活动连接),会报错
				// javax.jms.InvalidClientIDException: Broker: localhost -
				// Client: 1
				// already connected from tcp://127.0.0.1:2758
				// jmsConnection.setClientID(queueName);
				jmsConnection.start();
				AppLogger.getInstance().infoLog("------connection start------------");
			}
			// session = jmsConnection.createSession(Boolean.FALSE,
			// Session.AUTO_ACKNOWLEDGE);

			// 消息手动签收功能,Session.CLIENT_ACKNOWLEDGE，(ackType
			// =2),由客户端程序通过手工调用Message.acknowledge()方法显示确认接收。
			if (null != CLIENT_ACKNOWLEDGE && Boolean.TRUE == CLIENT_ACKNOWLEDGE) {
				session = jmsConnection.createSession(Boolean.FALSE, Session.CLIENT_ACKNOWLEDGE);
			} else {
				session = jmsConnection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
			}

			JMSReceiver.receMsg(session, queueName, queueType, callUrl, callRequestTypeID, callbackMethodTypeID,
					callbackQueueID, callbackQueueName, callbackUrl, callbackeRequestTypeID, bean, startThreadNumber);

			status = "1";
			AppLogger.getInstance()
					.infoLog("消息中心[" + msgCenterName + "]消息处理器[" + name + "]启动成功，最大并发数量：" + startThreadNumber);
		} catch (Exception e) {
			bean.setExceptionContent("消息中心[" + msgCenterName + "]消息处理器[" + name + "]启动失败");
			CatchExcption.log2DB(bean);
			throw new Exception("消息中心[" + msgCenterName + "]消息处理器[" + name + "]启动失败", e);
		} finally {
			// if (null != jmsConnection) {
			// jmsConnection.close();
			// }
		}
	}

	@SuppressWarnings("unused")
	private void consumerThreadExecute() {
		JMSConsumer consumer = new JMSConsumer(session, queueName, queueType, callUrl, callRequestTypeID,
				callbackMethodTypeID, callbackQueueName, callbackUrl, callbackeRequestTypeID, bean);
		ConsumerThreadPool.getInstance().execute(consumer);
	}

	public void stop() throws Exception {
		if (status.equals("0")) {
			return;
		}

		try {
			if (session != null) {
				session.close();

				status = "0";
			}
		} catch (Exception e) {
			bean.setExceptionContent("消息中心[" + msgCenterName + "]消息处理器[" + name + "]停止出错");
			CatchExcption.log2DB(bean);
			throw new Exception("消息中心[" + msgCenterName + "]消息处理器[" + name + "]停止出错", e);
		}
		/*
		 * try { if (jmsConnection != null) { jmsConnection.close();
		 * //此处报异常，考虑使用连接池获取的jmsConnection，是否可以这样关闭
		 * 
		 * status = "0"; } } catch (Exception e) {
		 * bean.setExceptionContent("消息中心[" + msgCenterName + "]消息处理器[" + name +
		 * "]停止出错"); CatchExcption.log2DB(bean); throw new Exception("消息中心[" +
		 * msgCenterName + "]消息处理器[" + name + "]停止出错", e); }
		 */
	}

	public void restart() throws Exception {
		stop();
		start();
	}

	public void startListenCheckStatusQueue() throws Exception {
		javax.jms.Connection jmsConnection = null;
		Session session = null;
		Destination destination = null;
		MessageConsumer consumer = null;
		try {
			jmsConnection = ConnectionManager.getConnection(mqServerUserName, mqServerPassword, mqServerUrl);
			jmsConnection.start();
			session = jmsConnection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
			// session = jmsConnection.createSession(Boolean.FALSE,
			// Session.CLIENT_ACKNOWLEDGE);
			destination = session.createQueue(AppKeys.STATUS_QUEUE);
			consumer = session.createConsumer(destination);
			consumer.setMessageListener(new CheckStatusMessageListener(this, jmsConnection, session, bean));

			AppLogger.getInstance().infoLog("消息中心[" + msgCenterName + "]消息处理器[" + name + "]启动监听状态检查队列完成");
		} catch (Exception e) {
			try {
				if (session != null) {
					session.close();
				}
			} catch (Exception e2) {
			}

			try {
				if (jmsConnection != null) {
					jmsConnection.close();
				}
			} catch (Exception e2) {
			}

			bean.setExceptionContent("消息中心[" + msgCenterName + "]消息处理器[" + name + "]启动监听状态检查队列失败");
			CatchExcption.log2DB(bean);
			throw new Exception("消息中心[" + msgCenterName + "]消息处理器[" + name + "]启动监听状态检查队列失败", e);
		}
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

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
}
