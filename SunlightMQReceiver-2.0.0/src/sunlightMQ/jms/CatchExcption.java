package sunlightMQ.jms;

import java.sql.Connection;
import java.util.Hashtable;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import SunlightFrame.config.AppConfig;
import SunlightFrame.database.DBConnectionPool;
import SunlightFrame.database.DBProxy;
import SunlightFrame.database.IndexGenerater;
import SunlightFrame.log.AppLogger;
import sunlightMQ.AppKeys;
import sunlightMQ.bean.CatchExceptionBean;

public class CatchExcption {

	private static Boolean needDoInsert = Boolean
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("catchExcption.needDoInsert"));

	/**
	 * 处理接收MQ消息异常
	 * 
	 * @param bean
	 */
	public static void log2DB(CatchExceptionBean bean) {
		if (null != needDoInsert && needDoInsert) {
			doInsert(bean);// 插入数据库
		}
		// doCallService(bean);//异常回调接口
	}

	/**
	 * 插入数据库
	 * 
	 * @param bean
	 */
	private static void doInsert(CatchExceptionBean bean) {
		Connection con = null;
		try {
			con = DBConnectionPool.getInstance().getConnection();
			Hashtable<String, String> values = new Hashtable<String, String>();
			values.put("processExceptionLogID", IndexGenerater.getTableIndex("processExceptionLog", con));
			values.put("msgCenterName", bean.getMsgCenterName() != null ? bean.getMsgCenterName() : "");
			values.put("msgProcessorName", bean.getMsgProcessorName() != null ? bean.getMsgProcessorName() : "");
			values.put("messageID", bean.getMessageID() != null ? bean.getMessageID() : "");
			values.put("msgProcessorIP", bean.getMsgProcessorIP() != null ? bean.getMsgProcessorIP() : "");
			values.put("messageJson", bean.getMessageJson() != null ? bean.getMessageJson() : "");
			values.put("exceptionContent", bean.getExceptionContent() != null ? bean.getExceptionContent() : "");
			values.put("exceptionType", bean.getExceptionType() != null ? bean.getExceptionType() : "");
			values.put("exceptionAddress", AppKeys.LOCAL_IP);
			values.put("logTime", DateTimeUtil.getCurrentTime());
			DBProxy.insert(con, "processExceptionLog", values);
		} catch (Exception e) {
			AppLogger.getInstance().errorLog("loadData error", e);
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e2) {
			}
		}
	}

	/**
	 * 异常回调接口
	 * 
	 * @param bean
	 */
	@SuppressWarnings("unused")
	private static void doCallService(CatchExceptionBean bean) {
		HttpClient httpclient = null;
		try {
			httpclient = new DefaultHttpClient();
			if (bean.getProcessExceptionReportModeID() != null && bean.getProcessExceptionReportModeID().equals("1")) {// 异常回调接口
				HttpGet getMethod = new HttpGet(bean.getProcessExceptionUrl());
				httpclient.execute(getMethod);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpclient != null) {
				httpclient.getConnectionManager().shutdown();
			}
		}
	}
}
