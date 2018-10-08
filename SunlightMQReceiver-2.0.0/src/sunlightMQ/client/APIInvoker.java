package sunlightMQ.client;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import SunlightFrame.config.AppConfig;
import sunlightMQ.AppKeys;
import sunlightMQ.DataCache;
import sunlightMQ.Result;
import sunlightMQ.bean.CatchExceptionBean;
import sunlightMQ.jms.ZipUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class APIInvoker {
	private String url;// 发送目标URL
	private String messageJson;// 消息体
	private String md5;// 加密字符

	private String appKey;// appKey
	private String businessName;// businessName
	private String syncFlag;// syncFlag
	private String sendMessage;// 发送跨中心数据

	private static Boolean msgGZip = Boolean
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("MSG_GZIP"));

	private static Boolean needCheckMD5Paras = Boolean
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("NEED_CHECKMD5PARAS"));

	public APIInvoker(String url, String sendMessage, boolean toOtherCenterFlag) {
		Map<String, String> sendMessageParam = JSON.parseObject(sendMessage, new TypeReference<Map<String, String>>() {
		});
		this.url = url;
		this.sendMessage = sendMessage;

		if (!toOtherCenterFlag && null != msgGZip && Boolean.TRUE == msgGZip) {
			this.messageJson = ZipUtils.gunzip(sendMessageParam.get("messageJson"));
		} else {
			this.messageJson = sendMessageParam.get("messageJson");
		}

		this.md5 = sendMessageParam.get("md5");

		this.appKey = sendMessageParam.get("appKey");
		this.businessName = sendMessageParam.get("businessName");
		this.syncFlag = sendMessageParam.get("syncFlag");
		// AppLogger.getInstance().infoLog("SunlightMQManager:待处理消息信息：【"
		// + "businessName" + businessName
		// + "sync：" + syncFlag
		// + "】");
	}

	public String invokUrl(CatchExceptionBean bean) throws Exception {
		Hashtable<String, String> msgProcessor = DataCache.getInstance().getMsgProcessor(bean.getMsgProcessorName());
		if (msgProcessor.get("toOtherCenter").equals("0")) { // 非跨中心队列
			if (msgProcessor.get("remoteSendQueue").equals("1")) {// 混合队列 重新解析参数
																	// 发送本地sender
				HashMap<String, String> requestParas = new HashMap<String, String>();
				requestParas.put("businessName", businessName);
				requestParas.put("syncFlag", syncFlag);
				requestParas.put("appKey", appKey);
				requestParas.put("messageJson", URLEncoder.encode(messageJson, "utf-8"));
				return MyHttpClient.postDataSelf(makeJson(requestParas),
						((url.startsWith("http") || url.startsWith("https")) ? "" : "http://") + url, bean);
			} else { // 业务队列
				if (msgProcessor.get("callRequestTypeID").equals(AppKeys.REQUESTTYPE_GET)) {// get方式发送数据
					HashMap<String, String> requestParas = new HashMap<String, String>();
					requestParas.put("messageJson", URLEncoder.encode(messageJson, "utf-8"));
					requestParas.put("md5", md5);
					return MyHttpClient.postData(requestParas,
							((url.startsWith("http") || url.startsWith("https")) ? "" : "http://") + url, bean);
				} else if (msgProcessor.get("callRequestTypeID").equals(AppKeys.REQUESTTYPE_POST)) {// post方式发送数据
					if (DataCache.getInstance().askB2c(bean.getMsgProcessorName())) {// 指向B2C
						HashMap<String, String> requestParas = new HashMap<String, String>();
						requestParas.put("messageJson", URLEncoder.encode(messageJson, "utf-8"));
						requestParas.put("md5", md5);
						return MyHttpClient.postB2CData(makeJson(requestParas),
								((url.startsWith("http") || url.startsWith("https")) ? "" : "http://") + url, bean);
					} else {
						if (!needCheckMD5Paras) {
							return MyHttpClient.postJsonData(messageJson,
									((url.startsWith("http") || url.startsWith("https")) ? "" : "http://") + url, bean);
						}

						if (md5 != null && !md5.equals("")) {// 如果需要加密 参数结构不一样
							Map<String, String> sendMssageJson = new HashMap<String, String>();
							sendMssageJson.put("messageJson", messageJson);
							sendMssageJson.put("md5", md5);
							return MyHttpClient.postJsonData(JSON.toJSONString(sendMssageJson),
									((url.startsWith("http") || url.startsWith("https")) ? "" : "http://") + url, bean);
						} else {
							return MyHttpClient.postJsonData(messageJson,
									((url.startsWith("http") || url.startsWith("https")) ? "" : "http://") + url, bean);
						}
					}
				} else {
					return new Result("00100", "不支持该种请求方式", null).toJSON();
				}
			}
		} else {// 跨中心队列，路由转发数据
			return MyHttpClient.postRouteData(url, sendMessage, syncFlag, msgProcessor.get("otherCenterUrl"), bean);
		}
	}

	/**
	 * 参数构造
	 * 
	 * @param requestParas
	 * @return
	 */
	private String makeJson(Map<String, String> requestParas) {
		Iterator<String> itor = requestParas.keySet().iterator();
		StringBuffer sbf = new StringBuffer();
		while (itor.hasNext()) {
			String key = itor.next();
			sbf.append("&");
			sbf.append(key);
			sbf.append("=");
			sbf.append(requestParas.get(key));
		}
		return sbf.toString().replaceFirst("&", "");
	}
}
