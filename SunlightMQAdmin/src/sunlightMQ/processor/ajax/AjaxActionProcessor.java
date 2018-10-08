package sunlightMQ.processor.ajax;

import java.util.Hashtable;
import java.util.Vector;

import javax.jms.Connection;

import SunlightFrame.config.AppConfig;
import SunlightFrame.config.Module;
import SunlightFrame.database.DBProxy;
import SunlightFrame.database.EntityCache;
import SunlightFrame.database.IndexGenerater;
import SunlightFrame.log.AppLogger;
import SunlightFrame.util.DateTimeUtil;
import SunlightFrame.util.MD5Util;
import SunlightFrame.util.StringUtil;
import SunlightFrame.web.CheckList;
import SunlightFrame.web.DataHandle;
import SunlightFrame.web.FrameKeys;
import SunlightFrame.web.validate.IntegerCheckItem;
import SunlightFrame.web.validate.PasswordCheckItem;
import SunlightFrame.web.validate.StringCheckItem;
import sunlightMQ.AppKeys;
import sunlightMQ.DataCache;
import sunlightMQ.jms.ConnectionManager;
import sunlightMQ.jms.JMSSendder;
import sunlightMQ.tool.MyHttpClient;

public class AjaxActionProcessor extends AjaxBaseProcessor {

	public AjaxActionProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	public void getNotices() throws Exception {
	}

	public void resetSystemUserPassword() throws Exception {
		CheckList list = getChecklist();
		list.addCheckItem(new PasswordCheckItem("password", getFormData("password2"), "密码"));
		if (!list.check()) {
			setErrorMessageAndFocusItem(getControlDatas().get(FrameKeys.ERROR_MESSAGE),
					getControlDatas().get(FrameKeys.FOCUS_ITEM));
			return;
		}

		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put("systemUserID", getFormData("systemUserID"));
		Hashtable<String, String> value = new Hashtable<String, String>();
		value.put("password", MD5Util.MD5(getFormData("password")));
		DBProxy.update(getConnection(), "systemUser", key, value);
		setFormData(AppKeys.AJAX_RESULT, "alert('密码已重置');closeWindow();");
	}

	public void recache() throws Exception {
		AppConfig.getInstance().reload();
		EntityCache.getInstance().clear();
		DataCache.getInstance().reload();

		setAjaxJavascript("alert('重启缓存成功')");
	}

	public void recacheAll() throws Exception {
		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put("isAppHost", "1");
		Vector<Hashtable<String, String>> hosts = DBProxy.query(getConnection(), "host");
		StringBuffer sbf = new StringBuffer();
		for (int i = 0; i < hosts.size(); ++i) {
			Hashtable<String, String> host = hosts.get(i);
			String[] appHostPort = StringUtil.split(host.get("appHostPort"), ",");
			for (int j = 0; j < appHostPort.length; ++j) {
				String recacheUrl = "http://" + host.get("innerIP") + ":" + appHostPort[j]
						+ "/bussinessEntry?module=recache&action=recache" + "&recacheSystemUserID="
						+ getLoginedUserInfo().get("systemUserID") + "&recachePassword="
						+ getLoginedUserInfo().get("password");
				try {
					String res = MyHttpClient.excuteGetMethod(recacheUrl, "utf-8");
					if (res.indexOf("true") != -1) {
						sbf.append("重启[" + host.get("name") + ":" + appHostPort[j] + "]缓存成功;");
					} else {
						sbf.append("重启[" + host.get("name") + ":" + appHostPort[j] + "]缓存失败;");
					}
				} catch (Exception e) {
					AppLogger.getInstance().errorLog("重启[" + host.get("name") + ":" + appHostPort[j] + "]缓存失败", e);
					sbf.append("重启[" + host.get("name") + ":" + appHostPort[j] + "]缓存失败;");
				}
			}
		}

		setAjaxJavascript("alert('" + sbf.toString() + "')");
	}

	public void resetHotelCache() throws Exception {
		CheckList list = getChecklist();
		list.addCheckItem(new IntegerCheckItem("hostID", "主机ID", true));
		if (!list.check()) {
			setErrorMessageAndFocusItem();
			return;
		}
		String hostID = getFormData("hostID");
		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put("hostID", hostID);
		Vector<Hashtable<String, String>> hosts = DBProxy.query(getConnection(), "host", key);
		if (hosts.size() == 0) {
			setAjaxJavascript("alert('不存在该主机')");
			return;
		}

		Hashtable<String, String> host = hosts.get(0);

		if (!host.get("isAppHost").equals("1")) {
			setAjaxJavascript("alert('该主机不是应用服务器,无法进行此操作')");
			return;
		}

		String[] appHostPort = StringUtil.split(host.get("appHostPort"), ",");
		StringBuffer sbf = new StringBuffer();
		for (int i = 0; i < appHostPort.length; ++i) {
			String recacheUrl = "http://" + host.get("innerIP") + ":" + appHostPort[i]
					+ "/bussinessEntry?module=recache&action=resetHotelCache" + "&recacheSystemUserID="
					+ getLoginedUserInfo().get("systemUserID") + "&recachePassword="
					+ getLoginedUserInfo().get("password");
			try {
				String res = MyHttpClient.excuteGetMethod(recacheUrl, "utf-8");
				if (res.indexOf("true") != -1) {
					sbf.append("重启" + host.get("innerIP") + ":" + appHostPort[i] + "酒店信息缓缓存成功;");
				} else {
					sbf.append("重启" + host.get("innerIP") + ":" + appHostPort[i] + "酒店信息缓缓存失败;");
				}
			} catch (Exception e) {
				AppLogger.getInstance().errorLog("重启酒店信息缓缓存失败", e);
				sbf.append("重启" + host.get("innerIP") + ":" + appHostPort[i] + "酒店信息缓缓存失败;");
			}
		}

		setAjaxJavascript("alert('" + sbf.toString() + "')");
	}

	public void resetAllHotelCache() throws Exception {
		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put("isAppHost", "1");
		Vector<Hashtable<String, String>> hosts = DBProxy.query(getConnection(), "host");
		StringBuffer sbf = new StringBuffer();
		for (int i = 0; i < hosts.size(); ++i) {
			Hashtable<String, String> host = hosts.get(i);
			String[] appHostPort = StringUtil.split(host.get("appHostPort"), ",");
			for (int j = 0; j < appHostPort.length; ++j) {
				String recacheUrl = "http://" + host.get("innerIP") + ":" + appHostPort[j]
						+ "/bussinessEntry?module=recache&action=resetHotelCache" + "&recacheSystemUserID="
						+ getLoginedUserInfo().get("systemUserID") + "&recachePassword="
						+ getLoginedUserInfo().get("password");
				try {
					String res = MyHttpClient.excuteGetMethod(recacheUrl, "utf-8");
					if (res.indexOf("true") != -1) {
						sbf.append("重启[" + host.get("name") + ":" + appHostPort[j] + "]酒店信息缓存成功;");
					} else {
						sbf.append("重启[" + host.get("name") + ":" + appHostPort[j] + "]酒店信息缓存失败;");
					}
				} catch (Exception e) {
					AppLogger.getInstance().errorLog("重启[" + host.get("name") + ":" + appHostPort[j] + "]酒店信息缓存失败", e);
					sbf.append("重启[" + host.get("name") + ":" + appHostPort[j] + "]酒店信息缓存失败;");
				}
			}
		}

		setAjaxJavascript("alert('" + sbf.toString() + "')");
	}

	public void resetDesKey() throws Exception {
		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put("clientID", getFormData("clientID"));
		Hashtable<String, String> value = new Hashtable<String, String>();
		value.put("desKey", getFormData("newDesKey"));
		DBProxy.update(getConnection(), "client", key, value);

		setAjaxJavascript("alert('重置私钥成功!');postModuleAndAction('client', 'editView')");
	}

	public void resetAppKey() throws Exception {
		CheckList list = getChecklist();
		list.addCheckItem(new StringCheckItem("newAppKey", "新AppKey", true));
		if (!list.check()) {
			setErrorMessageAndFocusItem();
			return;
		}

		String newAppKey = getFormData("newAppKey");
		if (isExistsAppKey(getFormData("clientID"), newAppKey)) {
			setAjaxJavascript("alert('该AppKey已被使用,请重新输入')");
			return;
		}

		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put("clientID", getFormData("clientID"));
		Hashtable<String, String> value = new Hashtable<String, String>();
		value.put("appKey", newAppKey);
		DBProxy.update(getConnection(), "client", key, value);

		setAjaxJavascript("alert('重置AppKey成功!');postModuleAndAction('client', 'editView')");
	}

	public void confirmClient() throws Exception {
		CheckList list = getChecklist();
		list.addCheckItem(new StringCheckItem("name", "名称", true));
		list.addCheckItem(new StringCheckItem("name", "名称", true));

		if (list.check()) {
			if (getFormData("clientID").equals("")) {
				if (isExistsAppKey(getFormData("clientID"), getFormData("appKey"))) {
					setAjaxJavascript("alert('Appkey已被使用,请重新输入一个新的AppKey')");
					return;
				}
			}
		} else {
			setErrorMessageAndFocusItem();
			return;
		}

		if (isExistsClientName(getFormData("name"), getFormData("clientID"))) {
			setAjaxJavascript("alert('已能存在相同的平台名称，操作失败')");
			return;
		}

		if (getFormData("clientID").equals("")) {
			String clientID = IndexGenerater.getTableIndex("client", getConnection());
			setFormData("clientID", clientID);
			setFormData("validFlag", "1");
			setFormData("deleteFlag", "0");
			setFormData("appKey", createAppKey(clientID));
			setFormData("privateKey", createPrivateKey(clientID));
			setFormData("addTime", DateTimeUtil.getCurrentDateTime());
			setFormData("updateTime", DateTimeUtil.getCurrentDateTime());

			setFormData("clientParaID", IndexGenerater.getTableIndex("clientPara", getConnection()));

			DBProxy.insert(getConnection(), "client", getFormDatas());
		} else {
			setFormData("updateTime", DateTimeUtil.getCurrentDateTime());
			Hashtable<String, String> key = new Hashtable<String, String>();
			key.put("clientID", getFormData("clientID"));

			getFormDatas().remove("appKey");
			getFormDatas().remove("privateKey");
			DBProxy.update(getConnection(), "client", key, getFormDatas());
		}

		setAjaxJavascript("postModuleAndAction('client', 'list')");
	}

	private boolean isExistsClientName(String clientName, String clientID) throws Exception {
		String sql = "select count(*) as count from client where name = ? and deleteFlag != '1' ";
		Vector<String> paras = new Vector<String>();
		paras.add(clientName);
		if (!clientID.equals("")) {
			sql += " and clientID != ?";
			paras.add(clientID);
		}
		int count = Integer.parseInt(DBProxy.query(getConnection(), sql, paras).get(0).get("count"));
		return count > 0;
	}

	public void selectQueue() throws Exception {
		String[] MsgCenterAndQueueSelect = getMsgCenterAndQueueSelect();

		setAjaxJavascript("document.getElementById('queueSelect').innerHTML=\"" + MsgCenterAndQueueSelect[1] + "\"");
	}

	public void operationMsgProcessor() throws Exception {
		CheckList list = getChecklist();
		list.addCheckItem(new StringCheckItem("operType", "操作类型", true));
		if (!list.check()) {
			setErrorMessageAndFocusItem();
			return;
		}

		Hashtable<String, String> msgProcessorK = new Hashtable<String, String>();
		msgProcessorK.put("msgProcessorID", getFormData("msgProcessorID"));
		msgProcessorK.put("validFlag", "1");

		Vector<Hashtable<String, String>> msgProcessors = DBProxy.query(getConnection(), "msgProcessor_V",
				msgProcessorK);
		if (msgProcessors.size() == 0) {
			setAjaxJavascript("alert('不存在该消息处理器')");
			return;
		}

		Hashtable<String, String> msgProcessor = msgProcessors.get(0);

		Connection jmsConnection = null;
		try {
			String msgProcessorAndCmd = msgProcessor.get("name") + "-" + getFormData("operType");
			jmsConnection = ConnectionManager.getConnection(msgProcessor.get("userName"), msgProcessor.get("password"),
					msgProcessor.get("url"));
			JMSSendder.sendMsg(jmsConnection, AppKeys.CMD_QUEUE, true, true, AppKeys.ACK_AUTOACKNOWLEDGE,
					msgProcessorAndCmd);
		} catch (Exception e) {
			setAjaxJavascript("alert('发送指令到MQ失败')");
			return;
		} finally {
			if (jmsConnection != null) {
				jmsConnection.close();
			}
		}

		setAjaxJavascript("alert('发送指令到MQ成功');closeInfoWindow()");
	}
}