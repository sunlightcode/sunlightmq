package sunlightMQ.jms;

import javax.jms.Message;
import javax.jms.Session;

import sunlightMQ.bean.CatchExceptionBean;
import sunlightMQ.processor.ReceiverThreadPool;

public class MessageConsumeTest extends Thread {
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
	public CatchExceptionBean bean;
	Message message;
	int i = 0;
	int n = 0;

	public MessageConsumeTest(Session session, String queueName, String callUrl, String callRequestTypeID,
			String callbackMethodTypeID, String callbackQueueName, String callbackUrl, String callbackeRequestTypeID,
			Message message, CatchExceptionBean bean) {
		this.session = session;
		this.callUrl = callUrl;
		this.callbackeRequestTypeID = callbackeRequestTypeID;
		this.callbackMethodTypeID = callbackMethodTypeID;
		this.callbackQueueName = callbackQueueName;
		this.callbackUrl = callbackUrl;
		this.callbackeRequestTypeID = callbackeRequestTypeID;
		this.message = message;
		this.bean = bean;
		this.queueName = queueName;
	}

	public void run() {
		try {
			ReceiverThreadPool.getInstance().addQueueCounter(queueName);
			
//			String messageJson = ((TextMessage) message).getText();
//			Map<String, String> map = JSON.parseObject(messageJson, new TypeReference<Map<String, String>>() {
//			});
//			String identity = map.get(AppKeys.RESPONSE_IDENTITY);
//			String masterServer = map.get("masterServer");
//
//			Hashtable<String, String> msgProcessor = DataCache.getInstance()
//					.getMsgProcessor(bean.getMsgProcessorName());
//			boolean toOtherCenterFlag = msgProcessor.get("toOtherCenter").equals("1");

//			APIInvoker invoker = new APIInvoker(callUrl, messageJson, toOtherCenterFlag);
//			String result = invoker.invokUrl(bean);
			
			sleep(10000);
			message.acknowledge();
			System.out.println("消息已签收");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("消息签收异常！");
		} finally {
//			 if (session != null) {
//				 try {
//					 session.close();
//				 } catch (Exception e) {
//					 
//				 }
//			 }
			ReceiverThreadPool.getInstance().reduceQueueCounter(queueName);
		}
	}

}
