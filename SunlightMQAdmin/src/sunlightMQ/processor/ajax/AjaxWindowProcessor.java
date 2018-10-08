package sunlightMQ.processor.ajax;

import SunlightFrame.config.Module;
import SunlightFrame.web.DataHandle;

public class AjaxWindowProcessor extends AjaxActionProcessor {

	public AjaxWindowProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	public void resetSystemUserPasswordWindow() throws Exception {
	}

	public void resetDesKeyWindow() throws Exception {
		getData("client", DATA_TYPE_TABLE);
		setFormData("newDesKey", createDESPrivateKey(getFormData("clientID")));
	}

	public void resetAppKeyWindow() throws Exception {
		getData("client", DATA_TYPE_TABLE);
	}

	public void viewPrivateKeyWindow() throws Exception {
		getData("client", DATA_TYPE_TABLE);
	}

	public void viewPublicKeyWindow() throws Exception {
		getData("client", DATA_TYPE_TABLE);
	}

	public void viewPrivateKeyPKCS8Window() throws Exception {
		getData("client", DATA_TYPE_TABLE);
	}

	public void operationMsgProcessorWindow() throws Exception {
	}
}
