package sunlightMQ.jms;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class JMSSendder {
	public static void sendMsg(Connection connection, String queueName, boolean isTopic, boolean persistent,
			int ackType, String text) throws Exception {

		Session session = null;
		Destination destination = null;
		MessageProducer producer = null;

		try {
			session = connection.createSession(Boolean.FALSE, ackType);
			if (isTopic) {
				destination = session.createTopic(queueName);
			} else {
				destination = session.createQueue(queueName);
			}
			producer = session.createProducer(destination);
			producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
			TextMessage sendMessage = session.createTextMessage(text);

			producer.send(sendMessage);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (Exception e2) {
			}
		}
	}
}
