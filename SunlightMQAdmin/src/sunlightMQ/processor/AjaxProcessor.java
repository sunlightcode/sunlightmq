package sunlightMQ.processor;

import SunlightFrame.config.Module;
import SunlightFrame.web.DataHandle;
import sunlightMQ.processor.ajax.AjaxRefreshItemProcessor;

public class AjaxProcessor extends AjaxRefreshItemProcessor {

	public AjaxProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}
}
