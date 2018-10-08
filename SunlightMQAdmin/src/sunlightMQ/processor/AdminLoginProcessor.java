package sunlightMQ.processor;

import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.Cookie;

import SunlightFrame.config.Module;
import SunlightFrame.database.DBProxy;
import SunlightFrame.util.MD5Util;
import SunlightFrame.web.CheckList;
import SunlightFrame.web.DataHandle;

public class  AdminLoginProcessor extends BaseProcessor {
	public AdminLoginProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	public void makeView() throws Exception {
	}

	public void defaultViewAction() throws Exception {
		if (isUserLogined()) {
			loginAction();
		} else {
			setFormData("adminUserName", getCookieData("adminUserName").replace('$', '@'));
			setFormData("rememberUserNameFlag", getCookieData("rememberUserNameFlag"));
		}
	}

	public void loginAction() throws Exception {
		setControlData("INIT_FUNCTION", "location.href='admin';");

		String rememberUserNameFlag = getFormData("rememberUserNameFlag");

		if (rememberUserNameFlag.equals("1")) {
			if (getFormData("adminUserName").indexOf("@") != -1) {
				setFormData("adminUserName", getFormData("adminUserName").replace("@", "$"));
			}
			Cookie adminUserNameCookie = new Cookie("adminUserName", getFormData("adminUserName"));
			adminUserNameCookie.setMaxAge(30 * 24 * 60 * 60);
			setCookieData(adminUserNameCookie);

			Cookie rememberUserNameFlagCookie = new Cookie("rememberUserNameFlag", getFormData("rememberUserNameFlag"));
			rememberUserNameFlagCookie.setMaxAge(30 * 24 * 60 * 60);
			setCookieData(rememberUserNameFlagCookie);
		} else {
			if (getCookieData("adminUserName").replace("$", "@").equals(getFormData("adminUserName"))) {
				setCookieData(new Cookie("adminUserName", ""));
				setCookieData(new Cookie("rememberUserNameFlag", ""));
			}
		}
	}

	public boolean loginActionCheck() throws Exception {
		CheckList list = getChecklist();
		// list.addCheckItem(new RandomStringCheckItem(
		// "randomString", "验证码", (String)
		// getSessionData(FrameKeys.RANDOM_STRING)));

		if (!list.check()) {
			return false;
		}

		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put("userID", getFormData("adminUserName"));
		Vector<Hashtable<String, String>> users = DBProxy.query(getConnection(), "systemUser_V", key);
		if (users.size() > 0) {
			Hashtable<String, String> user = users.get(0);
			if (!user.get("validFlag").equals("1")) {
				setErrorMessage("该账号已停用");
				setFocusItem("adminUserName");
				return false;
			} else if (!MD5Util.MD5(getFormData("adminPassword")).equals(user.get("password").toLowerCase())) {// MD5
																												// 算法不区分大小写
				System.out.println(user.get("password"));
				System.out.println(MD5Util.MD5(getFormData("adminPassword")));
				setErrorMessage("错误的密码");
				setFocusItem("adminPassword");
				return false;
			} else {
				setLoginedUserInfo(user);
				return true;
			}
		} else {
			setErrorMessage("错误的账号");
			setFocusItem("adminUserName");
			return false;
		}
	}

	public void logoffAction() throws Exception {
		removeLoginedUserInfo();
	}
}
