package sunlightMQ.processor;

import java.util.Hashtable;
import java.util.Vector;

import SunlightFrame.config.Module;
import SunlightFrame.database.DBProxy;
import SunlightFrame.web.DataHandle;

public class ExceptionLogProcessor extends BaseProcessor {

	public ExceptionLogProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	public void makeView() throws Exception {
		String action = getFormData("action");
		if (action.equals("list")) {
			initPageByQueryDataList("processExceptionLog", getFormDatas(), "datas", "", new Vector<String>(),
					" order by logTime desc");
		}
	}

	public void defaultViewAction() throws Exception {
		listAction();
	}

	public void deleteAction() throws Exception {
		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put("processExceptionLogID", getFormData("processExceptionLogID"));
		DBProxy.delete(getConnection(), "processExceptionLog", key);
		listAction();
	}
}
