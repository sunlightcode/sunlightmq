package sunlightMQ.jms;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;

import SunlightFrame.config.AppConfig;
import sunlightMQ.bean.CatchExceptionBean;
import sunlightMQ.processor.ReceiverThreadPool;

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
	String msgCenterName;
	String msgProcessorName;
	String queueName;
	String msgProcessorIP;
	CatchExceptionBean bean;
	Integer queueCapacity = Integer.valueOf(AppConfig.getInstance().getParameterConfig()
			.getParameter("threadPoolExecutor.linkedBlockingQueue.capacity"));
	int maxNumConn = 50;// 接口应用的最大并发数量
	Integer receiverNumber = Integer
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("threadPoolExecutor.receiverNumber"));
	Integer corePoolSize = Integer
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("threadPoolExecutor.corePoolSize"));
	int discardCounter = 0;

	public ConsumerMessageListener(Session session, String queueName, String callUrl, String callRequestTypeID,
			String callbackMethodTypeID, String callbackQueueName, String callbackUrl, String callbackeRequestTypeID,
			CatchExceptionBean bean, Integer startThreadNumber) {
		this.session = session;
		this.callUrl = callUrl;
		this.callbackeRequestTypeID = callbackeRequestTypeID;
		this.callbackMethodTypeID = callbackMethodTypeID;
		this.callbackQueueName = callbackQueueName;
		this.callbackUrl = callbackUrl;
		this.callbackeRequestTypeID = callbackeRequestTypeID;
		this.bean = bean;
		this.queueName = queueName;
		if (null != startThreadNumber && 0 < startThreadNumber) {
			this.maxNumConn = startThreadNumber;
			this.maxNumConn = this.maxNumConn / this.receiverNumber;
			System.out.println("MAX_NUM_CONN = startThreadNumber MAX_NUM_CONN=" + this.maxNumConn);
		} else {
			this.maxNumConn = this.maxNumConn / this.receiverNumber;
		}
		ReceiverThreadPool.getInstance().getQueueThreadCounterMap().put(queueName, 0);
	}

	public void onMessage(Message message) {
		int activeSize = ReceiverThreadPool.getInstance().getActiveSize();
		int activeCounter = ReceiverThreadPool.getInstance().getQueueCounter(queueName);
		if (corePoolSize > activeSize && maxNumConn > activeCounter) {
			receiverThreadsExecuter(message);
		} else {
			for (;;) { // 使用自动签收，for循环保证每个消息得到执行机会
				int activeSize2 = ReceiverThreadPool.getInstance().getActiveSize();
				int activeCounter2 = ReceiverThreadPool.getInstance().getQueueCounter(queueName);
				if (corePoolSize > activeSize2 && maxNumConn > activeCounter2) {
					receiverThreadsExecuter(message);
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// discardCounter = discardCounter + 1;
			// System.out.println("discard the message, i=" + discardCounter);
			// System.out.println("corePoolSize = activeSize, activeSize=" +
			// activeSize);
			// System.out.println("maxNumConn = activeCounter, activeCounter=" +
			// activeCounter);
			// SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd
			// HH:mm:ss");
			// String expHMS = "";
			// try {
			// expHMS = formatter.format(message.getJMSExpiration());
			// } catch (JMSException e) { // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// AppLogger.getInstance()
			// .infoLog("因未获得可用线程，或并发数已经达到最大值，异步消息未手动签收，MQ准备重试: " +
			// "activeSize=" + activeSize + ", activeCounter="
			// + activeCounter + ", 消息过期时间为=" + expHMS + ", 这是第" +
			// discardCounter + "个被抛弃的消息");

			// System.out.println("ReceiverThreadPool queueCapacity <
			// waitSize");
			// SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd
			// HH:mm:ss");
			// Date now = new Date();
			// String nowHMS = formatter.format(now);
			// String expHMS = "";
			// try {
			// expHMS = formatter.format(message.getJMSExpiration());
			// } catch (JMSException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// try {
			// Date nowDate = formatter.parse(nowHMS);
			// Date expDate = formatter.parse(expHMS);
			// long diff = nowDate.getTime() - expDate.getTime();
			// long days = diff / (1000 * 60 * 60 * 24);
			// } catch (ParseException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
	}

	// public void onMessage(Message message) {
	// try {
	// String messageJson = ((TextMessage) message).getText();
	// Map<String, String> map = JSON.parseObject(messageJson, new
	// TypeReference<Map<String, String>>() {
	// });
	// String identity = map.get(AppKeys.RESPONSE_IDENTITY);
	// Integer msgAckType =
	// ReceiverThreadPool.getInstance().getMessageAcknowledge(identity);
	// if (null != msgAckType && 1 == msgAckType.intValue()) {
	// // 对重发消息进行签收
	// message.acknowledge();
	// ReceiverThreadPool.getInstance().removeMessageAcknowledge(identity);
	// } else {
	// int activeSize = ReceiverThreadPool.getInstance().getActiveSize();
	// int activeCounter =
	// ReceiverThreadPool.getInstance().getQueueCounter(queueName);
	// if (corePoolSize > activeSize && maxNumConn > activeCounter) {
	// receiverThreadExecute(message);
	// } else {
	// discardCounter = discardCounter + 1;
	// System.out.println("discard the message, i=" + discardCounter);
	// System.out.println("corePoolSize = activeSize, activeSize=" +
	// activeSize);
	// System.out.println("maxNumConn = activeCounter, activeCounter=" +
	// activeCounter);
	// SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	// String expHMS = "";
	// try {
	// expHMS = formatter.format(message.getJMSExpiration());
	// } catch (JMSException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// AppLogger.getInstance()
	// .infoLog("因未获得可用线程，或并发数已经达到最大值，异步消息未手动签收，MQ准备重试: " + "activeSize=" +
	// activeSize
	// + ", activeCounter=" + activeCounter + ", 消息过期时间为=" + expHMS + ", 这是第"
	// + discardCounter + "个被抛弃的消息");
	// }
	// }
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	private void receiverThreadsExecuter(Message message) {
		MessageConsume consume = new MessageConsume(session, queueName, callUrl, callRequestTypeID,
				callbackMethodTypeID, callbackQueueName, callbackUrl, callbackeRequestTypeID, message, bean);
		ReceiverThreadPool.getInstance().execute(consume);
	}

	// private void receiverThreadExecuteTest(Message message) {
	// MessageConsumeTest consumeTest = new MessageConsumeTest(session,
	// queueName, callUrl, callRequestTypeID,
	// callbackMethodTypeID, callbackQueueName, callbackUrl,
	// callbackeRequestTypeID, message, bean);
	// ReceiverThreadPool.getInstance().execute(consumeTest);
	// }
}
