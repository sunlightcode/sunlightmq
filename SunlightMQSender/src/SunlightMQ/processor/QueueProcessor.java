package SunlightMQ.processor;

import SunlightFrame.config.AppConfig;
import SunlightFrame.config.Module;
import SunlightFrame.log.AppLogger;
import SunlightFrame.util.MD5Util;
import SunlightFrame.web.DataHandle;
import SunlightMQ.AppKeys;
import SunlightMQ.DataCache;
import SunlightMQ.service.Service;
import SunlightMQ.service.ServiceFactory;
import SunlightMQ.tool.CatchExceptionBean;
import SunlightMQ.tool.CatchExcption;
//import SunlightMQ.tool.CompressUtil;

import java.util.Hashtable;
import java.util.Iterator;

public class QueueProcessor extends BaseProcessor {

	private static Boolean needCheckMD5Paras = Boolean
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("NEED_CHECKMD5PARAS"));

	private static Boolean defaultValueEnabled = Boolean
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("defaultValue.enabled"));
	
	private static String defaultValueBusinessName = AppConfig.getInstance().getParameterConfig()
			.getParameter("defaultValue.businessName");	

	// private static Boolean needUncompress = Boolean
	// .valueOf(AppConfig.getInstance().getParameterConfig().getParameter("compress.needUncompress"));
	//
	// private static String compressAppKey =
	// AppConfig.getInstance().getParameterConfig().getParameter("compress.appKey");

	public QueueProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	@Override
	public void makeView() throws Exception {
	}

	public void restAction() throws Exception {
		Hashtable<String, String> paras = getFormDatas();

		// if (needUncompress && compressAppKey.equals(getFormData("appKey"))) {
		// System.out.println("Uncompress: " +
		// CompressUtil.uncompress(getFormData("messageJso+n")) +
		// "================");
		// } else {
		// System.out.println(getFormData("messageJson") + "================");
		// }

		System.out.println(getFormData("messageJson") + "================");

		Hashtable<String, String> sysParas = new Hashtable<String, String>();
		Hashtable<String, String> appParas = new Hashtable<String, String>();

		Iterator<String> iter = paras.keySet().iterator();
		String key;
		String value;
		while (iter.hasNext()) {
			key = iter.next();
			value = paras.get(key);

			if (key.equals("appKey") || key.equals("businessName")) {
				sysParas.put(key, value);
			} else {
				appParas.put(key, value);
			}
		}

		Hashtable<String, String> sysErrorMsg = checkSysParas();
		Hashtable<String, String> md5ErrorMsg = checkMD5Paras();// 数据验密
		if (!sysErrorMsg.isEmpty()) {
			getRequest().setAttribute("responseObj", Service.getErrorJSONString(sysErrorMsg.get("msg")));
		}
		if (!md5ErrorMsg.isEmpty()) {
			getRequest().setAttribute("responseObj", Service.getErrorJSONString(md5ErrorMsg.get("msg")));
		} else {
			
			String businessName = sysParas.get("businessName");
			if ("".equals(businessName) || null == businessName) {
				businessName = defaultValueBusinessName;
			}
			
			Hashtable<String, String> sessionKey = getSessionKey();
			Service service = ServiceFactory.create("APIService", businessName, getFormDatas(), sessionKey,
					getRequest());
			if (service.checkProtiry()) {
				try {
					service.doAction();
					getRequest().setAttribute("responseObj", service.getJSONString());
				} catch (Exception e) {
					AppLogger.getInstance().errorLog("", e);
					getRequest().setAttribute("responseObj", Service.getErrorJSONString("99", "服务器内部错误"));
					CatchExceptionBean bean = new CatchExceptionBean();
					bean.setExceptionAddress("Sender");
					bean.setExceptionType("服务器内部错误");
					bean.setExceptionContent(e.getMessage());
					bean.setMessageJson(paras.get("messageJson"));
					String queueName = DataCache.getInstance().getMethod(paras.get("businessName")).get("c_queueName");
					Hashtable<String, String> queue = DataCache.getInstance().getQueue(queueName);
					bean.setProcessExceptionReportModeID(queue.get("processExceptionReportModeID"));
					bean.setProcessExceptionUrl(queue.get("processExceptionUrl"));
					CatchExcption.doAction(bean);
				}
			} else {
				AppLogger.getInstance().errorLog("业务权限验证不通过，业务名称：" + paras.get("businessName"));
				getRequest().setAttribute("responseObj", Service.getErrorJSONString("98", "无调用权限"));
			}
		}
	}

	public Hashtable<String, String> checkSysParas() throws Exception {
		Hashtable<String, String> sysErrorMsg = new Hashtable<String, String>();
		
		if (defaultValueEnabled) {
			return sysErrorMsg;
		}
		
		String appKey = getFormData("appKey");
		String method = getFormData("businessName");
		String privateKey = getPrivateKey(appKey);

		if (isParaEmpty(appKey)) {
			sysErrorMsg.put("code", "00001");
			sysErrorMsg.put("msg", "appKey为空");
			return sysErrorMsg;
		}

		if (!checkMethod(method)) {
			sysErrorMsg.put("code", "00004");
			sysErrorMsg.put("msg", "方法名格式不正确");
			return sysErrorMsg;
		}

		if (!checkPrivateKey(privateKey)) {
			sysErrorMsg.put("code", "00005");
			sysErrorMsg.put("msg", "无效的appKey");
			return sysErrorMsg;
		}
		return sysErrorMsg;
	}

	private boolean checkMethod(String method) {
		if (method == null || method.equals("")) {
			return false;
		}
		return true;
	}

	private boolean checkPrivateKey(String privateKey) throws Exception {
		return !privateKey.equals("");
	}

	private boolean isParaEmpty(String para) {
		return para.equals("");
	}

	/**
	 * 消息体验密
	 * 
	 * @return
	 * @throws Exception
	 */
	public Hashtable<String, String> checkMD5Paras() throws Exception {
		Hashtable<String, String> md5ErrorMsg = new Hashtable<String, String>();
		String md5 = getFormData("md5");
		if (md5 != null && !md5.equals("")) {// 如果接受到MD5加密信息则进行验密操作
			String checkMD5 = MD5Util.MD5(getFormData("messageJson") + AppKeys.MD5_KYE);
			if (!md5.equals(checkMD5)) {
				md5ErrorMsg.put("code", "00700");
				md5ErrorMsg.put("msg", "数据验密不通过, md5:" + md5 + "; checkMD5:" + checkMD5 + "; messageJson:"
						+ getFormData("messageJson"));
				AppLogger.getInstance().infoLog("数据验密不通过,md5:" + md5);
				AppLogger.getInstance().infoLog("数据验密不通过,checkMD5:" + checkMD5);
				AppLogger.getInstance().infoLog("数据验密不通过,messageJson:" + getFormData("messageJson"));
				return md5ErrorMsg;
			}
		} else if (needCheckMD5Paras && !defaultValueEnabled) {
			md5ErrorMsg.put("code", "00700");
			md5ErrorMsg.put("msg", "数据验密不通过, md5 is null");
			AppLogger.getInstance().infoLog("数据验密不通过,md5 is null");
			AppLogger.getInstance().infoLog("数据验密不通过,messageJson:" + getFormData("messageJson"));
			return md5ErrorMsg;
		}
		
		return md5ErrorMsg;
	}
}
