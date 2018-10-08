package sunlightMQ.processor;

import java.util.Vector;

import SunlightFrame.config.Module;
import SunlightFrame.web.DataHandle;

public class ClientProcessor extends BaseProcessor {
	public ClientProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	public void makeView() throws Exception {
		String action = getFormData("action");
		if (action.equals("list")) {

			setFormData("q_deleteFlag", "0");
			initPageByQueryDataList("client", getFormDatas(), "clients", "", new Vector<String>(), "");
		}

		if (!getFormData("action").equals("list")) {
			setFormData("queryCondition", makeQueryConditionHtml("client"));
		}
	}

	public void defaultViewAction() throws Exception {
		listAction();
	}

	public void addViewAction() throws Exception {
		String[] items = { "clientID" };
		clearDatas(items);
	}

	public void editViewAction() throws Exception {
		getData("client", DATA_TYPE_TABLE);
	}

	public void detailViewAction() throws Exception {
		getData("client", DATA_TYPE_TABLE);
	}
}
