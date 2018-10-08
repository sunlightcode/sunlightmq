package sunlightMQ;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import SunlightFrame.config.AppConfig;
import SunlightFrame.database.DBConnectionPool;
import SunlightFrame.database.DBProxy;
import SunlightFrame.log.AppLogger;
import sunlightMQ.jms.CheckMsgProcessorStatusJMSSendder;

public class CheckMsgProcessorThread extends Thread {
	private static CheckMsgProcessorThread instance = new CheckMsgProcessorThread();
	private static boolean isRunning = true;
	private static Boolean needCheckProcessor = Boolean
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("checkMsgProcessor.needCheckProcessor"));


	private CheckMsgProcessorThread() {
	}

	public static CheckMsgProcessorThread getInstance() {
		return instance;
	}

	public void run() {
		while (isRunning && needCheckProcessor) {
			try {
				String timeInterval = AppConfig.getInstance().getParameterConfig()
						.getParameter("checkMsgProcessor.timeInterval");
				sleep(Integer.valueOf(timeInterval));
				AppLogger.getInstance().infoLog("CheckMsgProcessorThread is Running");
				autoCheckMsgProcessorsStatus();
			} catch (Exception e) {
				AppLogger.getInstance().errorLog("CheckMsgProcessorThread run error", e);
			}
		}
	}

	public void autoCheckMsgProcessorsStatus() throws Exception {
		Connection con = null;
		try {
			con = DBConnectionPool.getInstance().getConnection();

			Hashtable<String, String> k = new Hashtable<String, String>();
			k.put("validFlag", "1");
			Vector<Hashtable<String, String>> msgProcessors = DBProxy.query(con, "msgProcessor_V", k);
			for (int i = 0; i < msgProcessors.size(); ++i) {
				Hashtable<String, String> msgProcessor = msgProcessors.get(i);
				String checkResult = CheckMsgProcessorStatusJMSSendder.sendMsg(msgProcessor.get("url"),
						msgProcessor.get("userName"), msgProcessor.get("password"), AppKeys.STATUS_QUEUE, false, false,
						AppKeys.ACK_AUTOACKNOWLEDGE, "get status", true);

				Hashtable<String, String> msgProcessorK = new Hashtable<String, String>();
				msgProcessorK.put("msgProcessorID", msgProcessor.get("msgProcessorID"));
				Hashtable<String, String> msgProcessorV = new Hashtable<String, String>();
				msgProcessorV.put("status", checkResult.equals("running") ? "1" : "0");
				DBProxy.update(con, "msgProcessor", msgProcessorK, msgProcessorV);
			}

		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e2) {
			}
		}
	}

	public void stopRunning() {
		isRunning = false;
	}

	public void beginRunning() {
		isRunning = true;
	}

}
