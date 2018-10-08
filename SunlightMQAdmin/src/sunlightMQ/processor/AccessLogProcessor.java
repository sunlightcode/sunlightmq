package sunlightMQ.processor;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import SunlightFrame.config.AppConfig;
import SunlightFrame.config.Module;
import SunlightFrame.config.QueryDataList;
import SunlightFrame.database.DBProxy;
import SunlightFrame.util.DateTimeUtil;
import SunlightFrame.util.FileUtil;
import SunlightFrame.web.DataHandle;
import sunlightMQ.AppKeys;
import sunlightMQ.tool.ZipCompressor;

public class AccessLogProcessor extends BaseProcessor {
	public AccessLogProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	public void makeView() throws Exception {
		String action = getFormData("action");
		if (action.equals("list")) {
			String addTimeFrom = getFormData("q_addTimeFrom");
			if (!addTimeFrom.equals("")) {
				setFormData("q_addTimeFrom", addTimeFrom.replace("-", ""));
			}

			String addTimeTo = getFormData("q_addTimeTo");
			if (!addTimeTo.equals("")) {
				setFormData("q_addTimeTo", addTimeTo.replace("-", ""));
			}

			initPageByQueryDataList("accessLog_V", getFormDatas(), "datas", "", new Vector<String>(),
					"order by addTime desc");

			setFormData("q_addTimeFrom", addTimeFrom);
			setFormData("q_addTimeTo", addTimeTo);
		}

		if (!getFormData("action").equals("list")) {
			setFormData("queryCondition", makeQueryConditionHtml("accessLog_V"));
		}
	}

	public void defaultViewAction() throws Exception {
		listAction();
	}

	public void exportAction() throws Exception {
		String addTimeFrom = getFormData("q_addTimeFrom");
		if (!addTimeFrom.equals("")) {
			setFormData("q_addTimeFrom", addTimeFrom.replace("-", ""));
		}

		String addTimeTo = getFormData("q_addTimeTo");
		if (!addTimeTo.equals("")) {
			setFormData("q_addTimeTo", addTimeTo.replace("-", ""));
		}

		int count = getDataListCountByExtendSql("accessLog_V", getFormDatas(), "", new Vector<String>());

		int pageNumber = 100;
		int pageCount = count / pageNumber + (count % pageNumber > 0 ? 1 : 0);

		Vector<Hashtable<String, String>> datas = new Vector<Hashtable<String, String>>();
		QueryDataList querydatalist = AppConfig.getInstance().getQueryDataListConfig().getQueryDataList("accessLog_V");

		Vector<String> keyValueSelect = new Vector<String>();
		String sqlSelect = querydatalist.getPreparedSql(getFormDatas(), keyValueSelect) + "order by addTime desc";

		Vector<String> files = new Vector<String>();
		for (int i = 1; i <= pageCount; ++i) {
			int pageIndex = i;
			Vector<Hashtable<String, String>> tmp = DBProxy.query(getConnection(), "accessLog_V", sqlSelect,
					keyValueSelect, (pageIndex - 1), pageNumber);

			datas.addAll(tmp);
			if (i % 10 == 0 || i == pageCount) {
				files.add(write(datas));
				datas = new Vector<Hashtable<String, String>>();
			}
		}
		if (files.size() > 0) {
			String zipFileName = zipLogXlsFiles(files);

			String downLoadUrl = "location.href='/download?dir=logoutput&fileName=" + zipFileName + "'";
			setControlData("INIT_FUNCTION", downLoadUrl);
		} else {
			setErrorMessage("没有导出任何数据");
		}

		listAction();
	}

	private String zipLogXlsFiles(Vector<String> files) throws Exception {
		String[] filesName = new String[files.size()];
		filesName = files.toArray(filesName);
		String fileDirName = "logoutput";
		String dir = AppKeys.UPLOAD_FILE_PATH + File.separator + fileDirName + File.separator;

		String time = DateTimeUtil.getCurrentDateTime();
		String downloadFileName = time.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "") + ".zip";

		String zipFileName = dir + downloadFileName;

		ZipCompressor zc = new ZipCompressor(zipFileName);
		zc.compress(filesName);

		for (int i = 0; i < files.size(); ++i) {
			File f = new File(files.get(i));
			f.delete();
		}

		return downloadFileName;
	}

	private String write(Vector<Hashtable<String, String>> exportDatas) throws Exception {
		String[] titles = { "账号", "访问接口", "酒店ID", "酒店名称", "酒店地区", "调用时间", "来源IP", "状态", "appKey" };
		String[] fieldNames = { "name", "apiName", "hotelID", "HotelName", "CityName", "addTime", "ip", "statusName",
				"appKey" };
		int[] columnWidths = { 50, 50, 50, 50, 50, 50, 50, 50, 50, 50 };

		String fileName = outputDatasToExcel(exportDatas, "日志", titles, fieldNames, columnWidths);

		String fileDirName = "logoutput";
		String downloadDir = AppConfig.getInstance().getApplicationRoot() + File.separator + "download"
				+ File.separator;

		String dir = AppKeys.UPLOAD_FILE_PATH + File.separator + fileDirName + File.separator;
		FileUtil.moveFile(new File(downloadDir + fileName), dir);

		return dir + fileName;
	}
}
