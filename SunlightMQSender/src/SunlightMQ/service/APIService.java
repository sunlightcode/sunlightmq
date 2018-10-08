package SunlightMQ.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSON;

import SunlightMQ.DataCache;
import SunlightMQ.jms.JMSSendder;
import SunlightMQ.tool.ZipUtils;
import flowController.level.ErrorSizeLevel;
import flowController.level.QueueSizeLevel;
import flowController.service.FCSMessage;
import flowController.service.FlowControlParameters;
import flowController.service.IFlowControlServices;
import flowController.service.impl.FlowControlServicesImpl;
import flowController.util.ArrayQueue;

import SunlightFrame.config.AppConfig;
import SunlightFrame.log.AppLogger;
import SunlightFrame.web.validate.StringCheckItem;

public class APIService extends BaseService {
	private static ArrayQueue<String> statusQueue = new ArrayQueue<String>(ErrorSizeLevel.totalSize);

	private static Boolean defaultValueEnabled = Boolean
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("defaultValue.enabled"));

	private static String defaultValueBusinessName = AppConfig.getInstance().getParameterConfig()
			.getParameter("defaultValue.businessName");

	private static String defaultValueAppKey = AppConfig.getInstance().getParameterConfig()
			.getParameter("defaultValue.appKey");

	private static String defaultValueSyncFlag = AppConfig.getInstance().getParameterConfig()
			.getParameter("defaultValue.syncFlag");

	private static String defaultValueMessageJson = AppConfig.getInstance().getParameterConfig()
			.getParameter("defaultValue.messageJson");

	private static String defaultValueMD5 = AppConfig.getInstance().getParameterConfig()
			.getParameter("defaultValue.md5");

	private static Boolean msgGZip = Boolean
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("MSG_GZIP"));

	public APIService(String className, String method, Hashtable<String, String> paras,
			Hashtable<String, String> sesstionKey, HttpServletRequest request) {
		super(className, method, paras, sesstionKey, request);
	}

	public boolean sendMessageActionCheck() throws Exception {
		Checker checker = getChecker();
		checker.addCheckItem(new StringCheckItem("messageJson", "消息体", true));

		return checker.check();
	}

	/**
	 * 同步发送消息
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void sendMessageAction() throws Exception {
		if (!"1".equals(getPara("syncFlag")) && null != getPara("timeToLive")
				&& !"".equals(getPara("timeToLive").trim())) {
			AppLogger.getInstance().infoLog(
					"[准备发送消息]businessName=" + getPara("businessName") + "，messageJson=" + getPara("messageJson")
							+ "，syncFlag=" + getPara("syncFlag") + "，timeToLive=" + getPara("timeToLive"));
		} else if (null != getPara("businessName") && !"".equals(getPara("businessName"))) {
			AppLogger.getInstance().infoLog("[准备发送消息]businessName=" + getPara("businessName") + "，messageJson="
					+ getPara("messageJson") + "，syncFlag=" + getPara("syncFlag"));
		}
		String tmpJson  = getBody();
		if (defaultValueEnabled && (null == getPara("businessName") || "".equals(getPara("businessName")))) {
			AppLogger.getInstance().infoLog("[准备发送消息(defaultValue)]businessName=" + defaultValueBusinessName + "，messageJson="
					+ tmpJson + "，syncFlag=" + defaultValueSyncFlag);
		}

		boolean syncFlag = getPara("syncFlag").equals("1");
		Hashtable<String, String> method = DataCache.getInstance().getMethod(getMethodName());
		Hashtable<String, String> queue = DataCache.getInstance().getQueue(method.get("c_queueName"));
		String mqServerName = AppConfig.getInstance().getParameterConfig().getParameter("mqServer.name");
		Hashtable<String, String> mqServer = DataCache.getInstance().getMqServer(mqServerName);

		String messageJson1 = getPara("messageJson");
		if (null != msgGZip && Boolean.TRUE == msgGZip) {
			messageJson1 = ZipUtils.gzip(messageJson1);
		}

		// Map<String, Object> srcMap = JSON.parseObject(messageJson1, new
		// TypeReference<Map<String, Object>>(){});
		// if (srcMap.get("jmsTraceID") == null) {
		// srcMap.put("jmsTraceID", Calendar.getInstance().getTimeInMillis() +
		// "");
		// messageJson1 = JSON.toJSONString(srcMap);
		// }

		Map<String, String> sendMessage = new HashMap<String, String>();
		if (null != getPara("businessName") && !"".equals(getPara("businessName"))) {
			sendMessage.put("messageJson", messageJson1);// 消息体
			String md5 = getPara("md5");
			sendMessage.put("md5", md5);// 密文
			sendMessage.put("appKey", getPara("appKey"));// AppKey
			sendMessage.put("businessName", getPara("businessName"));// businessName
			sendMessage.put("syncFlag", getPara("syncFlag"));// syncFlag
		} else if (defaultValueEnabled) {
			syncFlag = defaultValueSyncFlag.equals("1");
			sendMessage.put("businessName", defaultValueBusinessName);
			if(!"".equals(tmpJson) && null != tmpJson) {
				AppLogger.getInstance().infoLog("[发送特殊消息体]" + tmpJson);
				sendMessage.put("messageJson", tmpJson);// 消息体
			} else {
				AppLogger.getInstance().infoLog("[发送默认消息体]" + defaultValueMessageJson);
				sendMessage.put("messageJson", defaultValueMessageJson);// 消息体
			}
			sendMessage.put("appKey", defaultValueAppKey);
			sendMessage.put("syncFlag", defaultValueSyncFlag);// syncFlag
			sendMessage.put("md5", defaultValueMD5);// 密文
		}

		if (queue.isEmpty()) {
			status = "0";
			message = "不存在该消息队列";
		} else {
			// 流量控制代码-剩余队列统计参数;
			String tmpQueueSizeLevel1 = AppConfig.getInstance().getParameterConfig()
					.getParameter("flowController.queueSizeLevel1");
			String tmpQueueSizeLevel2 = AppConfig.getInstance().getParameterConfig()
					.getParameter("flowController.queueSizeLevel2");
			String tmpQueueSizeLevel3 = AppConfig.getInstance().getParameterConfig()
					.getParameter("flowController.queueSizeLevel3");
			// String tmpJmxServiceURL =
			// AppConfig.getInstance().getParameterConfig()
			// .getParameter("flowController.jmxServiceURL");
			// String tmpObjectName =
			// AppConfig.getInstance().getParameterConfig()
			// .getParameter("flowController.objectName");
			QueueSizeLevel.queueSizeLevel1 = Integer.valueOf(tmpQueueSizeLevel1);// 1;
			QueueSizeLevel.queueSizeLevel2 = Integer.valueOf(tmpQueueSizeLevel2);// 5;
			QueueSizeLevel.queueSizeLevel3 = Integer.valueOf(tmpQueueSizeLevel3);// 10;
			// String jmxServiceURL = tmpJmxServiceURL;//
			// "service:jmx:rmi:///jndi/rmi://localhost:2011/jmxrmi";
			// String objectName = tmpObjectName;//
			// "my-broker:brokerName=localhost,type=Broker";

			// 流量控制代码-出错统计参数;
			String tmpErrorValue = AppConfig.getInstance().getParameterConfig()
					.getParameter("flowController.errorValue");
			String tmpErrorSizeLevel1 = AppConfig.getInstance().getParameterConfig()
					.getParameter("flowController.errorSizeLevel1");
			String tmpErrorSizeLevel2 = AppConfig.getInstance().getParameterConfig()
					.getParameter("flowController.errorSizeLevel2");
			String tmpErrorSizeLevel3 = AppConfig.getInstance().getParameterConfig()
					.getParameter("flowController.errorSizeLevel3");
			ErrorSizeLevel.errorValue = tmpErrorValue;
			ErrorSizeLevel.errorSizeLevel1 = Integer.valueOf(tmpErrorSizeLevel1);
			ErrorSizeLevel.errorSizeLevel2 = Integer.valueOf(tmpErrorSizeLevel2);
			ErrorSizeLevel.errorSizeLevel3 = Integer.valueOf(tmpErrorSizeLevel3);

			String tmpNeedSenderControlQueues = AppConfig.getInstance().getParameterConfig()
					.getParameter("flowController.needSenderControlQueues");
			FlowControlParameters.needSenderControlQueues = tmpNeedSenderControlQueues;

			String tmpNeedSenderControlErrors = AppConfig.getInstance().getParameterConfig()
					.getParameter("flowController.needSenderControlErrors");
			FlowControlParameters.needSenderControlErrors = tmpNeedSenderControlErrors;

			IFlowControlServices fcs = new FlowControlServicesImpl();

			// 流量控制代码-剩余队列
			// FCSMessage queueFCSMsg = fcs.senderControlService(jmxServiceURL,
			// objectName, queue.get("name"));
			FCSMessage queueFCSMsg = fcs.senderControlService(queue.get("name"));

			// 流量控制代码-出错统计
			fcs.setErrorQueue(queue.get("name"), statusQueue);
			FCSMessage errorFCSMsg = fcs.senderControlServiceByErrors(queue.get("name"));

			if (null != queueFCSMsg && !queueFCSMsg.getFcsResult()) {
				status = "00901";
				message = "消息发送失败：队列【" + queueFCSMsg.getQueneName() + "】剩余消息数量【" + queueFCSMsg.getQueueSize()
						+ "】个，已达到消息服务器【" + queueFCSMsg.getLevel() + "】级阀值";
				AppLogger.getInstance().infoLog("SunlightMQManager:" + message);
			} else if (null != errorFCSMsg && !errorFCSMsg.getFcsResult()) {
				status = "00902";
				message = "消息发送失败：队列【" + errorFCSMsg.getQueneName() + "】的消息处理错误数为【" + errorFCSMsg.getErrorSize() + "】个，"
						+ "已达到消息服务器【" + errorFCSMsg.getLevel() + "】级错误阀值";
				AppLogger.getInstance().infoLog("SunlightMQManager:" + message);
			} else {
				Long timeToLive = Long
						.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("TIMETOLIVESIZE"));

				if (null != queue.get("timeToLive") && !"".equals(queue.get("timeToLive").trim())) {
					timeToLive = Long.valueOf(queue.get("timeToLive"));
				}

				if (null != getPara("timeToLive") && !"".equals(getPara("timeToLive").trim())) {
					timeToLive = Long.valueOf(getPara("timeToLive"));
				}

				if (null == timeToLive || 0 >= timeToLive) {
					timeToLive = (long) 86400000;
				}

				String response = JMSSendder.sendMsg(mqServer.get("url"), mqServer.get("userName"),
						mqServer.get("password"), queue.get("name"), queue.get("queueType").equals("topic"),
						queue.get("persistentFlag").equals("1"), Integer.parseInt(queue.get("acknowledTypeID")),
						sendMessage, syncFlag, timeToLive);

				AppLogger.getInstance().infoLog("APIService response: " + response);

				Map<String, Object> map = JSON.parseObject(response, Map.class);

				if (null != map && null != map.get("status")) {
					status = map.get("status").toString();
				} else {
					status = "non-standard access";
				}

				if (null != map && null != map.get("message")) {
					message = map.get("message").toString();
				} else {
					message = "non-standard access";
				}

				if (null != map && null != map.get("messageJson")) {
					messageJson = map.get("messageJson").toString();
				} else if (syncFlag) {
					messageJson = response;
				}

				// 流量控制功能-出错统计
				if (null != status && null != statusQueue) {
					statusQueue.enqueue(status);
				}
			}
		}
	}
	
	private String getBody() {
		InputStream is = null;
		String contentStr = "";
		try {
			is = getRequest().getInputStream();
			contentStr = IOUtils.toString(is, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contentStr;
	}
}
