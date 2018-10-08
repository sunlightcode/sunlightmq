package sunlightMQ.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import sunlightMQ.AppKeys;

public class CmdMessageUtil {

	// ConnectionFactory ：连接工厂，JMS 用它创建连接
	private ConnectionFactory connectionFactory = null;
	// Connection ：JMS 客户端到JMS Provider 的连接
	private Connection connection = null;
	// Session： 一个发送或接收消息的线程
	private Session session = null;
	// Destination ：消息的目的地;消息发送给谁.
	private Destination destination = null;
	// MessageProducer：消息发送者
	private MessageProducer producer = null;
	// TextMessage message;

	// connectionFactory parameters
	private String userName = "";
	private String password = "";
	private String brokerURL = "tcp://localhost:61616";

	public CmdMessageUtil() {
		super();
	}

	public CmdMessageUtil(String userName, String password, String brokerURL) {
		super();
		this.userName = userName;
		this.password = password;
		this.brokerURL = brokerURL;
	}

	public void sendCmdMsg(String cmdMsg) {

		// 构造ConnectionFactory实例对象，此处采用ActiveMq的实现jar
		connectionFactory = new ActiveMQConnectionFactory(userName, password, brokerURL);
		try {
			// 构造从工厂得到连接对象
			connection = connectionFactory.createConnection();
			// 启动
			connection.start();
			// 获取操作连接
			session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
			// 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
			destination = session.createTopic(AppKeys.CMD_QUEUE);
			// 得到消息生成者【发送者】
			producer = session.createProducer(destination);
			// 设置不持久化，可以更改
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			// 构造消息
			sendMessage(session, producer, cmdMsg);

			session.commit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != connection)
					connection.close();
				if (null != session)
					session.close();
			} catch (Throwable ignore) {
			}
		}
	}

	private static void sendMessage(Session session, MessageProducer producer, String cmdMsg) throws Exception {

		TextMessage message = session.createTextMessage(cmdMsg);// "tienstest2-start"

		producer.send(message);
	}

}
