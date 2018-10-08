package sunlightMQ;

import SunlightFrame.config.AppConfig;
import SunlightFrame.database.DBProxy;
import SunlightFrame.log.AppLogger;
import flowController.core.FlowControllerServer;
import sunlightMQ.processor.MsgCenter;

public class Application {

	private static FlowControllerServer fcs = new FlowControllerServer();
	// 流量控制功能启动
	private static Boolean startJmxActiveMQ = Boolean.valueOf(
			AppConfig.getInstance().getParameterConfig().getParameter("flowController.startJmxActiveMQ"));

	public static void main(String[] args) {
		String appRoot = new Application().getClass().getClassLoader().getResource(".").getPath();
		appRoot = appRoot.substring(0, appRoot.indexOf("WEB-INF"));
		MsgCenter msgCenter = null;

		startReceiver(appRoot, msgCenter);
	}

	public static void startReceiver(String appRoot, MsgCenter msgCenter) {
		try {
			AppConfig.getInstance().init(appRoot);
			AppLogger.getInstance().init(appRoot);
			DBProxy.init();
			DataCache.getInstance().loadData();
			String msgCenterName = AppConfig.getInstance().getParameterConfig().getParameter("msgCenter.name");

			msgCenter = new MsgCenter(msgCenterName);

			msgCenter.createMsgProcessors();
			msgCenter.startListenCMDQueue();
			
			if (startJmxActiveMQ) {
				String jmxServiceURL = AppConfig.getInstance().getParameterConfig()
						.getParameter("flowController.jmxServiceURL");
				String objectName = AppConfig.getInstance().getParameterConfig().getParameter("flowController.objectName");
				Integer timeInterval = Integer
						.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("flowController.timeInterval"));
				AppLogger.getInstance().infoLog("SunlightMQManager: startJmxActiveMQ=" + startJmxActiveMQ + "; jmxServiceURL="
						+ jmxServiceURL + "; objectName=" + objectName + "; timeInterval=" + timeInterval);

				fcs.setStartJmxActiveMQ(startJmxActiveMQ);
				fcs.setJmxServiceURL(jmxServiceURL);
				fcs.setObjectName(objectName);
				fcs.setTimeInterval(timeInterval);
				fcs.startServer();
				AppLogger.getInstance().infoLog("SunlightMQManager: FlowControllerServer is running!");				
			}
		} catch (Exception e) {
			AppLogger.getInstance().errorLog("程启动器失败", e);
			if (msgCenter != null) {
				try {
					msgCenter.stopAllMsgProcessor();
				} catch (Exception e2) {
					AppLogger.getInstance().errorLog("停止消息处理器失败", e2);
				}
			}
			AppLogger.getInstance().errorLog("程序启动失败", e);
		}
	}

	public static void stopReceiver(MsgCenter msgCenter) {
		if (msgCenter != null) {
			try {
				msgCenter.stopAllMsgProcessor();
				// 流量控制功能停止
				if (startJmxActiveMQ) {
					fcs.stopServer();
					AppLogger.getInstance().infoLog("SunlightMQManager: FlowControllerServer is stop!");
				}
			} catch (Exception e2) {
				AppLogger.getInstance().errorLog("停止消息处理器失败", e2);
			}
		}
	}
}
