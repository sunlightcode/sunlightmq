package sunlightMQ.processor;

import java.util.Hashtable;
import java.util.Vector;

import SunlightFrame.config.Module;
import SunlightFrame.database.DBProxy;
import SunlightFrame.util.DateTimeUtil;
import SunlightFrame.web.CheckList;
import SunlightFrame.web.DataHandle;
import SunlightFrame.web.validate.StringCheckItem;

public class MqServerProcessor extends BaseProcessor {
	public MqServerProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	public void makeView() throws Exception {
		String action = getFormData("action");
		if (action.equals("list")) {
			Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), "mqServer");
			setJSPData("datas", datas);
		}
	}

	public void defaultViewAction() throws Exception {
		listAction();
	}

	public void addViewAction() throws Exception {
		String[] items = { "mqServerID" };
		clearDatas(items);
	}

	public void editViewAction() throws Exception {
		getData("mqServer", DATA_TYPE_TABLE);
	}

	public boolean confirmActionCheck() throws Exception {
		CheckList list = getChecklist();
		list.addCheckItem(new StringCheckItem("name", "名称", true));
		list.addCheckItem(new StringCheckItem("url", "url", true));
		return list.check();
	}

	public void confirmAction() throws Exception {
		String curTime = DateTimeUtil.getCurrentDateTime();
		if (getFormData("mqServerID").equals("")) {
			setFormData("addTime", curTime);
		}
		setFormData("updateTime", curTime);
		confirmValue("mqServer");

		listAction();
	}
}
