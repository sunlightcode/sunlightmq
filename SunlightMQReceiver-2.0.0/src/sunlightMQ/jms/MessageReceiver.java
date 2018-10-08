package sunlightMQ.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import SunlightFrame.log.AppLogger;
import sunlightMQ.AppKeys;
import sunlightMQ.bean.CatchExceptionBean;
import sunlightMQ.processor.ReceiverThreadPool;

public class MessageReceiver implements MessageListener, ExceptionListener {

	private Connection connection = null;
	private ConnectionFactory connectionFactory = null;
	private Destination destination = null;
	private Session session = null;
	private MessageConsumer consumer = null;

	private boolean connected = false;

	private boolean needStop = false;

	private String username;
	private String password;
	private String serverAddr;

	private String queueType;
	private String topicName;

	String callUrl;
	String callRequestTypeID;
	String callbackMethodTypeID;
	String callbackQueueName;
	String callbackUrl;
	String callbackeRequestTypeID;
	String msgCenterName;
	String msgProcessorName;
	String queueName;
	String msgProcessorIP;
	CatchExceptionBean bean;

	public MessageReceiver(String username, String password, String serverAddr, String topicName, String queueType,
			String callUrl, String callRequestTypeID, String callbackMethodTypeID, String callbackQueueID,
			String callbackQueueName, String callbackUrl, String callbackeRequestTypeID, CatchExceptionBean bean) {
		this.username = username;
		this.password = password;
		this.serverAddr = serverAddr;
		this.topicName = topicName;
		this.queueType = queueType;

		this.callUrl = callUrl;
		this.callbackeRequestTypeID = callbackeRequestTypeID;
		this.callbackMethodTypeID = callbackMethodTypeID;
		this.callbackQueueName = callbackQueueName;
		this.callbackUrl = callbackUrl;
		this.callbackeRequestTypeID = callbackeRequestTypeID;
		this.bean = bean;
	}

	public void start() {
		needStop = false;
		(new Thread(new Runnable() {

			public void run() {
				initialize();
			}
		})).start();
	}

	public void stop() {
		needStop = true;
		cleanup();
	}

	private void initialize() {
		while ((!connected) && (!needStop)) {
			try {
				ConnectionFactory factory = new ActiveMQConnectionFactory(username, password, serverAddr);
				connectionFactory = factory;

				connection = connectionFactory.createConnection();

				connection.setExceptionListener(this);

				// destination = new com.sun.messaging.Topic(topicName);

				session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

				if (queueType.equals("topic")) {
					destination = new ActiveMQTopic(topicName + "?consumer.prefetchSize=" + AppKeys.MQ_PREFETCHSIZE);
				} else {
					destination = new ActiveMQQueue(topicName + "?consumer.prefetchSize=" + AppKeys.MQ_PREFETCHSIZE);
				}

				consumer = session.createConsumer(destination);
				consumer.setMessageListener(this);

				// 开始接收
				connection.start();

				connected = true;
			} catch (JMSException ex) {
				AppLogger.getInstance().errorLog("初始化失败：" + ex);
				cleanup();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				AppLogger.getInstance().errorLog("睡眠被打断：" + ex);
			}
		}
	}

	public void cleanup() {
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
		if (connection != null) {
			try {
				connection.close();
			} catch (JMSException ex) {
				AppLogger.getInstance().errorLog("连接关闭失败：" + ex);
			}
		}
		connected = false;
	}

	public void onException(JMSException exception) {
		AppLogger.getInstance().errorLog("异常监听器接收到异常：" + exception.getMessage());
		cleanup();
		initialize();
	}

	public void onMessage(Message message) {
		try {
			processMessage(message);
		} catch (Exception ex) {
			AppLogger.getInstance().errorLog("消息接收异常：" + ex.getMessage());
		}
	}

	public void processMessage(Message message) {
		try {
			MessageConsume consume = new MessageConsume(session, topicName, callUrl, callRequestTypeID,
					callbackMethodTypeID, callbackQueueName, callbackUrl, callbackeRequestTypeID, message, bean);

			ReceiverThreadPool.getInstance().execute(consume);
		} catch (Exception e) {
			try {
				AppLogger.getInstance().errorLog("消息:" + ((TextMessage) message).getText() + "处理失败", e);
			} catch (Exception e2) {
			}
		}
	}
}
