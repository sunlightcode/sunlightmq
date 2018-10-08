package SunlightMQ.processor;

import SunlightMQ.CheckMQConnectionProcessorThread;
import SunlightFrame.config.Module;
import SunlightFrame.web.DataHandle;

public class CheckProcessor extends BaseProcessor {

	public CheckProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void makeView() throws Exception {

	}

	public void defaultAction() throws Exception {
		if (!CheckMQConnectionProcessorThread.isConnected) {
			throw new RuntimeException();
		}
	}

}
