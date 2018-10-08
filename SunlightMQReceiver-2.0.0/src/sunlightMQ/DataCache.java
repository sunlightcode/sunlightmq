package sunlightMQ;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import SunlightFrame.config.AppConfig;
import SunlightFrame.database.DBConnectionPool;
import SunlightFrame.database.DBProxy;
import SunlightFrame.log.AppLogger;
import SunlightFrame.util.StringUtil;

public class DataCache {
	private static String[] baseDataTables = { "queue", "mqServer", "msgCenter", "msgProcessor", "c_method" };

	private static DataCache instance = new DataCache();

	private Hashtable<String, Vector<Hashtable<String, String>>> tableDatas;
	private Hashtable<String, Hashtable<String, Hashtable<String, String>>> tableDataHash;

	private Hashtable<String, Hashtable<String, String>> queueHash;

	private Hashtable<String, Hashtable<String, String>> mqServerHash;

	private Hashtable<String, Hashtable<String, String>> msgCenterHash;

	private Hashtable<String, Hashtable<String, String>> msgProcessorHash;

	private Hashtable<String, Hashtable<String, String>> businessHash;

	private DataCache() {
	}

	public static DataCache getInstance() {
		return instance;
	}

	public void reload() throws Exception {
		AppConfig.getInstance().reload();
		loadData();
	}

	public void loadData() {
		Connection con = null;
		try {
			con = DBConnectionPool.getInstance().getConnection();
			loadBaseDatas(con);
			loadQueue();
			loadMqServer();
			loadMsgCenter();
			loadMsgProcessor();
			loadBusiness();
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

	private void loadBaseDatas(Connection con) throws Exception {
		tableDatas = new Hashtable<String, Vector<Hashtable<String, String>>>();
		tableDataHash = new Hashtable<String, Hashtable<String, Hashtable<String, String>>>();

		for (int i = 0; i < baseDataTables.length; ++i) {
			loadTableData(baseDataTables[i], con);
		}
	}

	public void loadMqServer() throws Exception {
		mqServerHash = new Hashtable<String, Hashtable<String, String>>();
		Vector<Hashtable<String, String>> methods = getTableDatas("mqServer");
		for (int i = 0; i < methods.size(); ++i) {
			Hashtable<String, String> tmp = methods.get(i);
			if (mqServerHash.get(tmp.get("mqServerID")) == null) {
				mqServerHash.put(tmp.get("mqServerID"), tmp);
			}
		}
	}

	public void loadQueue() throws Exception {
		queueHash = new Hashtable<String, Hashtable<String, String>>();
		Vector<Hashtable<String, String>> methods = getTableDatas("queue");
		for (int i = 0; i < methods.size(); ++i) {
			Hashtable<String, String> tmp = methods.get(i);
			if (queueHash.get(tmp.get("name")) == null) {
				queueHash.put(tmp.get("name"), tmp);
			}
		}
	}

	public void loadMsgCenter() throws Exception {
		msgCenterHash = new Hashtable<String, Hashtable<String, String>>();
		Vector<Hashtable<String, String>> methods = getTableDatas("msgCenter");
		for (int i = 0; i < methods.size(); ++i) {
			Hashtable<String, String> tmp = methods.get(i);
			if (msgCenterHash.get(tmp.get("name")) == null) {
				msgCenterHash.put(tmp.get("name"), tmp);
			}
		}
	}

	public void loadMsgProcessor() throws Exception {
		msgProcessorHash = new Hashtable<String, Hashtable<String, String>>();
		Vector<Hashtable<String, String>> methods = getTableDatas("msgProcessor");
		for (int i = 0; i < methods.size(); ++i) {
			Hashtable<String, String> tmp = methods.get(i);
			if (msgProcessorHash.get(tmp.get("name")) == null) {
				msgProcessorHash.put(tmp.get("name"), tmp);
			}
		}
	}

	public void loadBusiness() throws Exception {
		businessHash = new Hashtable<String, Hashtable<String, String>>();
		Vector<Hashtable<String, String>> methods = getTableDatas("c_method");
		for (int i = 0; i < methods.size(); ++i) {
			Hashtable<String, String> tmp = methods.get(i);
			if (businessHash.get(tmp.get("c_methodName")) == null) {
				businessHash.put(tmp.get("c_methodName"), tmp);
			}
		}
	}

	public void loadTableData(String tableName, Connection con) throws Exception {
		Vector<Hashtable<String, String>> datas = new Vector<Hashtable<String, String>>();
		datas = DBProxy.query(con, tableName);

		Hashtable<String, Hashtable<String, String>> datasHash = new Hashtable<String, Hashtable<String, String>>();
		Hashtable<String, String> tmpData;
		for (int i = 0; i < datas.size(); ++i) {
			tmpData = datas.get(i);
			datasHash.put(tmpData.get(tableName + "ID"), tmpData);
		}
		tableDatas.put(tableName, datas);
		tableDataHash.put(tableName, datasHash);
	}

	public Vector<Hashtable<String, String>> getTableDatas(String tableName) {
		return tableDatas.get(tableName);
	}

	public Hashtable<String, Hashtable<String, String>> getTableDatasHash(String tableName) {
		return tableDataHash.get(tableName);
	}

	public Hashtable<String, String> getTableDataByID(String tableName, String id) {
		Hashtable<String, String> data = getTableDatasHash(tableName).get(id);
		if (data == null) {
			return new Hashtable<String, String>();
		}
		return data;
	}

	public String getTableDataColumnValue(String tableName, String id, String column) {
		Hashtable<String, String> data = getTableDataByID(tableName, id);
		return data.get(column) == null ? "" : data.get(column);
	}

	public String getTableDataColumnsValue(String tableName, String ids, String column) {
		String[] id = StringUtil.split(ids, ", ");
		StringBuffer sbf = new StringBuffer();
		int index = 0;
		for (int i = 0; i < id.length; ++i) {
			Hashtable<String, String> data = getTableDataByID(tableName, id[i]);
			String tmpColumnValue = data.get(column) == null ? "" : data.get(column);
			if (!tmpColumnValue.equals("")) {
				if (index != 0) {
					sbf.append(",").append(tmpColumnValue);
				} else {
					sbf.append(tmpColumnValue);
				}
				index++;
			}
		}

		return sbf.toString();
	}

	@SuppressWarnings("rawtypes")
	public void clear() throws Exception {
		// 将所有的的dataCache下的变量置为null
		Class clazz = DataCache.class;
		Field[] field = clazz.getDeclaredFields();
		for (int i = field.length - 1; i >= 0; i--) {
			field[i].setAccessible(true);// 设置true,使其不在检查访问修饰符。
			field[i].set(instance, null);
		}
		instance = null;
	}

	public Hashtable<String, String> getQueue(String name) {
		return queueHash.get(name) == null ? new Hashtable<String, String>() : queueHash.get(name);
	}

	public Hashtable<String, String> getMqServer(String id) {
		return mqServerHash.get(id) == null ? new Hashtable<String, String>() : mqServerHash.get(id);
	}

	public Hashtable<String, String> getMsgCenter(String name) {
		return msgCenterHash.get(name) == null ? new Hashtable<String, String>() : msgCenterHash.get(name);
	}

	public Hashtable<String, String> getMsgProcessor(String name) {
		return msgProcessorHash.get(name) == null ? new Hashtable<String, String>() : msgProcessorHash.get(name);
	}

	public Hashtable<String, String> getBusiness(String name) {
		return businessHash.get(name) == null ? new Hashtable<String, String>() : businessHash.get(name);
	}

	public boolean askB2c(String messageProcessorName) {
		boolean askB2c = false;
		if (msgProcessorHash.get(messageProcessorName).get("askb2cflag").equals("1")) {
			askB2c = true;
		}
		return askB2c;
	}
}
