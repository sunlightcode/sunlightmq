package SunlightMQ.processor;

import java.util.Hashtable;
import java.util.Iterator;

import SunlightMQ.AppKeys;
import SunlightMQ.DataCache;
import SunlightMQ.service.Service;
import SunlightMQ.service.ServiceFactory;
import SunlightMQ.tool.CatchExceptionBean;
import SunlightMQ.tool.CatchExcption;
import SunlightFrame.config.Module;
import SunlightFrame.log.AppLogger;
import SunlightFrame.util.MD5Util;
import SunlightFrame.web.DataHandle;

public class RouterProcessor extends BaseProcessor {
	public RouterProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	@Override
	public void makeView() throws Exception {
	}

	public void restAction() throws Exception {
		Hashtable<String, String> paras = getFormDatas();
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

		String appKey = getFormData("appKey");

		if (isParaEmpty(appKey)) {
			sysErrorMsg.put("code", "00001");
			sysErrorMsg.put("msg", "appKey为空");
			return sysErrorMsg;
		}

		String method = getFormData("businessName");
		if (!checkMethod(method)) {
			sysErrorMsg.put("code", "00004");
			sysErrorMsg.put("msg", "方法名格式不正确");
			return sysErrorMsg;
		}

		String privateKey = getPrivateKey(appKey);
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
				md5ErrorMsg.put("msg", "数据验密不通过");
				return md5ErrorMsg;
			}
		}
		return md5ErrorMsg;
	}
}
