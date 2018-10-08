package sunlightMQ.processor;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import SunlightFrame.database.DBConnectionPool;
import SunlightFrame.database.DBProxy;
import SunlightFrame.log.AppLogger;
import sunlightMQ.AppKeys;
import sunlightMQ.bean.CatchExceptionBean;
import sunlightMQ.jms.CmdMessageListener;

public class MsgCenter {
	private String msgCenterName;
	private Hashtable<String, String> msgCenterHash;
	private Hashtable<String, MsgProcessor> msgProcessors;

	public MsgCenter(String msgCenterName) throws Exception {
		this.msgCenterName = msgCenterName;
		CatchExceptionBean bean = new CatchExceptionBean();
		bean.setMsgCenterName(msgCenterName);
		init();
	}

	private void init() throws Exception {
		java.sql.Connection con = null;
		try {
			con = DBConnectionPool.getInstance().getConnection();

			Hashtable<String, String> k = new Hashtable<String, String>();
			k.put("name", msgCenterName);
			k.put("validFlag", "1");

			Vector<Hashtable<String, String>> msgCenters = DBProxy.query(con, "msgCenter_V", k);
			if (msgCenters.size() == 0) {
				throw new Exception("消息中心[" + msgCenterName + "]初始化失败，找不到该消息中心");
			} else {
				msgCenterHash = msgCenters.get(0);
			}

			AppLogger.getInstance().infoLog("消息中心[" + msgCenterName + "]初始化成功");
		} catch (Exception e) {
			throw new Exception("消息中心[" + msgCenterName + "]初始化失败", e);
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public void createMsgProcessors() throws Exception {
		msgProcessors = new Hashtable<String, MsgProcessor>();
		java.sql.Connection con = null;
		try {
			con = DBConnectionPool.getInstance().getConnection();

			Hashtable<String, String> msgProcessorK = new Hashtable<String, String>();
			msgProcessorK.put("msgCenterName", msgCenterName);
			msgProcessorK.put("validFlag", "1");
			Vector<Hashtable<String, String>> msgProcessorVector = DBProxy.query(con, "msgProcessor_V", msgProcessorK);
			for (int i = 0; i < msgProcessorVector.size(); ++i) {
				Hashtable<String, String> msgProcessorHash = msgProcessorVector.get(i);
				MsgProcessor msgProcessor = new MsgProcessor(msgCenterName, msgProcessorHash.get("msgProcessorID"));
				msgProcessors.put(msgProcessorHash.get("name"), msgProcessor);
			}
			AppLogger.getInstance().infoLog("消息中心[" + msgCenterName + "]创建消息处理器完成");

			for (int i = 0; i < msgProcessorVector.size(); ++i) {
				Hashtable<String, String> msgProcessorHash = msgProcessorVector.get(i);
				MsgProcessor msgProcessor = msgProcessors.get(msgProcessorHash.get("name"));
				if (msgProcessor.autoRunning) {
					msgProcessor.start();
				}
			}
		} catch (Exception e) {
			throw new Exception("消息中心[" + msgCenterName + "]创建处理器失败", e);
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public void startListenCMDQueue() throws Exception {
		javax.jms.Connection jmsConnection = null;
		Session session = null;
		Destination destination = null;
		MessageConsumer consumer = null;
		try {
			jmsConnection = ConnectionManager.getConnection(msgCenterHash.get("userName"),
					msgCenterHash.get("password"), msgCenterHash.get("url"));
			jmsConnection.start();
			session = jmsConnection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);

			destination = session.createTopic(AppKeys.CMD_QUEUE);
			consumer = session.createConsumer(destination);
			consumer.setMessageListener(new CmdMessageListener(jmsConnection, session, msgProcessors));

			AppLogger.getInstance().infoLog("消息中心[" + msgCenterName + "]启动监听命令队列完成");
		} catch (Exception e) {
			if (jmsConnection != null) {
				jmsConnection.close();
			}
			throw new Exception("消息中心[" + msgCenterName + "]启动监听命令队列失败", e);
		}
	}

	public void stopAllMsgProcessor() throws Exception {
		if (msgProcessors != null) {
			Iterator<String> iter = msgProcessors.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				MsgProcessor tmp = msgProcessors.get(key);
				tmp.stop();
			}
		}
	}
}