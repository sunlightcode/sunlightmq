package sunlightMQ.processor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;

import SunlightFrame.config.AppConfig;
import SunlightFrame.config.Module;
import SunlightFrame.database.DBConnectionPool;
import SunlightFrame.database.DBProxy;
import SunlightFrame.web.DataHandle;

public class QueueStateProcessor extends BaseProcessor {

	private static Boolean refreshRunning = Boolean.FALSE;
	private static Connection con = null;
	private static JMXConnector connector = null;

	public QueueStateProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	public void makeView() throws Exception {
		String action = getFormData("action");
		if (action.equals("list")) {
			initPageByQueryDataList("queueState", getFormDatas(), "datas", "", new Vector<String>(),
					" order by queueSize desc, enqueueCount desc");
		}
	}

	public void defaultViewAction() throws Exception {
		listAction();
	}

	public void refreshViewAction() {
		try {
			if (refreshRunning) {
				System.out.println("重复操作，队列状态正在刷新！");
				listAction();
			} else {
				refreshRunning = Boolean.TRUE;

				if (null == con) {
					con = DBConnectionPool.getInstance().getConnection();
				}

				if (null == connector) {
					String jmxServiceURL = AppConfig.getInstance().getParameterConfig()
							.getParameter("flowController.jmxServiceURL");
					JMXServiceURL url = new JMXServiceURL(jmxServiceURL);// "service:jmx:rmi:///jndi/rmi://localhost:2011/jmxrmi"
					connector = JMXConnectorFactory.connect(url, null);
					connector.connect();
				}

				Hashtable<String, String> deleteAll = new Hashtable<String, String>();
				DBProxy.delete(con, "queueState", deleteAll);

				MBeanServerConnection connection = connector.getMBeanServerConnection();
				String objectName = AppConfig.getInstance().getParameterConfig()
						.getParameter("flowController.objectName");
				// 需要注意的是，这里的my-broker必须和配置的名称相同
				ObjectName name = new ObjectName(objectName);// "my-broker:brokerName=localhost,type=Broker"
				BrokerViewMBean mBean = (BrokerViewMBean) MBeanServerInvocationHandler.newProxyInstance(connection,
						name, BrokerViewMBean.class, true);
				// System.out.println(mBean.getBrokerName());
				for (ObjectName queueName : mBean.getQueues()) {
					QueueViewMBean queueMBean = (QueueViewMBean) MBeanServerInvocationHandler
							.newProxyInstance(connection, queueName, QueueViewMBean.class, true);

					// if (!queueMBean.getName().contains("ActiveMQ.DLQ")
					// && !queueMBean.getName().contains("STATUS_QUEUE")) {
					if (!queueMBean.getName().contains("STATUS_QUEUE")) {
						Hashtable<String, String> data = new Hashtable<String, String>();
						data.put("queueName", queueMBean.getName());
						data.put("memoryLimit", queueMBean.getMemoryLimit() + "");
						data.put("memoryUsagePortion", queueMBean.getMemoryUsagePortion() + "");
						data.put("memoryPercentUsage", queueMBean.getMemoryPercentUsage() + "");
						data.put("averageEnqueueTime", queueMBean.getAverageEnqueueTime() + "");
						data.put("producerCount", queueMBean.getProducerCount() + "");
						data.put("consumerCount", queueMBean.getConsumerCount() + "");
						data.put("enqueueCount", queueMBean.getEnqueueCount() + "");
						data.put("dequeueCount", queueMBean.getDequeueCount() + "");
						data.put("queueSize", queueMBean.getQueueSize() + "");
						DBProxy.insert(con, "queueState", data);
					}
				}

				refreshRunning = Boolean.FALSE;

				// if (con != null) {
				// con.close();
				// con = null;
				// }
				//
				// if (connector != null) {
				// connector.close();
				// connector = null;
				// }

				listAction();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if (con != null) {
				try {
					con.close();
					con = null;
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (connector != null) {
				try {
					connector.close();
					connector = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		} finally {
			// if (con != null) {
			// try {
			// con.close();
			// con = null;
			// } catch (SQLException e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// }
			// }
			// if (connector != null) {
			// try {
			// connector.close();
			// connector = null;
			// } catch (IOException e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// }
			// }
		}

	}

	/*
	 * public void makeView() throws Exception { String action =
	 * getFormData("action"); if (action.equals("list")) { int pageIndex = 1;
	 * try { pageIndex = Integer.parseInt(getFormData("pageIndex")); if
	 * (pageIndex < 1) { pageIndex = 1; } } catch (Exception e) { pageIndex = 1;
	 * } setFormData("pageIndex", String.valueOf(pageIndex));
	 * 
	 * String jmxServiceURL = AppConfig.getInstance().getParameterConfig()
	 * .getParameter("flowController.jmxServiceURL"); String objectName =
	 * AppConfig.getInstance().getParameterConfig().getParameter(
	 * "flowController.objectName");
	 * 
	 * JMXServiceURL url = new JMXServiceURL(jmxServiceURL);//
	 * "service:jmx:rmi:///jndi/rmi://localhost:2011/jmxrmi" JMXConnector
	 * connector = JMXConnectorFactory.connect(url, null); connector.connect();
	 * MBeanServerConnection connection = connector.getMBeanServerConnection();
	 * // 需要注意的是，这里的my-broker必须和配置的名称相同 ObjectName name = new
	 * ObjectName(objectName);// "my-broker:brokerName=localhost,type=Broker"
	 * BrokerViewMBean mBean = (BrokerViewMBean)
	 * MBeanServerInvocationHandler.newProxyInstance(connection, name,
	 * BrokerViewMBean.class, true); //
	 * System.out.println(mBean.getBrokerName()); Vector<Hashtable<String,
	 * String>> datas = new Vector<Hashtable<String, String>>(); for (ObjectName
	 * queueName : mBean.getQueues()) { QueueViewMBean queueMBean =
	 * (QueueViewMBean)
	 * MBeanServerInvocationHandler.newProxyInstance(connection, queueName,
	 * QueueViewMBean.class, true); // 消息队列名称 // System.out.println("消息队列名称:" +
	 * queueMBean.getName()); // 消费者数 // System.out.println("消费者数:" +
	 * queueMBean.getConsumerCount()); // 入队数 // System.out.println("入队消息数:" +
	 * queueMBean.getEnqueueCount()); // 出队数 // System.out.println("出队消费数:" +
	 * queueMBean.getDequeueCount()); // 队列中剩余的消息数 //
	 * System.out.println("队列中剩余的消息数:" + queueMBean.getQueueSize());
	 * 
	 * Hashtable<String, String> data = new Hashtable<String, String>();
	 * data.put("queueName", queueMBean.getName()); data.put("memoryLimit",
	 * queueMBean.getMemoryLimit() + ""); data.put("memoryUsagePortion",
	 * queueMBean.getMemoryUsagePortion() + ""); data.put("memoryPercentUsage",
	 * queueMBean.getMemoryPercentUsage() + ""); data.put("averageEnqueueTime",
	 * queueMBean.getAverageEnqueueTime() + ""); // data.put("producerCount",
	 * queueMBean.getProducerCount() + ""); // data.put("consumerCount",
	 * queueMBean.getConsumerCount() + ""); data.put("enqueueCount",
	 * queueMBean.getEnqueueCount() + ""); data.put("dequeueCount",
	 * queueMBean.getDequeueCount() + ""); data.put("queueSize",
	 * queueMBean.getQueueSize() + "");
	 * 
	 * datas.add(data);
	 * 
	 * }
	 * 
	 * int count = datas.size(); int pageNumber = datas.size();
	 * 
	 * setJSPData("datas", datas); setJumpPageInfo(count, pageNumber);
	 * 
	 * } }
	 */
}
