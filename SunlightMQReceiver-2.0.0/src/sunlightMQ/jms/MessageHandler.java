package sunlightMQ.jms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import SunlightFrame.config.AppConfig;
import SunlightFrame.log.AppLogger;
import SunlightFrame.util.StringUtil;
import sunlightMQ.AppKeys;
import sunlightMQ.DataCache;
import sunlightMQ.bean.CatchExceptionBean;
import sunlightMQ.client.APIInvoker;
import sunlightMQ.client.MyHttpClient;

public class MessageHandler {
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

	private static String tmpErrorValue = AppConfig.getInstance().getParameterConfig()
			.getParameter("flowController.errorValue");

	public MessageHandler(Session session, String queueName, String callUrl, String callRequestTypeID,
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

	public void handleMsg() {
		try {
			String messageJson = ((TextMessage) message).getText();
			Map<String, String> map = JSON.parseObject(messageJson, new TypeReference<Map<String, String>>() {
			});
			String identity = map.get(AppKeys.RESPONSE_IDENTITY);
			String masterServer = map.get("masterServer");

			Hashtable<String, String> msgProcessor = DataCache.getInstance()
					.getMsgProcessor(bean.getMsgProcessorName());
			boolean toOtherCenterFlag = msgProcessor.get("toOtherCenter").equals("1");

			APIInvoker invoker = new APIInvoker(callUrl, messageJson, toOtherCenterFlag);
			String result = invoker.invokUrl(bean);
			
			AppLogger.getInstance().infoLog("[调用]" + identity + "[" + callUrl + "]返回：" + result);

			if (identity != null) {
				sendReponseMsg(toOtherCenterFlag, masterServer, identity, result);
			}

			Map<String, String> resultMsgParam = JSON.parseObject(result, new TypeReference<Map<String, String>>() {
			});
			String status = resultMsgParam.get("status");
			
			// 消息手动签收功能 开始
			String syncFlag = map.get("syncFlag").trim();
			// 同步消息手动签收
			if (null != message && Session.CLIENT_ACKNOWLEDGE == this.session.getAcknowledgeMode()
					&& (null != syncFlag && !syncFlag.equals("0"))) {
				message.acknowledge();
				AppLogger.getInstance().infoLog("SunlightMQManager:同步消息已手动签收: " + "identity=" + identity + ",callUrl="
						+ callUrl + ",result=" + result);
			}

			// 异步消息手动签收			
			if (null != message && Session.CLIENT_ACKNOWLEDGE == this.session.getAcknowledgeMode()
					&& (null != syncFlag && syncFlag.equals("0"))) {
				AppLogger.getInstance().infoLog("SunlightMQManager:异步结果返回,准备签收: " + "identity=" + identity + ",callUrl="
						+ callUrl + ",result=" + result);
				if (null != status
						&& (status.equals("1") || (null != tmpErrorValue && !tmpErrorValue.contains(status)))) {
					message.acknowledge();
					AppLogger.getInstance().infoLog("SunlightMQManager:异步消息已手动签收: " + "identity=" + identity + ",callUrl="
							+ callUrl + ",result=" + result);
				} else {
					AppLogger.getInstance().infoLog("SunlightMQManager:因接口返回异常信息，异步消息未手动签收，MQ准备重试: " + "identity="
							+ identity + ",callUrl=" + callUrl + ",result=" + result);
				}
			}
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String expHMS = formatter.format(message.getJMSExpiration());
			AppLogger.getInstance().infoLog("SunlightMQManager:消息过期时间为=" + expHMS + ",identity=" + identity + ",callUrl="
					+ callUrl + ",result=" + result);
			// 消息手动签收功能 结束

		} catch (Exception e) {
			try {
				AppLogger.getInstance().infoLog(Calendar.getInstance().getTimeInMillis() + " 消息处理失败："
						+ message.getJMSMessageID() + " exception info:" + e.getMessage());
				AppLogger.getInstance().errorLog("消息处理失败：" + message.getJMSMessageID(), e);

				bean.setMessageID(message.getJMSMessageID().toString());
				bean.setMessageJson(((TextMessage) message).getText());
				bean.setExceptionAddress("Reciver");
				bean.setExceptionType("消息处理失败");
				CatchExcption.log2DB(bean);
			} catch (Exception e2) {
			}
		} finally {
		}
	}

	private void sendReponseMsg(boolean toOtherCenterFlag, String masterServer, String identity, String result) {
		Map<String, String> map = JSON.parseObject(result, new TypeReference<Map<String, String>>() {
		});
		map.put(AppKeys.RESPONSE_IDENTITY, identity);

		String[] strArray = StringUtil.split(identity, "-");
		String srcDest = strArray[1];

		String localDest = AppConfig.getInstance().getParameterConfig().getParameter("mqServer.name");
		localDest = localDest.substring(0, localDest.indexOf("-"));

		if (!toOtherCenterFlag && localDest.equals(srcDest)) {
			MessageProducer replyProducer = null;
			try {
				AppLogger.getInstance().infoLog("发送同步调用回执消息给SENDER:" + map);
				Topic responseTopic = session.createTopic(AppKeys.RESPONSE_TOPIC_NAME);
				TextMessage response = session.createTextMessage(JSON.toJSONString(map));
				response.setJMSExpiration(20 * 1000);
				replyProducer = session.createProducer(responseTopic);
				replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				replyProducer.send(responseTopic, response);
			} catch (Exception e) {
			} finally {
				if (replyProducer != null) {
					try {
						replyProducer.close();
					} catch (Exception ex) {
					}
				}
			}
		} else if (!localDest.equals(srcDest)) {
			MyHttpClient.postRouteData(AppKeys.ROUTER_URL, masterServer, JSON.toJSONString(map));
		}
	}
}
