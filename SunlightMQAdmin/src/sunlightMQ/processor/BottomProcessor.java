package sunlightMQ.processor;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import SunlightFrame.config.Module;
import SunlightFrame.util.DateTimeUtil;
import SunlightFrame.web.DataHandle;
import SunlightFrame.web.FrameKeys;

public class BottomProcessor extends BaseProcessor {
	public BottomProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	@SuppressWarnings("unchecked")
	public void makeView() throws Exception {
		Hashtable<String, String> adminUserDatas = (Hashtable<String, String>) getSessionData(FrameKeys.LOGIN_USER);
		setFormData("userName", adminUserDatas.get("userName"));
		setFormData("date", DateTimeUtil.getCurrentDate());
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		String dayOfWeekStr = "";
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		if (dayOfWeek < 8 && dayOfWeek > 0) {
			dayOfWeekStr = weekDays[dayOfWeek - 1];
		}

		setFormData("dayOfWeekStr", dayOfWeekStr);
	}

	public void defaultViewAction() {
	}
}
