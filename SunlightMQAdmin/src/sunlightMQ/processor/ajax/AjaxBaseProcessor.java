package sunlightMQ.processor.ajax;

import java.lang.reflect.Method;

import SunlightFrame.config.Module;
import SunlightFrame.log.AppLogger;
import SunlightFrame.web.DataHandle;
import SunlightFrame.web.FrameKeys;
import sunlightMQ.AppKeys;
import sunlightMQ.processor.BaseProcessor;

public class AjaxBaseProcessor extends BaseProcessor {

	public AjaxBaseProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	public void makeView() throws Exception {

	}

	public void refreshhiddenSpanAction() throws Exception {
		String ajaxAction = getFormData("ajaxAction");
		if (!isUserLogined() && !ajaxAction.equals("frontLogin")) {
			setFormData(AppKeys.AJAX_RESULT, "location.reload()");
			return;
		}

		try {
			Class<? extends AjaxBaseProcessor> c = this.getClass();
			Method method = c.getMethod(ajaxAction, new Class[0]);
			method.invoke(this, new Object[0]);
		} catch (Exception e) {
			AppLogger.getInstance().errorLog("error happend when ajax action:" + ajaxAction, e);
			setFormData(AppKeys.AJAX_RESULT, "alert('服务器未响应，请稍候重试')");
		}
	}

	public void refreshwindowInsideDIVAction() throws Exception {
		String ajaxAction = getFormData("ajaxAction");

		try {
			Class<? extends AjaxBaseProcessor> c = this.getClass();
			Method method = c.getMethod(ajaxAction, new Class[0]);
			method.invoke(this, new Object[0]);
			dispatch("popUpWindow/" + getFormData("ajaxAction") + ".jsp");
		} catch (Exception e) {
			AppLogger.getInstance().errorLog("error happend when ajax action:" + ajaxAction, e);
			setFormData(AppKeys.AJAX_RESULT, "alert('服务器未响应，请稍候重试')");
			dispatch("popUpWindow/javascript.jsp");
		}
	}

	protected void setAjaxInfoMessage(String message) {
		setFormData(AppKeys.AJAX_RESULT, "alert('" + message + "');");
	}

	protected void setAjaxDispatch(String link) {
		setFormData(AppKeys.AJAX_RESULT, "dispatch('" + link + "')");
	}

	protected void setErrorMessageAndFocusItem() {
		setErrorMessageAndFocusItem(getControlDatas().get(FrameKeys.ERROR_MESSAGE),
				getControlDatas().get(FrameKeys.FOCUS_ITEM));
	}

	protected void setErrorMessageAndFocusItem(String message, String focusItem) {
		setFormData(AppKeys.AJAX_RESULT,
				"alert('" + message + "');document.getElementById('" + focusItem + "').focus();");
	}

	protected void setAjaxJavascript(String script) {
		setFormData(AppKeys.AJAX_RESULT, script);
	}

	protected void setAjaxJavascriptInWindow(String script) {
		setFormData(AppKeys.AJAX_RESULT, script);
		setFormData("ajaxAction", "javascript");
	}
}
