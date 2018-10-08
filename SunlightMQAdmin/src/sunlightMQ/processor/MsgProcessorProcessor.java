package sunlightMQ.processor;

import java.util.Hashtable;
import java.util.Vector;

import SunlightFrame.config.Module;
import SunlightFrame.database.DBProxy;
import SunlightFrame.util.DateTimeUtil;
import SunlightFrame.web.CheckList;
import SunlightFrame.web.DataHandle;
import SunlightFrame.web.validate.IntegerCheckItem;
import SunlightFrame.web.validate.StringCheckItem;
import sunlightMQ.AppKeys;
import sunlightMQ.DataCache;

public class MsgProcessorProcessor extends BaseProcessor {
	public MsgProcessorProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	public void makeView() throws Exception {
		String action = getFormData("action");
		if (action.equals("list")) {
			setFormData("q_deletedFlag", "0");
			initPageByQueryDataList("msgProcessor_V", getFormDatas(), "datas", "", new Vector<String>(),
					" order by msgProcessorID asc");
		} else {
			// String[] MsgCenterAndQueueSelect = getMsgCenterAndQueueSelect();
			// setFormData("msgCenterSelect", MsgCenterAndQueueSelect[0]);

			setFormData("msgCenterSelect", makeSelectElementString("msgCenterID",
					DataCache.getInstance().getTableDatas("msgCenter"), "msgCenterID", "name", ""));

			setFormData("queueSelect", makeSelectElementString("queueID", getQueues(), "queueID", "name", ""));

			// setFormData("queueSelect", MsgCenterAndQueueSelect[1]);
			setFormData("callRequestTypeSelect",
					makeSelectElementString("callRequestTypeID", DataCache.getInstance().getTableDatas("c_requestType"),
							"c_requestTypeID", "c_requestTypeName", ""));

			setFormData("callbackMethodTypeSelect",
					makeSelectElementString("callbackMethodTypeID",
							DataCache.getInstance().getTableDatas("c_callbackMethodType"), "c_callbackMethodTypeID",
							"c_callbackMethodTypeName", "selectCallbackMethod()"));
			setFormData("callbackRequestTypeSelect",
					makeSelectElementString("callbackRequestTypeID",
							DataCache.getInstance().getTableDatas("c_requestType"), "c_requestTypeID",
							"c_requestTypeName", ""));
			setFormData("callbackQueueSelect", getCallBackQueueSelect());

			setFormData("queryCondition", makeQueryConditionHtml("msgProcessor_V"));
		}
	}

	public void defaultViewAction() throws Exception {
		listAction();
	}

	public void addViewAction() throws Exception {
		String[] items = { "msgProcessorID" };
		clearDatas(items);
	}

	public void editViewAction() throws Exception {
		getData("msgProcessor", DATA_TYPE_TABLE);
	}

	public boolean confirmActionCheck() throws Exception {
		CheckList list = getChecklist();
		list.addCheckItem(new StringCheckItem("name", "名称", true));
		list.addCheckItem(new IntegerCheckItem("msgCenterID", "消息中心", true));
		list.addCheckItem(new IntegerCheckItem("queueID", "消息队列", true));
		list.addCheckItem(new StringCheckItem("callUrl", "调用接口", true));
		list.addCheckItem(new IntegerCheckItem("callRequestTypeID", "接口请求方式", true));

		list.addCheckItem(new IntegerCheckItem("callbackMethodTypeID", "回调方式", false));
		if (getFormData("callbackMethodTypeID").equals(AppKeys.CALLBACKMETHODTYPE_API)) {
			list.addCheckItem(new StringCheckItem("callbackUrl", "回调接口", true));
			list.addCheckItem(new IntegerCheckItem("callbackRequestTypeID", "回调接口请求方式", true));
		} else if (getFormData("callbackMethodTypeID").equals(AppKeys.CALLBACKMETHODTYPE_QUEUE)) {
			list.addCheckItem(new IntegerCheckItem("callbackQueueID", "回调队列", true));
		}
		list.addCheckItem(new IntegerCheckItem("startThreadNumber", "启动线程数", true));
		list.addCheckItem(new IntegerCheckItem("autoRunning", "是否自动运行", true));
		return list.check();
	}

	public void confirmAction() throws Exception {
		if (getFormData("callbackMethodTypeID").equals(AppKeys.CALLBACKMETHODTYPE_API)) {
			setFormData("callbackQueueID", "");
		} else if (getFormData("callbackMethodTypeID").equals(AppKeys.CALLBACKMETHODTYPE_QUEUE)) {
			setFormData("callbackUrl", "");
			setFormData("callbackRequestTypeID", "");
		} else {
			setFormData("callbackQueueID", "");
			setFormData("callbackUrl", "");
			setFormData("callbackRequestTypeID", "");
		}

		if (!getFormData("toOtherCenter").equals("1")) {// 非跨中心
			setFormData("otherCenterQueue", "");
			setFormData("otherCenterUrl", "");
		}
		String curTime = DateTimeUtil.getCurrentDateTime();
		if (getFormData("msgProcessorID").equals("")) {
			setFormData("addTime", curTime);
			setFormData("deletedFlag", "0");
		}
		setFormData("updateTime", curTime);
		confirmValue("msgProcessor");

		listAction();
	}

	private Vector<Hashtable<String, String>> getQueues() throws Exception {
		Vector<Hashtable<String, String>> queues = new Vector<Hashtable<String, String>>();
		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put("deletedFlag", "0");
		key.put("validFlag", "1");
		queues = DBProxy.query(getConnection(), "queue", key);
		return queues;
	}
}
