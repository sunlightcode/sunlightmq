package sunlightMQ.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ConnectionManager {
	public static Connection getConnection(String userName, String password, String url) throws Exception {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(userName, password, url);

		Connection connection = connectionFactory.createConnection();
		return connection;
	}
}
