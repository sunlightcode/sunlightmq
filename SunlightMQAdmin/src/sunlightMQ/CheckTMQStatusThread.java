package sunlightMQ;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import SunlightFrame.config.AppConfig;
import SunlightFrame.log.AppLogger;
import net.sf.json.JSONObject;
import sunlightMQ.tool.BrandwisdomClient;

public class CheckTMQStatusThread extends Thread {
	private static CheckTMQStatusThread instance = new CheckTMQStatusThread();
	private static boolean isRunning = true;

	private static Boolean isDebugMode = Boolean
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("checkTMQStatus.isDebugMode"));
	// "internal-b2c3-mq-ire-1063896040.eu-west-1.elb.amazonaws.com:8080/router";
	private static String url = AppConfig.getInstance().getParameterConfig().getParameter("checkTMQStatus.url");
	// "TMQSTATUSTEST"
	private static String messageJson = AppConfig.getInstance().getParameterConfig()
			.getParameter("checkTMQStatus.messageJson");
	// "en_groupordernew"
	private static String businessName = AppConfig.getInstance().getParameterConfig()
			.getParameter("checkTMQStatus.businessName");
	// "0"
	private static String syncFlag = AppConfig.getInstance().getParameterConfig()
			.getParameter("checkTMQStatus.syncFlag");
	// "1020M9RJ4R2D9A9Z"
	private static String appKey = AppConfig.getInstance().getParameterConfig().getParameter("checkTMQStatus.appKey");
	// "./sendSMS.sh"
	private static String warningShell = AppConfig.getInstance().getParameterConfig()
			.getParameter("checkTMQStatus.warningShell");
	// "./sendSMS.sh"
	private static String warningShellPath = AppConfig.getInstance().getParameterConfig()
			.getParameter("checkTMQStatus.warningShellPath");
	// "Tiens TMQ System Error! URL:"
	private static String warningMessage = AppConfig.getInstance().getParameterConfig()
			.getParameter("checkTMQStatus.warningMessage") + url;
	// "00505,-1,1,98"
	private static String statusCodes = AppConfig.getInstance().getParameterConfig()
			.getParameter("checkTMQStatus.statusCodes");
	// "13802123481,13802123481,13802123481"
	private static String warningNumberString = AppConfig.getInstance().getParameterConfig()
			.getParameter("checkTMQStatus.warningNumberString");

	private static Boolean needCheckTMQStatus = Boolean
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("checkTMQStatus.needCheckTMQStatus"));
	
	private static String shellString = "";
	
	private CheckTMQStatusThread() {
	}

	public static CheckTMQStatusThread getInstance() {
		return instance;
	}

	public void stopRunning() {
		isRunning = false;
	}

	public void beginRunning() {
		isRunning = true;
	}

	public void run() {
		while (isRunning && needCheckTMQStatus) {
			try {
				String timeInterval = AppConfig.getInstance().getParameterConfig()
						.getParameter("checkTMQStatus.timeInterval");
				sleep(Integer.valueOf(timeInterval));
				AppLogger.getInstance().infoLog("CheckTMQStatusThread is Running");
				if (!autoCheckTMQStatusThread()) {
					String warnNum[] = warningNumberString.split(",");
					for (int i = 0; i < warnNum.length; i++) {
						shellString = warningShell + " \"" + warnNum[i] + "\" \"\" \"" + warningMessage + "\"";
						callShell(warnNum[i], "", warningMessage);
					}
					stopRunning();
				}
			} catch (Exception e) {
				AppLogger.getInstance().errorLog("CheckTMQStatusThread run error", e);
			}
		}
	}

	public boolean autoCheckTMQStatusThread() {
		HashMap<String, String> requestParas = new HashMap<String, String>();
		requestParas.put("businessName", businessName);
		requestParas.put("syncFlag", syncFlag);
		requestParas.put("appKey", appKey);
		try {
			requestParas.put("messageJson", URLEncoder.encode(messageJson, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			AppLogger.getInstance().errorLog("autoCheckTMQStatusThread error: " + e);
			return false;
		}
		requestParas.put("md5", "");
		String result = BrandwisdomClient.callService1(makeJson(requestParas), url);
		JSONObject syncResultJO = JSONObject.fromObject(result);
		String status = syncResultJO.getString("status");
		AppLogger.getInstance().infoLog("TMQ STATUS = " + status);
		if (!statusCodes.contains(status) || isDebugMode) {
			return false;
		}
		return true;
	}

	private static String makeJson(Map<String, String> requestParas) {
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

	@SuppressWarnings("unused")
	private static void callShell() {
		if (isDebugMode) {
			AppLogger.getInstance().infoLog("call shell isDebugMode");
			AppLogger.getInstance().infoLog("shellString=" + shellString);
			AppLogger.getInstance().infoLog("call shell success.");
			return;
		}

		try {
			AppLogger.getInstance().infoLog("shellString=" + shellString);
			Process process = Runtime.getRuntime().exec(shellString);
			int exitValue = process.waitFor();
			if (0 != exitValue) {
				AppLogger.getInstance().errorLog("call shell failed. error code is :" + exitValue);
			} else {
				AppLogger.getInstance().infoLog("call shell success.");
			}
		} catch (Throwable e) {
			AppLogger.getInstance().errorLog("call shell failed. " + e);
		}
	}

	private static void callShell(String param1, String param2, String param3) {
		if (isDebugMode) {
			AppLogger.getInstance().infoLog("call shell isDebugMode");
			AppLogger.getInstance().infoLog("shellString=" + shellString);
			AppLogger.getInstance().infoLog("call shell success.");
			return;
		}
		
		AppLogger.getInstance().infoLog("shellString=" + shellString);
		ProcessBuilder pb = new ProcessBuilder("./" + warningShell, param1, param2, param3);
		pb.directory(new File(warningShellPath));
		int runningStatus = 0;
		try {
			Process p = pb.start();
			try {
				runningStatus = p.waitFor();
			} catch (InterruptedException e) {
				AppLogger.getInstance().errorLog("call shell failed. " + e);
			}
		} catch (IOException e) {
			AppLogger.getInstance().errorLog("call shell failed. " + e);
		}
		if (runningStatus != 0) {
			AppLogger.getInstance().errorLog("call shell failed. error code is :" + runningStatus);
		}
	}
}
