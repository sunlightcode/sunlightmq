package sunlightMQ.jms;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;

public class ConsumerMessageListener implements MessageListener {
	Connection con;
	Session session;
	boolean sync;
	String callUrl;
	String callRequestTypeID;
	String callbackMethodTypeID;
	String callbackQueueName;
	String callbackUrl;
	String callbackeRequestTypeID;

	public ConsumerMessageListener(Connection con, Session session, String callUrl, String callRequestTypeID,
			String callbackMethodTypeID, String callbackQueueName, String callbackUrl, String callbackeRequestTypeID) {
		this.con = con;
		this.session = session;
		this.callUrl = callUrl;
		this.callbackeRequestTypeID = callbackeRequestTypeID;
		this.callbackMethodTypeID = callbackMethodTypeID;
		this.callbackQueueName = callbackQueueName;
		this.callbackUrl = callbackUrl;
		this.callbackeRequestTypeID = callbackeRequestTypeID;
	}

	public void onMessage(Message message) {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
