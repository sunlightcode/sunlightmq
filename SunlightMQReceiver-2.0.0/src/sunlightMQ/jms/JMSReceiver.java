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

public class JMSReceiver {
	public static void receMsg(Session session, String queueName, String queueType, String callUrl,
			String callRequestTypeID, String callbackMethodTypeID, String callbackQueueID, String callbackQueueName,
			String callbackUrl, String callbackeRequestTypeID, CatchExceptionBean bean, Integer startThreadNumber) {

		AppLogger.getInstance().infoLog("receMsg begin");

		MessageConsumer consumer = null;
		try {
			if (queueType.equals("topic")) {
				Topic topic = new ActiveMQTopic(queueName + "?consumer.prefetchSize=" + AppKeys.MQ_PREFETCHSIZE);
				// 在同一个连接的ClientID下,持久订阅者的名称必须唯一
				// javax.jms.JMSException: Durable consumer is in use for
				// client: 1 and
				// subscriptionName: 11

				// TopicSubscriber subscriber = session.createSubscriber(topic);
				consumer = session.createConsumer(topic);
			} else {
				Queue queue = new ActiveMQQueue(queueName + "?consumer.prefetchSize=" + AppKeys.MQ_PREFETCHSIZE);
				consumer = session.createConsumer(queue);
			}

			// consumer.receive();

			consumer.setMessageListener(
					new ConsumerMessageListener(session, queueName, callUrl, callRequestTypeID, callbackMethodTypeID,
							callbackQueueName, callbackUrl, callbackeRequestTypeID, bean, startThreadNumber));

		} catch (Exception e) {
			AppLogger.getInstance().errorLog("JMSReceiver error", e);
		}
	}

}