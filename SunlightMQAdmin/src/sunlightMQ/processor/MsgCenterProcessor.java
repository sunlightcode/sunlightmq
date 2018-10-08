package sunlightMQ.processor;

import java.util.Vector;

import SunlightFrame.config.Module;
import SunlightFrame.util.DateTimeUtil;
import SunlightFrame.web.CheckList;
import SunlightFrame.web.DataHandle;
import SunlightFrame.web.validate.IntegerCheckItem;
import SunlightFrame.web.validate.StringCheckItem;

public class MsgCenterProcessor extends BaseProcessor {
	public MsgCenterProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	public void makeView() throws Exception {
		String action = getFormData("action");
		if (action.equals("list")) {
			initPageByQueryDataList("msgCenter_V", getFormDatas(), "datas", "", new Vector<String>(), "");
		} else {
			setFormData("mqServerSelect", getMQServerSelect());
			setFormData("queryCondition", makeQueryConditionHtml("msgCenter_V"));
		}
	}

	public void defaultViewAction() throws Exception {
		listAction();
	}

	public void addViewAction() throws Exception {
		String[] items = { "msgCenterID" };
		clearDatas(items);
	}

	public void editViewAction() throws Exception {
		getData("msgCenter", DATA_TYPE_TABLE);
	}

	public boolean confirmActionCheck() throws Exception {
		CheckList list = getChecklist();
		list.addCheckItem(new StringCheckItem("name", "名称", true));
		list.addCheckItem(new IntegerCheckItem("mqServerID", "消息服务器", true));
		if (list.check()) {
			if (isExistsData("msgCenter", "msgCenterID", getFormData("msgCenterID"), "name", getFormData("name"))) {
				setErrorMessage("名称重复");
				return false;
			}
		}
		return list.check();
	}

	public void confirmAction() throws Exception {
		String curTime = DateTimeUtil.getCurrentDateTime();
		if (getFormData("msgCenterID").equals("")) {
			setFormData("addTime", curTime);
			setFormData("deletedFlag", "0");
		}
		setFormData("updateTime", curTime);
		confirmValue("msgCenter");

		listAction();
	}
}
