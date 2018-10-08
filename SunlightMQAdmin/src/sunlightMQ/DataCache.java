package sunlightMQ;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import SunlightFrame.config.AppConfig;
import SunlightFrame.database.DBConnectionPool;
import SunlightFrame.database.DBProxy;
import SunlightFrame.log.AppLogger;
import SunlightFrame.util.StringUtil;

public class DataCache {
	private static String[] baseDataTables = { "c_method", "c_priority", "c_acknowledType", "c_requestType",
			"c_callbackMethodType", "c_processExceptionMode", "c_processExceptionReportMode", "msgCenter", "queue" };

	private static DataCache instance = new DataCache();

	private Hashtable<String, Vector<Hashtable<String, String>>> tableDatas;
	private Hashtable<String, Hashtable<String, Hashtable<String, String>>> tableDataHash;

	private Vector<Hashtable<String, String>> clients;
	private Hashtable<String, Hashtable<String, String>> clientHash;

	private Hashtable<String, Hashtable<String, String>> methodHash;

	private HashMap<String, ArrayList<HashMap<String, String>>> semantickeysHash;

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
			loadClient(con);
			loadMethod();
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

	public void loadClient(Connection con) throws Exception {
		clientHash = new Hashtable<String, Hashtable<String, String>>();

		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put("deleteFlag", "0");

		clients = DBProxy.query(con, "client", key);
		for (int i = 0; i < clients.size(); ++i) {
			clientHash.put(clients.get(i).get("appKey"), clients.get(i));
		}
	}

	public void loadMethod() throws Exception {
		methodHash = new Hashtable<String, Hashtable<String, String>>();
		Vector<Hashtable<String, String>> methods = getTableDatas("c_method");
		for (int i = 0; i < methods.size(); ++i) {
			Hashtable<String, String> tmp = methods.get(i);
			if (methodHash.get(tmp.get("c_methodName")) == null) {
				methodHash.put(tmp.get("c_methodName"), tmp);
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

	public Hashtable<String, String> getClient(String appKey) {
		return clientHash.get(appKey) == null ? new Hashtable<String, String>() : clientHash.get(appKey);
	}

	public ArrayList<HashMap<String, String>> getSemantickeys(String aspectID) {
		return semantickeysHash.get(aspectID) == null ? new ArrayList<HashMap<String, String>>()
				: semantickeysHash.get(aspectID);
	}

	public void clear() throws Exception {
		// 将所有的的dataCache下的变量置为null
		Class<DataCache> clazz = DataCache.class;
		Field[] field = clazz.getDeclaredFields();
		for (int i = field.length - 1; i >= 0; i--) {
			field[i].setAccessible(true);// 设置true,使其不在检查访问修饰符。
			field[i].set(instance, null);
		}
		instance = null;
	}

	public Vector<Hashtable<String, String>> getClients() {
		return clients;
	}

	public Hashtable<String, String> getMethod(String method) {
		return methodHash.get(method) == null ? new Hashtable<String, String>() : methodHash.get(method);
	}
}
