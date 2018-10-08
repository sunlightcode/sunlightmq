package SunlightMQ.service;

import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;

import SunlightFrame.config.AppConfig;
import SunlightFrame.log.AppLogger;

import com.alibaba.fastjson.JSON;

import SunlightMQ.DataCache;

public class BaseService extends Service {
	
	private static Boolean defaultValueEnabled = Boolean
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("defaultValue.enabled"));
	
	private static String defaultValueAppKey = AppConfig.getInstance().getParameterConfig()
			.getParameter("defaultValue.appKey");
	
	public BaseService(String className, String businessName, Hashtable<String, String> paras,
			Hashtable<String, String> sesstionKey, HttpServletRequest request) {
		super(className, businessName, paras, sesstionKey, request);
	}

	public boolean checkProtiry() {
		String appKey  = getPara("appKey");
		try {
			if (("".equals(appKey) || null == appKey) && defaultValueEnabled) {
				appKey = defaultValueAppKey;
			}
			
			Hashtable<String, String> client = DataCache.getInstance().getClient(appKey);
			if (!client.get("validFlag").equals("1")) {
				Hashtable<String, String> sessionErrorMsg = new Hashtable<String, String>();
				sessionErrorMsg.put("code", "00010");
				sessionErrorMsg.put("msg", "无效的账号");
				getRequest().setAttribute("responseObj", JSON.toJSONString(sessionErrorMsg));
				return false;
			}

			if (!checkMethodProtiry(client)) {
				Hashtable<String, String> sessionErrorMsg = new Hashtable<String, String>();
				sessionErrorMsg.put("code", "00009");
				sessionErrorMsg.put("msg", "权限不足");
				getRequest().setAttribute("responseObj", JSON.toJSONString(sessionErrorMsg));
				return false;
			}

			return true;
		} catch (Exception e) {
			AppLogger.getInstance().errorLog("AppKey验证失败：" + getPara("appKey"));
			return false;
		}
	}

	private boolean checkMethodProtiry(Hashtable<String, String> client) {
		String methodIDs = client.get("methodIDs");
		String methodName = getMethodName();
		System.out.println(methodName);
		String methodNames = DataCache.getInstance().getTableDataColumnsValue("c_method", methodIDs, "c_methodName");
		System.out.println(methodNames);
		if (("," + methodNames + ",").indexOf("," + methodName + ",") == -1) {
			return false;
		}
		return true;
	}

	public void endProcess() throws Exception {
	}

	public int getTotalPageCount(int dataCount, int pageSize) {
		return (dataCount / pageSize) + (dataCount % pageSize == 0 ? 0 : 1);
	}

}
