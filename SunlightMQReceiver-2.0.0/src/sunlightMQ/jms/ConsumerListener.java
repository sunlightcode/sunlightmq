package sunlightMQ.jms;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;

import sunlightMQ.bean.CatchExceptionBean;

public class ConsumerListener implements MessageListener {
	Connection con;
	Session session;
	boolean sync;
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

	public ConsumerListener(Session session, String queueName, String callUrl, String callRequestTypeID,
			String callbackMethodTypeID, String callbackQueueName, String callbackUrl, String callbackeRequestTypeID,
			CatchExceptionBean bean) {
		this.session = session;
		this.callUrl = callUrl;
		this.callbackeRequestTypeID = callbackeRequestTypeID;
		this.callbackMethodTypeID = callbackMethodTypeID;
		this.callbackQueueName = callbackQueueName;
		this.callbackUrl = callbackUrl;
		this.callbackeRequestTypeID = callbackeRequestTypeID;
		this.bean = bean;
		this.queueName = queueName;
	}

	public void onMessage(Message message) {
		MessageHandler handler = new MessageHandler(session, queueName, callUrl, callRequestTypeID,
				callbackMethodTypeID, callbackQueueName, callbackUrl, callbackeRequestTypeID, message, bean);
		handler.handleMsg();
	}

}
