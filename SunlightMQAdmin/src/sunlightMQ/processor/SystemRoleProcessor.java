package sunlightMQ.processor;

import java.util.Hashtable;
import java.util.Vector;

import SunlightFrame.config.Module;
import SunlightFrame.database.DBProxy;
import SunlightFrame.web.CheckList;
import SunlightFrame.web.DataHandle;
import SunlightFrame.web.validate.StringCheckItem;

public class SystemRoleProcessor extends BaseProcessor {
	public SystemRoleProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	public void makeView() throws Exception {
		String action = getFormData("action");
		if (action.equals("list")) {
			setJSPData("datas", DBProxy.query(getConnection(), "systemRole"));
		} else {
			setJSPData("systemModuleDatas", getConstantValues(getConnection(), "c_systemModule"));
		}
	}

	public void defaultViewAction() throws Exception {
		setFormData("pageIndex", "");
		listAction();
	}

	public void addViewAction() throws Exception {
		String[] items = { "systemRoleID", "name", "selectedValues" };
		clearDatas(items);
	}

	public void editViewAction() throws Exception {
		getData("systemRole", DATA_TYPE_TABLE);
		setFormData("selectedValues", getFormData("priority"));
	}

	public void confirmAction() throws Exception {
		setFormData("priority", getFormData("selectedValues"));
		confirmValue("systemRole");
		listAction();
	}

	public boolean confirmActionCheck() throws Exception {
		CheckList list = getChecklist();
		list.addCheckItem(new StringCheckItem("name", "角色名称", true));
		return list.check();
	}

	public void disableAction() throws Exception {
		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put("systemRoleID", getFormData("systemRoleID"));

		key.put("validFlag", "1");
		Vector<Hashtable<String, String>> systemUsers = DBProxy.query(getConnection(), "systemUser", key);
		if (systemUsers.size() > 0) {
			setErrorMessage("请先停用掉相关的系统用户账号");
			return;
		}

		Hashtable<String, String> value = new Hashtable<String, String>();
		value.put("validFlag", "0");

		key.remove("validFlag");
		DBProxy.update(getConnection(), "systemRole", key, value);
		listAction();
	}
}
