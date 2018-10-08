package sunlightMQ.processor;

import java.util.Hashtable;
import java.util.Vector;

import SunlightFrame.config.Module;
import SunlightFrame.database.DBProxy;
import SunlightFrame.web.CheckList;
import SunlightFrame.web.DataHandle;
import SunlightFrame.web.validate.StringCheckItem;

public class ServiceProcessor extends BaseProcessor {
	public ServiceProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	public void makeView() throws Exception {
		String action = getFormData("action");
		if (action.equals("list")) {
			initPageByQueryDataList("c_method", getFormDatas(), "datas", "", new Vector<String>(), "");
			setFormData("queueSelect", getQueryQueueSelect());
		} else {
			setFormData("queueSelect", getQueryQueueSelect());
		}
	}

	public void defaultViewAction() throws Exception {
		listAction();
		setFormData("q_c_queueName", getFormData(""));
	}

	public void addViewAction() throws Exception {
		String[] items = { "c_methodID" };
		clearDatas(items);
	}

	public void editViewAction() throws Exception {
		getData("c_method", DATA_TYPE_TABLE);
		setFormData("q_c_queueName", getFormData("c_queueName"));
	}

	public void deleteAction() throws Exception {
		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put("c_methodID", getFormData("c_methodID"));
		DBProxy.delete(getConnection(), "c_method", key);
		listAction();
	}

	public boolean confirmActionCheck() throws Exception {
		CheckList list = getChecklist();
		list.addCheckItem(new StringCheckItem("c_methodName", "名称", true));
		if (list.check()) {
			if (isExistsData("c_method", "c_methodID", getFormData("c_methodID"), "c_methodName",
					getFormData("c_methodName"))) {
				setErrorMessage("名称重复");
				return false;
			}
		}
		return list.check();
	}

	public void confirmAction() throws Exception {
		setFormData("c_queueName", getFormData("q_c_queueName"));
		confirmValue("c_method");
		setFormData("q_c_queueName", getFormData(""));
		listAction();
	}
}
