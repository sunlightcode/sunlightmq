package sunlightMQ.processor;

import java.util.Hashtable;

import SunlightFrame.config.AppConfig;
import SunlightFrame.config.Module;
import SunlightFrame.database.DBProxy;
import SunlightFrame.web.DataHandle;
import sunlightMQ.DataCache;

public class RecacheProcessor extends BaseProcessor {

	public RecacheProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	@Override
	public void makeView() throws Exception {
	}

	public boolean recacheActionCheck() throws Exception {
		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put("systemUserID", getFormData("recacheSystemUserID"));
		key.put("password", getFormData("recachePassword"));

		if (DBProxy.query(getConnection(), "systemUser", key).size() == 0) {
			setInfoMessage("false");
			return false;
		}

		return true;
	}

	public void recacheAction() throws Exception {
		AppConfig.getInstance().reload();
		DataCache.getInstance().loadData();
		setInfoMessage("true");
	}

	public boolean resetHotelCacheActionCheck() throws Exception {
		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put("systemUserID", getFormData("recacheSystemUserID"));
		key.put("password", getFormData("recachePassword"));

		if (DBProxy.query(getConnection(), "systemUser", key).size() == 0) {
			setInfoMessage("false");
			return false;
		}

		return true;
	}
}
