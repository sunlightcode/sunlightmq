package SunlightMQ.jms;

import javax.jms.Connection;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;

import SunlightFrame.log.AppLogger;

public class ConnectionManager {
	private static PooledConnectionFactory pooledConnectionFactory;
	private static ActiveMQConnectionFactory connectionFactory;

	public static Connection getConnection(String username, String password, String url) throws Exception {
		if (connectionFactory == null) {
			connectionFactory = new ActiveMQConnectionFactory(username, password, url);
			connectionFactory.setWatchTopicAdvisories(false);
			connectionFactory.setProducerWindowSize(1024000);
		}

		Connection con = null;
		try {
			AppLogger.getInstance().infoLog("获取MQ连接：" + url);
			con = connectionFactory.createConnection();
			AppLogger.getInstance().infoLog("连接获取成功！");
		} catch (Exception e) {
			e.printStackTrace();
			AppLogger.getInstance().infoLog("获取MQ连接失败：" + url + " with exception:" + e.getMessage());
		}

		return con;
	}

	public static Connection getPoolConnection(String username, String password, String url) throws Exception {
		if (pooledConnectionFactory == null) {
			ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(username, password, url);
			factory.setWatchTopicAdvisories(false);
			pooledConnectionFactory = new PooledConnectionFactory(factory);
			pooledConnectionFactory.setMaxConnections(100);
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
		Connection con = ConnectionManager.getPoolConnection("", "",
				"failover:(tcp://192.168.3.218:61616,tcp://192.168.3.219:61616)");
		con.start();
		System.out.println("------------");

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
