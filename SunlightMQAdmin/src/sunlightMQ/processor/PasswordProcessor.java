package sunlightMQ.processor;

import java.util.Hashtable;

import SunlightFrame.config.Module;
import SunlightFrame.database.DBProxy;
import SunlightFrame.util.MD5Util;
import SunlightFrame.web.CheckList;
import SunlightFrame.web.DataHandle;
import SunlightFrame.web.validate.PasswordCheckItem;

public class PasswordProcessor extends BaseProcessor {
	public PasswordProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	public void makeView() throws Exception {
	}

	public void defaultViewAction() {
	}

	public boolean confirmActionCheck() throws Exception {
		if (!MD5Util.MD5(getFormData("password1")).equals(getLoginedUserInfo().get("password"))) {
			setErrorMessage("原密码的输入不正确");
			setFocusItem("password1");
			return false;
		}

		CheckList list = getChecklist();
		list.addCheckItem(new PasswordCheckItem("password2", getFormData("password3"), "密码"));
		return list.check();
	}

	public void confirmAction() throws Exception {
		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put("systemUserID", getLoginedUserInfo().get("systemUserID"));

		String newPassword = MD5Util.MD5(getFormData("password2"));
		Hashtable<String, String> value = new Hashtable<String, String>();
		value.put("password", newPassword);

		DBProxy.update(getConnection(), "systemUser", key, value);

		getLoginedUserInfo().put("password", newPassword);

		setInfoMessage("密码修改成功");
	}

}
