package sunlightMQ.processor;

import java.util.Vector;

import SunlightFrame.config.Module;
import SunlightFrame.util.DateTimeUtil;
import SunlightFrame.web.CheckList;
import SunlightFrame.web.DataHandle;
import SunlightFrame.web.validate.IntegerCheckItem;
import SunlightFrame.web.validate.StringCheckItem;
import sunlightMQ.AppKeys;
import sunlightMQ.DataCache;

public class QueueProcessor extends BaseProcessor {
	public QueueProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	public void makeView() throws Exception {
		String action = getFormData("action");
		if (action.equals("list")) {
			setFormData("q_deletedFlag", "0");
			initPageByQueryDataList("queue", getFormDatas(), "datas", "", new Vector<String>(), "");
		} else {
			setFormData("prioritySelect", makeSelectElementString("priorityID",
					DataCache.getInstance().getTableDatas("c_priority"), "c_priorityID", "c_priorityName", ""));
			setFormData("acknowledTypeSelect",
					makeSelectElementString("acknowledTypeID", DataCache.getInstance().getTableDatas("c_acknowledType"),
							"c_acknowledTypeID", "c_acknowledTypeName", ""));
			setFormData("processExceptionModeSelect",
					makeSelectElementString("processExceptionModeID",
							DataCache.getInstance().getTableDatas("c_processExceptionMode"), "c_processExceptionModeID",
							"c_processExceptionModeName", ""));
			setFormData("processExceptionReportModeSelect",
					makeSelectElementString("processExceptionReportModeID",
							DataCache.getInstance().getTableDatas("c_processExceptionReportMode"),
							"c_processExceptionReportModeID", "c_processExceptionReportModeName",
							"selectProcessExceptionReportMode()"));

			setFormData("processExceptionRequestTypeSelect",
					makeSelectElementString("processExceptionRequestTypeID",
							DataCache.getInstance().getTableDatas("c_requestType"), "c_requestTypeID",
							"c_requestTypeName", ""));
		}
	}

	public void defaultViewAction() throws Exception {
		listAction();
	}

	public void addViewAction() throws Exception {
		String[] items = { "queueID" };
		clearDatas(items);
	}

	public void editViewAction() throws Exception {
		getData("queue", DATA_TYPE_TABLE);
	}

	public boolean confirmActionCheck() throws Exception {
		CheckList list = getChecklist();
		list.addCheckItem(new StringCheckItem("name", "名称", true));
		list.addCheckItem(new StringCheckItem("queueType", "类型", true));
		list.addCheckItem(new IntegerCheckItem("persistentFlag", "是否持久化", true));
		list.addCheckItem(new IntegerCheckItem("priorityID", "优先级别", true));
		list.addCheckItem(new IntegerCheckItem("timeToLive", "存活时间", true));
		list.addCheckItem(new IntegerCheckItem("durableFlag", "是否持久订阅", true));
		list.addCheckItem(new IntegerCheckItem("acknowledTypeID", "签收模式", true));

		list.addCheckItem(new IntegerCheckItem("processExceptionModeID", "异常处理方式", true));
		list.addCheckItem(new IntegerCheckItem("processExceptionReportModeID", "异常报告方式", true));
		if (getFormData("processExceptionReportModeID").equals(AppKeys.PROCESSEXCEPTIONREPORTMODE_CALLBACK)) {
			list.addCheckItem(new StringCheckItem("processExceptionUrl", "异常回调地址", true));
			list.addCheckItem(new IntegerCheckItem("processExceptionRequestTypeID", "异常回调请求方法", true));
		}

		if (list.check()) {
			if (isExistsData("queue", "queueID", getFormData("queueID"), "name", getFormData("name"))) {
				setErrorMessage("名称重复");
				return false;
			}
		}

		return list.check();
	}

	public void confirmAction() throws Exception {
		String curTime = DateTimeUtil.getCurrentDateTime();
		if (!getFormData("processExceptionReportModeID").equals(AppKeys.PROCESSEXCEPTIONREPORTMODE_CALLBACK)) {
			setFormData("processExceptionUrl", "");
			setFormData("processExceptionRequestTypeID", "");
		}
		if (getFormData("queueID").equals("")) {
			setFormData("addTime", curTime);
			setFormData("deletedFlag", "0");
		}
		setFormData("updateTime", curTime);
		confirmValue("queue");

		listAction();
	}
}
