package SunlightMQ.service;

import java.util.Vector;

import SunlightFrame.exception.FrameException;
import SunlightFrame.web.validate.CheckItem;

public class Checker {
	private Vector<CheckItem> items;
	private Service service;

	Checker(Service service) {
		items = new Vector<CheckItem>();
		this.service = service;
	}

	public void addCheckItem(CheckItem checkitem) {
		items.add(checkitem);
	}

	public boolean check() throws FrameException {
		String tmpParaValue;
		CheckItem checkItem;
		String checkResultMsg;
		for (int i = 0; i < items.size(); i++) {
			checkItem = items.get(i);
			tmpParaValue = service.getPara(checkItem.getItemID());
			checkResultMsg = checkItem.getCheckResultMessage(tmpParaValue);
			if (!checkResultMsg.equals("")) {
				service.setErrorMsg("code", "00099");
				service.setErrorMsg("ERROR_" + checkItem.getItemID(), checkResultMsg);
				return false;
			}
		}

		return true;
	}
}
