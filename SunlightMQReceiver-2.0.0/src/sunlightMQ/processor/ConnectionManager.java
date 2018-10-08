package sunlightMQ.processor;

import javax.jms.Connection;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;

import SunlightFrame.config.AppConfig;
import SunlightFrame.log.AppLogger;

public class ConnectionManager {
	private static PooledConnectionFactory pooledConnectionFactory;
	private static ActiveMQConnectionFactory connectionFactory;
	private static Integer maxConnections = Integer
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("jmsConnectionPool.maxConnections"));

	public static Connection getConnection(String username, String password, String url) throws Exception {
		if (connectionFactory == null) {
			connectionFactory = new ActiveMQConnectionFactory(username, password, url);
			connectionFactory.setWatchTopicAdvisories(false);
		}

		Connection con = null;
		try {
			AppLogger.getInstance().infoLog("获取MQ连接：" + url);
			con = connectionFactory.createConnection();
			AppLogger.getInstance().infoLog("连接获取成功！");
		} catch (Exception e) {
			AppLogger.getInstance().infoLog("获取MQ连接失败：" + url + " with exception:" + e.getMessage());
		}

		return con;
	}

	public static Connection getPoolConnection(String username, String password, String url) throws Exception {
		if (pooledConnectionFactory == null) {
			ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(username, password, url);
			factory.setWatchTopicAdvisories(false);
			pooledConnectionFactory = new PooledConnectionFactory(factory);
			pooledConnectionFactory.setMaxConnections(maxConnections);// 500
		}

		Connection con = null;
		try {
			AppLogger.getInstance().infoLog("获取MQ连接：" + url);
			con = pooledConnectionFactory.createConnection();
			AppLogger.getInstance().infoLog("连接获取成功！当前JMS连接数：" + pooledConnectionFactory.getNumConnections()
					+ ", 最大连接数：" + pooledConnectionFactory.getMaxConnections());
		} catch (Exception e) {
			AppLogger.getInstance().infoLog("获取MQ连接失败：" + url + " with exception:" + e.getMessage());
		}

		return con;

	}

	public static void main(String[] args) throws Exception {

		getConnection("", "", "failover:(tcp://172.16.226.57:61616,tcp://172.16.227.80:61616)maxReconnectAttempts=2");
	}

	/**
	 * 对象回收销毁时停止链接
	 */
	@Override
	protected void finalize() throws Throwable {
		pooledConnectionFactory.stop();
		super.finalize();
	}

}
