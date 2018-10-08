package sunlightMQ.jms;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class CheckMsgProcessorStatusJMSSendder {
	public static String sendMsg(String url, String userName, String password, String queueName, boolean isTopic,
			boolean persistent, int ackType, String text, boolean sync) throws Exception {
		Connection connection = null;
		Session session = null;

		Destination destination = null;
		MessageProducer producer = null;

		Destination replyDest = null;
		MessageConsumer responseConsumer = null;

		try {
			connection = ConnectionManager.getConnection(userName, password, url);
			connection.start();

			session = connection.createSession(Boolean.FALSE, ackType);
			destination = session.createQueue(queueName);
			producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			producer.setTimeToLive(36000);
			TextMessage sendMessage = session.createTextMessage(text);

			replyDest = session.createTemporaryQueue();
			responseConsumer = session.createConsumer(replyDest);
			sendMessage.setJMSReplyTo(replyDest);
			sendMessage.setJMSCorrelationID(System.currentTimeMillis() + "");

			producer.send(sendMessage);
			try {
				Message replyMessage = responseConsumer.receive(1000 * 10); // 同步调用等待时间
																			// 目前默认为10秒
				String replyMessageText = ((TextMessage) replyMessage).getText();
				return replyMessageText;
			} catch (Exception e) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}

			if (connection != null) {
				connection.close();
			}
		}
		return "";
	}
}
