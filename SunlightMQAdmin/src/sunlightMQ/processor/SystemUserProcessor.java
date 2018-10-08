package sunlightMQ.processor;

import java.util.Hashtable;

import SunlightFrame.config.Module;
import SunlightFrame.database.DBProxy;
import SunlightFrame.util.MD5Util;
import SunlightFrame.web.CheckList;
import SunlightFrame.web.DataHandle;
import SunlightFrame.web.validate.IntegerCheckItem;
import SunlightFrame.web.validate.PasswordCheckItem;
import SunlightFrame.web.validate.StringCheckItem;

public class SystemUserProcessor extends BaseProcessor {
	public SystemUserProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	public void makeView() throws Exception {
		String action = getFormData("action");
		if (action.equals("list")) {
			setJSPData("datas", DBProxy.query(getConnection(), "systemUser_V", new Hashtable<String, String>()));
		} else {
			setFormData("systemRoleSelect", getSystemRoleSelect());
		}
	}

	public void defaultViewAction() throws Exception {
		listAction();
	}

	public void addViewAction() throws Exception {
		String[] items = { "systemUserID" };
		clearDatas(items);
	}

	public void editViewAction() throws Exception {
		getData("systemUser", DATA_TYPE_TABLE);
	}

	public void confirmAction() throws Exception {
		if (getFormData("systemUserID").equals("")) {
			setFormData("password", MD5Util.MD5(getFormData("password")));
		}

		confirmValue("systemUser");
		listAction();
	}

	public boolean confirmActionCheck() throws Exception {
		CheckList list = getChecklist();
		if (getFormData("systemUserID").equals("")) {
			list.addCheckItem(new StringCheckItem("userID", "用户帐号", true));
			list.addCheckItem(new PasswordCheckItem("password", getFormData("password2"), "密码"));
		}

		list.addCheckItem(new StringCheckItem("userName", "姓名", true));
		list.addCheckItem(new IntegerCheckItem("systemRoleID", "系统角色", true));

		if (list.check()) {
			if (getFormData("systemUserID").equals("")) {
				Hashtable<String, String> key = new Hashtable<String, String>();
				key.put("userID", getFormData("userID"));
				if (DBProxy.query(getConnection(), "systemUser", key).size() > 0) {
					setErrorMessage("该用户帐号已经存在");
					setFocusItem("userID");
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	public String getSystemRoleSelect() throws Exception {
		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put("validFlag", "1");
		return makeSelectElementString("systemRoleID", DBProxy.query(getConnection(), "systemRole", key),
				"systemRoleID", "name", "");
	}
}
