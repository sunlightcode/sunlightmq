package SunlightMQ;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

import SunlightFrame.config.AppConfig;
import SunlightFrame.log.AppLogger;

public class CheckMQConnectionProcessorThread extends Thread {

	public static boolean isConnected = true;

	public void run() {
		while (true) {
			try {
				sleep(5 * 60 * 1000);
				autoCheckMQConnectionStatus();
			} catch (Exception e) {
				AppLogger.getInstance().errorLog("Thread run error", e);
			}
		}
	}

	public void autoCheckMQConnectionStatus() throws Exception {
		Connection connection = null;

		String mqServerName = AppConfig.getInstance().getParameterConfig().getParameter("mqServer.name");
		AppLogger.getInstance().infoLog("mqServer.name = " + mqServerName);
		Hashtable<String, String> mqServer = DataCache.getInstance().getMqServer(mqServerName);
		try {
			// AppLogger.getInstance().infoLog("---------------检测连接MQ--------------");
			connection = getConnection(mqServer.get("userName"), mqServer.get("password"), mqServer.get("url"));
			isConnected = true;
			// AppLogger.getInstance().infoLog("---------------检测连接MQ成功--------------");
		} catch (Exception e) {
			// 连接失败
			isConnected = false;
			AppLogger.getInstance().infoLog("---------------检测连接MQ失败--------------");
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * 获取MQ连接
	 * 
	 * @param userName
	 * @param password
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private Connection getConnection(String userName, String password, String url) throws Exception {

		Connection connection = null;
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(userName, password, url);
		connection = connectionFactory.createConnection();
		return connection;
	}
}
