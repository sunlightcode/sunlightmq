package SunlightMQ.jms;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import SunlightMQ.AppKeys;
import SunlightMQ.DataCache;
import SunlightMQ.Result;
import SunlightMQ.tool.CatchExceptionBean;
import SunlightMQ.tool.CatchExcption;
import SunlightFrame.config.AppConfig;
import SunlightFrame.log.AppLogger;

public class JMSSendder {

	private static long replyTimeout = Long
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("TOPIC_REPLYMSG_TIMEOUT"));

	private static Boolean needTimeToLive = Boolean
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("NEED_TIMETOLIVE"));

	// private static long timeToLiveSize =
	// Long.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("TIMETOLIVESIZE"));

	public static String sendMsg(String url, String userName, String password, String queueName, boolean isTopic,
			boolean persistent, int ackType, Map<String, String> text, boolean sync, long timeToLive)
			throws Exception {
		Connection connection = null;
		Session session = null;

		Destination destination = null;
		MessageProducer producer = null;

		Topic replyDest = null;
		MessageConsumer responseConsumer = null;

		try {
			connection = ConnectionManager.getConnection(userName, password, url);
			connection.start();
			session = connection.createSession(Boolean.FALSE, ackType);

			if (isTopic) {
				destination = session.createTopic(queueName);
			} else {
				destination = session.createQueue(queueName);
			}

			producer = session.createProducer(destination);
			producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);

			String identity = "";
			String srcDest = AppConfig.getInstance().getParameterConfig().getParameter("mqServer.name");
			if (srcDest.indexOf("-") != -1) {
				srcDest = srcDest.substring(0, srcDest.indexOf("-"));
			}
			if (sync) {
				identity = AppKeys.MSG_IDENTITY_PRE + "-" + srcDest + "-" + (AppKeys.msgIDIndex++)
						+ System.currentTimeMillis();
				text.put("masterServer", url);
				text.put(AppKeys.RESPONSE_IDENTITY, identity);
			} else {
				identity = AppKeys.MSG_IDENTITY_PRE + "-" + srcDest + "-" + (AppKeys.msgIDIndex++)
						+ System.currentTimeMillis();
				text.put(AppKeys.RESPONSE_IDENTITY, identity);
			}
			
			TextMessage sendMessage = session.createTextMessage(JSON.toJSONString(text));

			if (sync) {
				replyDest = session.createTopic(AppKeys.RESPONSE_TOPIC_NAME);
				responseConsumer = session.createConsumer(replyDest);
			}

			if (null != needTimeToLive && Boolean.TRUE == needTimeToLive) {
				// 同步：消息有效期为Sender等待返回结果超时时间，过有效期后需要用户重发消息
				// 异步：消息有效期为24小时内有效
				if (sync) {
					producer.setTimeToLive(replyTimeout);
				} else {
					// 24小时（1000毫秒 * 60秒 * 60分 * 24）= 86400000毫秒
					producer.setTimeToLive(timeToLive);
				}
			}

			producer.send(sendMessage);
			AppLogger.getInstance().infoLog("[发送消息]" + sendMessage.getText());

			if (sync) {
				AppLogger.getInstance().infoLog("[开始等待同步消息回执]");
				String response = getResponseMsg(responseConsumer, identity);
				return response;
			} else {
				return new Result("1", "异步调用成功", null).toJSON();
			}

		} catch (Exception e) {
			AppLogger.getInstance().errorLog("发送消息到MQ失败", e);
			CatchExceptionBean bean = new CatchExceptionBean();
			bean.setExceptionAddress("Sender");
			bean.setExceptionType("发送消息到MQ失败");
			bean.setExceptionContent("send :" + url + "\t" + e);
			Hashtable<String, String> queue = DataCache.getInstance().getQueue(queueName);
			bean.setMsgCenterName(queue.get("name"));
			bean.setMsgProcessorName(queue.get("info"));
			CatchExcption.doAction(bean);
			return new Result("00300", "发送消息到MQ失败", null).toJSON();
		} finally {
			try {
				if (producer != null) {
					producer.close();
				}
			} catch (Exception e) {
			}

			try {
				if (session != null) {
					session.close();
				}
			} catch (Exception e) {
			}

			try {
				if (responseConsumer != null) {
					responseConsumer.close();
				}
			} catch (Exception e) {
			}

			// try {
			// if (replyDest != null) {
			// replyDest.delete();
			// }
			// } catch (Exception e) {}

			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
			}
		}
	}

	public static String getResponseMsg(MessageConsumer responseConsumer, String identity) {

		long begin = System.currentTimeMillis();
		while (true) {
			long end = System.currentTimeMillis();

			if (((end - begin)) > replyTimeout) {// 20 * 1000
				AppLogger.getInstance().infoLog("-------------------------[同步调用回执超时]--------------------");
				return new Result("00600", "同步调用出错", null).toJSON();
			}
			AppLogger.getInstance().infoLog("[轮训等待同步消息回执]");
			try {
				Message replyMessage = responseConsumer.receive(10 * 1000); // 同步调用等待时间
																			// 目前默认为10秒
				AppLogger.getInstance().infoLog("[监听到topic消息]");
				if (replyMessage != null) {
					String replyMessageText = ((TextMessage) replyMessage).getText();
					Map<String, String> map = JSON.parseObject(replyMessageText,
							new TypeReference<Map<String, String>>() {
							});
					String responseIdentity = map.get(AppKeys.RESPONSE_IDENTITY);
					AppLogger.getInstance().infoLog("[同步消息ID]" + identity);
					AppLogger.getInstance().infoLog("[监听到topic消息]" + responseIdentity);
					if (identity.equals(responseIdentity)) {
						AppLogger.getInstance().infoLog("[同步调用回执]" + replyMessageText);
						return replyMessageText;
					}
				}
				// else {
				// return new Result("00600", "同步调用出错", null).toJSON();
				// }
			} catch (Exception e) {
				AppLogger.getInstance()
						.infoLog(Calendar.getInstance().getTimeInMillis() + "同步调用出错，exception info:" + e.getMessage());
				AppLogger.getInstance().errorLog("同步调用出错", e);
				CatchExceptionBean bean = new CatchExceptionBean();
				bean.setExceptionAddress("Sender");
				bean.setExceptionType("同步调用出错");
				bean.setExceptionContent(e.getMessage());
				return new Result("00600", "同步调用出错", null).toJSON();
			}

			// try {
			// Thread.sleep(1000);
			// } catch (Exception e) {}
		}
	}
}
