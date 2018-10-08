package SunlightMQ.service;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import SunlightMQ.Result;
import SunlightFrame.database.DBConnectionPool;
import SunlightFrame.web.FrameKeys;

public abstract class Service {
	private long reflectTime = 0;
	private long dbTime = 0;
	private long cacheTime = 0;

	private Hashtable<String, String> paras;
	private Connection con = null;
	private HttpServletRequest request;
	private String className;
	private String methodName;
	private Hashtable<String, String> sessionKey;

	private Hashtable<String, String> formDatas;
	private Hashtable<String, Object> objectDatas;
	private Hashtable<String, String> errorData;

	public String status = "1";
	public String message = "";
	public String messageJson = "";

	protected void calReflectTime() {
		if (reflectTime == 0) {
			reflectTime = System.currentTimeMillis();
		} else {
			reflectTime = System.currentTimeMillis() - reflectTime;
			request.setAttribute("reflectTime", reflectTime);
		}
	}

	protected void calCacheTime() {
		if (cacheTime == 0) {
			cacheTime = System.currentTimeMillis();
		} else {
			cacheTime = System.currentTimeMillis() - cacheTime;
			request.setAttribute("cacheTime", cacheTime);
		}
	}

	protected void calDbTime() {
		if (dbTime == 0) {
			dbTime = System.currentTimeMillis();
		} else {
			dbTime = System.currentTimeMillis() - dbTime;
			request.setAttribute("dbTime", dbTime);
		}
	}

	public Service(String className, String businessName, Hashtable<String, String> paras,
			Hashtable<String, String> sesstionkey, HttpServletRequest request) {
		this.className = className;
		this.methodName = businessName;
		this.paras = paras;
		// this.con = con;
		this.request = request;
		this.sessionKey = sesstionkey;

		formDatas = new Hashtable<String, String>();
		objectDatas = new Hashtable<String, Object>();
		errorData = new Hashtable<String, String>();
	}

	public boolean checkProtiry() {
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doAction() throws Exception {
		Class class1 = getClass();

		boolean checkResult = false;
		calReflectTime();
		try {
			Method method = class1.getMethod("sendMessageCheck", new Class[0]);
			checkResult = ((Boolean) method.invoke(this, new Object[0])).booleanValue();
		} catch (NoSuchMethodException e) {
			checkResult = true;
		}

		if (checkResult) {
			Method method = class1.getMethod("sendMessageAction", new Class[0]);
			method.invoke(this, new Object[0]);
		}
	}

	public abstract void endProcess() throws Exception;

	public void setFormDatas(Hashtable<String, String> datas) {
		formDatas.putAll(datas);
	}

	public void setFormData(String key, String value) {
		formDatas.put(key, value);
	}

	public Hashtable<String, String> getFormDatas() {
		return formDatas;
	}

	public void setObjectData(String key, Object obj) {
		objectDatas.put(key, obj);
	}

	public void setErrorMsg(String key, String value) {
		errorData.put(key, value);
	}

	public Hashtable<String, String> getParas() {
		return paras;
	}

	public String getPara(String key) {
		String value = (String) paras.get(key);
		return value != null ? value : "";
	}

	public Connection getConnection() throws Exception {
		if (con == null) {
			con = DBConnectionPool.getInstance().getConnection();
			// con.setAutoCommit(false);
			request.setAttribute(FrameKeys.DATABASE_CONNECTION, con);
		}

		return con;
	}

	public Hashtable<String, String> getSessionKey() {
		return sessionKey;
	}

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName;
	}

	protected final Checker getChecker() {
		return new Checker(this);
	}

	public String getJSONString() throws Exception {
		return new Result(status, message, messageJson).toJSON();
	}

	public static String getErrorJSONString(String message) throws Exception {
		return new Result("0", message, null).toJSON();
	}

	public static String getErrorJSONString(String status, String message) throws Exception {
		return new Result(status, message, null).toJSON();
	}

	public HttpServletRequest getRequest() {
		return request;
	}
}
