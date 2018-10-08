package sunlightMQ.jms;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import SunlightFrame.log.AppLogger;

public class JMSReceiver {
	public static void receMsg(Connection jmsConnection, String queueName, String callUrl, String callRequestTypeID,
			String callbackMethodTypeID, String callbackQueueID, String callbackQueueName, String callbackUrl,
			String callbackeRequestTypeID) {
		Session session = null;

		Destination destination = null;
		MessageConsumer consumer = null;

		try {
			session = jmsConnection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);

			destination = session.createQueue(queueName);
			consumer = session.createConsumer(destination);
			consumer.setMessageListener(new ConsumerMessageListener(jmsConnection, session, callUrl, callRequestTypeID,
					callbackMethodTypeID, callbackQueueName, callbackUrl, callbackeRequestTypeID));
		} catch (Exception e) {
			AppLogger.getInstance().errorLog("接收消息错误", e);
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