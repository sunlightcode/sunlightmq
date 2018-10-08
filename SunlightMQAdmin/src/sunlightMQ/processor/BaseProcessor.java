package sunlightMQ.processor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import sunlightMQ.DataCache;
import sunlightMQ.tool.RandomCodeGenerator;
import SunlightFrame.config.AppConfig;
import SunlightFrame.config.Module;
import SunlightFrame.config.QueryCondition;
import SunlightFrame.config.QueryDataList;
import SunlightFrame.database.DBProxy;
import SunlightFrame.database.IndexGenerater;
import SunlightFrame.util.DateTimeUtil;
import SunlightFrame.util.StringUtil;
import SunlightFrame.web.AbstractModuleProcessor;
import SunlightFrame.web.DataHandle;

public abstract class BaseProcessor extends AbstractModuleProcessor {
	public BaseProcessor(Module module, DataHandle dataHandle) {
		super(module, dataHandle);
	}

	public void startProcess() throws Exception {
	}

	public void endProcess() throws Exception {
	}

	public void listAction() throws Exception {
		setFormData("action", "list");
	}

	/**
	 * 获得数据库表某字段列的统计和
	 * 
	 * @param tableName
	 *            数据库表名
	 * @param fieldName
	 *            求和自动名
	 * @param keyID
	 *            筛选字段名
	 * @param keyValue
	 *            筛选值
	 */
	public int getSumValue(String tableName, String fieldName, String keyID, String keyValue) throws Exception {
		String preparedSql = "select sum(" + fieldName + ") as sum from " + tableName + " where " + keyID + " = '"
				+ keyValue + "'";
		String sum = DBProxy.query(getConnection(), preparedSql, new Vector<String>()).get(0).get("sum");
		if (sum.equals("")) {
			return 0;
		} else {
			return Integer.parseInt(sum);
		}
	}

	public void changeValidFlag(String tableName, String validFlag) throws Exception {
		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put(tableName + "ID", getFormData(tableName + "ID"));

		Hashtable<String, String> value = new Hashtable<String, String>();
		value.put("validFlag", validFlag);

		DBProxy.update(getConnection(), tableName, key, value);
	}

	public void getData(String tableName, int dataType) throws Exception {
		String id = tableName + "ID";
		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put(id, getFormData(id));

		if (dataType == DATA_TYPE_TABLE) {
			setFormData(DBProxy.query(getConnection(), tableName, key).get(0));
		} else if (dataType == DATA_TYPE_VIEW) {
			setFormData(DBProxy.query(getConnection(), tableName + "_V", key).get(0));
		}
	}

	public void deleteData(String tableName) throws Exception {
		String id = tableName + "ID";
		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put(id, getFormData(id));

		DBProxy.delete(getConnection(), tableName, key);
	}

	public boolean normalImageCheck() throws Exception {
		if (getUploadFiles().size() == 0) {
			setErrorMessage("请上传一张正确的图片");
			return false;
		} else if (!isImageFile(getUploadFiles().get(0))) {
			getUploadFiles().get(0).delete();
			setErrorMessage("请上传一张正确的图片");
			return false;
		} else {
			return true;
		}
	}

	public boolean isImageFile(File f) {
		String fileName = f.getName().toLowerCase();
		if (!fileName.endsWith("jpeg") && !fileName.endsWith("gif") && !fileName.endsWith("png")
				&& !fileName.endsWith("logo") && !fileName.endsWith("jpg")) {
			f.delete();
			return false;
		}
		return true;
	}

	public boolean isExcelFile(File f) {
		String fileName = f.getName().toLowerCase();
		if (!fileName.endsWith("xls")) {
			f.delete();
			return false;
		}

		return true;
	}

	public void confirmValue(String tableName) throws Exception {
		String id = tableName + "ID";

		if (getFormData(id).equals("")) {
			setFormData(id, IndexGenerater.getTableIndex(tableName, getConnection()));
			setFormData("validFlag", "1");

			DBProxy.insert(getConnection(), tableName, getFormDatas());
		} else {
			Hashtable<String, String> key = new Hashtable<String, String>();
			key.put(id, getFormData(id));

			DBProxy.update(getConnection(), tableName, key, getFormDatas());
		}
	}

	public String confirmValue2(String tableName) throws Exception {
		String id = tableName + "ID";

		String idValue = "";
		if (getFormData(id).equals("")) {
			idValue = IndexGenerater.getTableIndex(tableName, getConnection());
			setFormData(id, idValue);
			setFormData("validFlag", "1");

			DBProxy.insert(getConnection(), tableName, getFormDatas());
		} else {
			Hashtable<String, String> key = new Hashtable<String, String>();
			idValue = getFormData(id);
			key.put(id, getFormData(id));

			DBProxy.update(getConnection(), tableName, key, getFormDatas());
		}

		return idValue;
	}

	public void disableAction() throws Exception {
		changeValidFlag(getFormData("module"), "0");
		listAction();
	}

	public void enableAction() throws Exception {
		changeValidFlag(getFormData("module"), "1");
		listAction();
	}

	public String getValidFlagQuerySelect() throws Exception {
		return makeSelectElementString("q_validFlag", getConstantValues(getConnection(), "c_validStatus"),
				"c_validStatusID", "c_validStatusName", "");
	}

	public void initPageByQueryDataList(String queryDataListName, Hashtable<String, String> condition,
			String jspDataName) throws Exception {
		int pageIndex = 1;
		try {
			pageIndex = Integer.parseInt(getFormData("pageIndex"));
			if (pageIndex < 1) {
				pageIndex = 1;
			}
		} catch (Exception e) {
			pageIndex = 1;
		}
		setFormData("pageIndex", String.valueOf(pageIndex));

		int count = getDataListCount(queryDataListName, condition);
		int pageNumber = getPageNumber(queryDataListName);
		Vector<Hashtable<String, String>> datas = getDataListDatas(queryDataListName, condition);
		if (count > 0 && datas.size() == 0 && pageIndex > 1) {
			pageIndex = pageIndex - 1;
			setFormData("pageIndex", String.valueOf(pageIndex));
			datas = getDataListDatas(queryDataListName, condition);
		}

		setJSPData(jspDataName, datas);
		setJumpPageInfo(count, pageNumber);
	}

	public void initPageByQueryDataList(String queryDataListName, Hashtable<String, String> condition,
			String jspDataName, String extendSql, Vector<String> extendValues, String sortSql) throws Exception {
		int pageIndex = 1;
		try {
			pageIndex = Integer.parseInt(getFormData("pageIndex"));
			if (pageIndex < 1) {
				pageIndex = 1;
			}
		} catch (Exception e) {
			pageIndex = 1;
		}

		int count = getDataListCountByExtendSql(queryDataListName, condition, extendSql, extendValues);
		int pageNumber = getPageNumber(queryDataListName);
		int pageCount = count / pageNumber + (count % pageNumber > 0 ? 1 : 0);

		if (pageIndex > pageCount && pageCount > 0) {
			pageIndex = pageCount;
		}
		setFormData("pageIndex", String.valueOf(pageIndex));

		Vector<Hashtable<String, String>> datas = getDataListByExtendSql(queryDataListName, condition, extendSql,
				extendValues, sortSql);
		if (count > 0 && datas.size() == 0 && pageIndex > 1) {
			pageIndex = pageIndex - 1;
			setFormData("pageIndex", String.valueOf(pageIndex));
			datas = getDataListDatas(queryDataListName, condition);
		}

		setJSPData(jspDataName, datas);
		setJumpPageInfo(count, pageNumber);
	}

	public int getDataListCountByExtendSql(String queryDataListName, Hashtable<String, String> condition,
			String extendSql, Vector<String> extendValues) throws Exception {
		QueryDataList querydatalist = AppConfig.getInstance().getQueryDataListConfig()
				.getQueryDataList(queryDataListName);

		Vector<String> keyValueSelect = new Vector<String>();
		String sqlSelect = querydatalist.getPreparedCountSql(condition, keyValueSelect);

		if (!extendSql.equals("")) {
			sqlSelect += ((sqlSelect.indexOf("where") == -1 ? " where " : " and ") + extendSql);
			keyValueSelect.addAll(extendValues);
		}

		int count = Integer.parseInt(DBProxy.query(getConnection(), sqlSelect, keyValueSelect).get(0).get("COUNT"));

		return count;

	}

	public Vector<Hashtable<String, String>> getDataListByExtendSql(String queryDataListName,
			Hashtable<String, String> condition, String extendSql, Vector<String> extendValues, String sortSql)
			throws Exception {
		initPageIndex();
		int pageIndex = Integer.parseInt(getFormData("pageIndex"));
		QueryDataList querydatalist = AppConfig.getInstance().getQueryDataListConfig()
				.getQueryDataList(queryDataListName);

		Vector<String> keyValueSelect = new Vector<String>();
		String sqlSelect = querydatalist.getPreparedSql(condition, keyValueSelect);

		if (!extendSql.equals("")) {
			sqlSelect += ((sqlSelect.indexOf("where") == -1 ? " where " : " and ") + extendSql);
			keyValueSelect.addAll(extendValues);
		}

		if (!sortSql.equals("")) {
			sqlSelect += (" " + sortSql);
		}

		Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), queryDataListName, sqlSelect,
				keyValueSelect, (pageIndex - 1), querydatalist.getPageNumber());

		return datas;
	}

	public String makeSelectElementString(String selectID, Vector<Hashtable<String, String>> sourceDatas,
			String optionValue, String optionText, String onchangeEvent, String cssName, boolean allowEmptyOption,
			String fillEmptyText, String validFlag) throws Exception {
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append((new StringBuilder("<select id='")).append(selectID).append("' name='").append(selectID)
				.append("'").append(" class='").append(cssName).append("'").append(onchangeEvent.equals("") ? ""
						: (new StringBuilder(" onchange=javascript:")).append(onchangeEvent).toString())
				.append(">").toString());

		if (allowEmptyOption) {
			stringbuffer.append("<option value=''>");
			stringbuffer.append(fillEmptyText);
			stringbuffer.append("</option>");
		}
		for (int i = 0; i < sourceDatas.size(); i++) {
			Hashtable<String, String> hashtable = sourceDatas.get(i);
			if (validFlag.equals("1")) {
				if (hashtable.get("validFlag") == null || hashtable.get("validFlag").equals("1")) {
					stringbuffer.append(
							(new StringBuilder("<option value='")).append(hashtable.get(optionValue)).append("'")
									.append((hashtable.get(optionValue)).equals(getFormData(selectID)) ? " selected"
											: "")
									.append(">").append(hashtable.get(optionText)).append("</option>").toString());
				}
			} else if (validFlag.equals("0")) {
				if (hashtable.get("validFlag") == null || hashtable.get("validFlag").equals("0")) {
					stringbuffer.append(
							(new StringBuilder("<option value='")).append(hashtable.get(optionValue)).append("'")
									.append((hashtable.get(optionValue)).equals(getFormData(selectID)) ? " selected"
											: "")
									.append(">").append(hashtable.get(optionText)).append("</option>").toString());
				}
			} else {
				stringbuffer
						.append((new StringBuilder("<option value='")).append(hashtable.get(optionValue)).append("'")
								.append((hashtable.get(optionValue)).equals(getFormData(selectID)) ? " selected" : "")
								.append(">").append(hashtable.get(optionText)).append("</option>").toString());
			}
		}

		stringbuffer.append("</select>");
		return stringbuffer.toString();
	}

	public void clearQueryCondition(String queryDataListName) throws Exception {
		QueryDataList queryDataList = AppConfig.getInstance().getQueryDataListConfig()
				.getQueryDataList(queryDataListName);
		Vector<QueryCondition> conditionList = queryDataList.getListConditions();
		String[] items = new String[conditionList.size()];
		for (int i = 0; i < conditionList.size(); ++i) {
			String expressionName = conditionList.get(i).getExpression();
			expressionName = expressionName.substring(expressionName.indexOf("$") + 1, expressionName.lastIndexOf("$"));
			items[i] = expressionName;
		}
		clearDatas(items);
	}

	public int getDataListCountByIDs(String queryDataListName, Hashtable<String, String> condition, String[] idArray,
			String idColumnName) throws Exception {
		QueryDataList querydatalist = AppConfig.getInstance().getQueryDataListConfig()
				.getQueryDataList(queryDataListName);

		Vector<String> keyValueCount = new Vector<String>();
		String sqlCount = querydatalist.getPreparedCountSql(getFormDatas(), keyValueCount);

		if (idArray.length > 0) {
			String extendSql = (sqlCount.indexOf("where") == -1 ? "where" : "and") + " " + idColumnName + " in (";
			for (int i = 0; i < idArray.length; ++i) {
				extendSql += (i == 0 ? "?" : ",?");
				keyValueCount.add(idArray[i]);
			}
			extendSql += " ) ";
			sqlCount += extendSql;
		}
		int count = Integer.parseInt(DBProxy.query(getConnection(), sqlCount, keyValueCount).get(0).get("count"));

		return count;
	}

	public Vector<Hashtable<String, String>> getDataListByIDs(String queryDataListName,
			Hashtable<String, String> condition, String[] idArray, String idColumnName) throws Exception {
		initPageIndex();
		int pageIndex = Integer.parseInt(getFormData("pageIndex"));
		QueryDataList querydatalist = AppConfig.getInstance().getQueryDataListConfig()
				.getQueryDataList(queryDataListName);

		Vector<String> keyValueSelect = new Vector<String>();
		String sqlSelect = querydatalist.getPreparedSql(getFormDatas(), keyValueSelect);

		if (idArray.length > 0) {
			String extendSql = (sqlSelect.indexOf("where") == -1 ? "where" : "and") + " " + idColumnName + " in (";
			for (int i = 0; i < idArray.length; ++i) {
				extendSql += (i == 0 ? "?" : ",?");
				keyValueSelect.add(idArray[i]);
			}
			extendSql += " ) ";
			sqlSelect += extendSql;
		}
		Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), queryDataListName, sqlSelect,
				keyValueSelect, (pageIndex - 1), querydatalist.getPageNumber());

		return datas;
	}

	public String makeQueryConditionHtml(String queryDataListName) throws Exception {
		QueryDataList queryDataList = AppConfig.getInstance().getQueryDataListConfig()
				.getQueryDataList(queryDataListName);
		Vector<QueryCondition> conditionList = queryDataList.getListConditions();

		String queryConditionHtml = "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < conditionList.size(); ++i) {
			String expressionName = conditionList.get(i).getExpression();
			expressionName = expressionName.substring(expressionName.indexOf("$") + 1, expressionName.lastIndexOf("$"));
			sb.append("<input type=\"hidden\" name=\"" + expressionName + "\" id=\"" + expressionName + "\" value=\""
					+ getFormData(expressionName) + "\" />");
		}
		queryConditionHtml = sb.toString();
		return queryConditionHtml;
	}

	public Vector<Hashtable<String, String>> getDatasByIDs(String[] arrayID, String tableOrViewName,
			String idColumnName, Hashtable<String, String> key) throws Exception {
		Vector<Hashtable<String, String>> datas = null;

		if (arrayID != null && arrayID.length > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append("select * from ");
			sb.append(tableOrViewName);
			sb.append(" where ");
			sb.append(idColumnName);
			sb.append(" in (");

			Vector<String> values = new Vector<String>();
			for (int i = 0; i < arrayID.length; ++i) {
				sb.append(i == 0 ? "?" : ",?");
				values.add(arrayID[i]);
			}

			sb.append(")");

			Iterator<String> iter = key.keySet().iterator();
			while (iter.hasNext()) {
				String keyName = iter.next();
				String keyValue = key.get(keyName);
				sb.append(" and ");
				sb.append(keyName);
				sb.append(" = ? ");
				values.add(keyValue);
			}

			String sql = sb.toString();
			datas = DBProxy.query(getConnection(), sql, values);
		}

		return datas == null ? new Vector<Hashtable<String, String>>() : datas;
	}

	public Vector<String> getValueNotInVector(String[] values, Vector<Hashtable<String, String>> datas, String key) {
		Vector<String> result = new Vector<String>();
		for (int i = 0; i < values.length; i++) {
			String tmpID = values[i];
			int j = 0;
			for (; j < datas.size(); j++) {
				Hashtable<String, String> tmpData = datas.get(j);
				if (tmpID.equals(tmpData.get(key))) {
					break;
				}
			}

			if (j == datas.size()) {
				result.add(tmpID);
			}
		}

		return result;
	}

	public void searchAction() throws Exception {
		setFormData("pageIndex", "1");
		listAction();
	}

	public String getClientTypeSelect() throws Exception {
		return makeSelectElementString("clientTypeID", DataCache.getInstance().getTableDatas("c_clientType"),
				"c_clientTypeID", "c_clientTypeName", "", "shortSel", true, "请选择", "");
	}

	public String getQueryClientTypeSelect() throws Exception {
		return makeSelectElementString("q_clientTypeID", DataCache.getInstance().getTableDatas("c_clientType"),
				"c_clientTypeID", "c_clientTypeName", "", "shortSel", true, "", "");
	}

	public Hashtable<String, String> getSessionKey() throws Exception {
		return new Hashtable<String, String>();
	}

	public String getPrivateKey(String appKey) throws Exception {
		Hashtable<String, String> client = DataCache.getInstance().getClient(appKey);
		return client.get("privateKey") == null ? "" : client.get("privateKey");
	}

	public Hashtable<String, String> getClient(String appKey) throws Exception {
		return DataCache.getInstance().getClient(appKey);
	}

	public String createAppKey(String clientID) throws Exception {
		int totalLen = 16;
		int randomCodeLen = totalLen - clientID.length();
		return clientID + RandomCodeGenerator.generateCode(randomCodeLen);
	}

	public String createPrivateKey(String clientID) throws Exception {
		int totalLen = 16;
		int randomCodeLen = totalLen - clientID.length();
		return clientID + RandomCodeGenerator.generateCode(randomCodeLen);
	}

	public String createDESPrivateKey(String clientID) throws Exception {
		return RandomCodeGenerator.generateCode(8);
	}

	public Vector<Hashtable<String, String>> getDatasInIDArray(String[] arrayID, String tableOrViewName,
			String idColumnName, Hashtable<String, String> key) throws Exception {
		Vector<Hashtable<String, String>> datas = null;

		if (arrayID != null && arrayID.length > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append("select * from ");
			sb.append(tableOrViewName);
			sb.append(" where ");
			sb.append(idColumnName);
			sb.append(" in (");

			Vector<String> values = new Vector<String>();
			for (int i = 0; i < arrayID.length; ++i) {
				sb.append(i == 0 ? "?" : ",?");
				values.add(arrayID[i]);
			}

			sb.append(")");

			Iterator<String> iter = key.keySet().iterator();
			while (iter.hasNext()) {
				String keyName = iter.next();
				String keyValue = key.get(keyName);
				sb.append(" and ");
				sb.append(keyName);
				sb.append(" = ? ");
				values.add(keyValue);
			}

			String sql = sb.toString();
			datas = DBProxy.query(getConnection(), sql, values);
		}

		return datas == null ? new Vector<Hashtable<String, String>>() : datas;
	}

	public boolean isExistsAppKey(String clientID, String appKey) throws Exception {
		String sql = "select * from client where appKey = ? ";
		Vector<String> v = new Vector<String>();
		v.add(appKey);
		if (!clientID.equals("")) {
			sql += " and clientID != ?";
			v.add(clientID);
		}

		return DBProxy.query(getConnection(), sql, v).size() > 0;
	}

	public Hashtable<String, String> getDefaultPara() throws Exception {
		Hashtable<String, String> key = new Hashtable<String, String>();
		key.put("defaultParaID", "1");

		return DBProxy.query(getConnection(), "defaultPara", key).get(0);
	}

	public Hashtable<String, Hashtable<String, String>> getAspectHash(String aspectIDs) {
		Hashtable<String, Hashtable<String, String>> aspectHash = new Hashtable<String, Hashtable<String, String>>();
		String[] aspectArray = StringUtil.split(aspectIDs, ";");
		String[] e;
		Hashtable<String, String> tmpHash;
		for (int i = 0; i < aspectArray.length; ++i) {
			e = StringUtil.split(aspectArray[i], ":");
			tmpHash = new Hashtable<String, String>();
			tmpHash.put("aspectID", e[0]);
			tmpHash.put("sortIndex", e.length > 1 ? e[1] : "");
			tmpHash.put("positiveNumber", e.length > 2 ? e[2] : "");
			tmpHash.put("negativeNumber", e.length > 3 ? e[3] : "");
			aspectHash.put(e[0], tmpHash);
		}

		return aspectHash;
	}

	public Vector<Hashtable<String, String>> getAspectVector(String aspectIDs) {
		Vector<Hashtable<String, String>> aspectVector = new Vector<Hashtable<String, String>>();
		String[] aspectArray = StringUtil.split(aspectIDs, ";");
		String[] e;
		Hashtable<String, String> tmpHash;
		for (int i = 0; i < aspectArray.length; ++i) {
			e = StringUtil.split(aspectArray[i], ":");
			tmpHash = new Hashtable<String, String>();
			tmpHash.put("aspectID", e[0]);
			tmpHash.put("sortIndex", e.length > 1 ? e[1] : "");
			tmpHash.put("positiveNumber", e.length > 2 ? e[2] : "");
			tmpHash.put("negativeNumber", e.length > 3 ? e[3] : "");
			aspectVector.add(tmpHash);
		}

		return aspectVector;
	}

	public String outputDatasToExcel(Vector<Hashtable<String, String>> outputDiamondDatas, String headTitle,
			String titles[], String fieldNames[], int[] columnWidths) throws Exception {
		String time = DateTimeUtil.getCurrentDateTime();
		String downloadFileName = time.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "") + ".xls";
		String downloadDir = AppConfig.getInstance().getApplicationRoot() + File.separator + "download"
				+ File.separator;

		// String headTitle = downloadFileName.replace(".xls", "") +
		// GlobalUtil.getItem("exported_diamonds",
		// getFormData(AppKeys.LANGUAGE));
		//
		// String[] titles = { "编号", "重量", "价格", "形状", "切工", "颜色", "净度", "证书",
		// "证书编号", "抛光",
		// "对称度", "荧光", "底面", "腰带", "尺寸", "深度", "台面", "产地"};
		//
		// String[] fieldNames = { "diamondID", "weight", "price", "shapeName",
		// "cutName", "colorName", "clarityName", "certName", "certNo",
		// "polishName",
		// "symmetryName", "fluorName", "culetName", "girdleName",
		// "measurements", "depth", "tab", "origin"};

		// int[] columnWidths = { 20, 10, 15, 10, 10, 10, 10, 10, 15, 15,
		// 15, 10, 10, 10, 30, 10, 10, 15};

		WritableCellFormat cellFormat = new WritableCellFormat();
		cellFormat.setBorder(Border.ALL, jxl.format.BorderLineStyle.THIN);
		cellFormat.setAlignment(Alignment.LEFT);
		cellFormat.setVerticalAlignment(VerticalAlignment.TOP);
		cellFormat.setWrap(true);
		WritableCellFormat[] cellFormats = new WritableCellFormat[titles.length];
		for (int i = 0; i < cellFormats.length; ++i) {
			cellFormats[i] = cellFormat;
		}

		writeExcel(downloadDir + downloadFileName, false, headTitle, titles, fieldNames, columnWidths, cellFormats,
				outputDiamondDatas, null, new int[0]);

		return downloadFileName;
	}

	public void writeExcel(String filePath, boolean isLandscapePageSetup, String headTitle, String[] titles,
			String[] fieldNames, int[] columnWidths, WritableCellFormat[] cellFormats,
			Vector<Hashtable<String, String>> datas, String mergeID, int[] mergeColumns) throws Exception {
		OutputStream os = null;
		try {
			if (mergeID != null) {
				for (int i = 0; i < datas.size(); i++) {
					Hashtable<String, String> data = datas.get(i);
					for (int j = i + 1; j < datas.size(); j++) {
						Hashtable<String, String> data2 = datas.get(j);
						if (data2.get(mergeID).equals(data.get(mergeID))) {
							data2.put(mergeID, "");
							for (int m = 0; m < mergeColumns.length; m++) {
								data2.put(fieldNames[mergeColumns[m]], "");
							}
						} else {
							i = j - 1;
							break;
						}
						if (j == datas.size() - 1) {
							i = datas.size() - 1;
						}
					}
				}
			}

			os = new FileOutputStream(filePath);
			WritableWorkbook wwb = Workbook.createWorkbook(os);

			WritableSheet ws = wwb.createSheet(headTitle, 0);
			// if (isLandscapePageSetup) {
			// ws.setPageSetup(PageOrientation.LANDSCAPE);
			// }
			// 设置页边距 (注意默认单位是英寸 1d = 2.54cm)
			ws.getSettings().setTopMargin(0.7 / 2.54d);
			ws.getSettings().setBottomMargin(1.3 / 2.54d);
			ws.getSettings().setLeftMargin(0.7 / 2.54d);
			ws.getSettings().setRightMargin(0.7 / 2.54d);
			// 页眉边距
			ws.getSettings().setHeaderMargin(0.7 / 2.54d);
			// // 页脚边距
			ws.getSettings().setFooterMargin(0.7 / 2.54d);
			// ws.getSettings().setFooter(
			// new HeaderFooter(headTitle + " 第 &P 页，共 &N 页"));

			// 标题
			WritableFont wf = new WritableFont(WritableFont.TIMES, 20, WritableFont.BOLD, false);
			WritableCellFormat headFormat = new WritableCellFormat(wf);
			headFormat.setAlignment(jxl.format.Alignment.CENTRE);
			headFormat.setVerticalAlignment(VerticalAlignment.CENTRE);

			// 表头
			WritableCellFormat titleFormat = new WritableCellFormat();
			titleFormat.setBorder(Border.ALL, jxl.format.BorderLineStyle.THIN); // 设置
																				// border
																				// 格式
			titleFormat.setAlignment(jxl.format.Alignment.CENTRE);
			titleFormat.setBackground(Colour.GRAY_25);
			titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);

			Label label = new Label(0, 0, headTitle, headFormat);
			ws.addCell(label);
			ws.mergeCells(0, 0, fieldNames.length - 1, 0);
			ws.setRowView(0, 800);

			for (int i = 0; i < titles.length; i++) {
				Label labelCF = new Label(i, 1, titles[i], titleFormat);
				ws.addCell(labelCF);
				ws.setColumnView(i, columnWidths[i]);
			}

			for (int i = 0; i < datas.size(); i++) {
				Hashtable<String, String> rowData = datas.get(i);
				rowData.put("null", "");
				for (int j = 0; j < fieldNames.length; j++) {
					Label labelCF = new Label(j, i + 2, rowData.get(fieldNames[j]), cellFormats[j]);
					ws.addCell(labelCF);
				}
			}

			if (mergeID != null) {
				for (int i = 0; i < datas.size(); i++) {
					if (!datas.get(i).get(mergeID).equals("")) {
						int startIndex = i;
						int endIndex = datas.size();
						for (int j = i + 1; j < datas.size(); j++) {
							if (!datas.get(j).get(mergeID).equals("")) {
								endIndex = j;
								break;
							}
						}
						for (int m = 0; m < mergeColumns.length; m++) {
							if (startIndex + 2 != endIndex + 2 - 1) {
								ws.mergeCells(mergeColumns[m], startIndex + 2, mergeColumns[m], endIndex + 2 - 1);
							}
						}
					}
				}
			}

			wwb.write();
			wwb.close();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (Exception e) {
			}
		}
	}

	public String getQueryClientSelect() throws Exception {
		return makeSelectElementString("q_clientID", DataCache.getInstance().getClients(), "clientID", "name", "",
				"shortSel", true, "", "");
	}

	public String getClusterRequestIPInfo() {
		if (getRequest().getHeader("X-Real-IP") != null) {
			return getRequest().getHeader("X-Real-IP");
		}
		return super.getRequestIPInfo();
	}

	/***
	 * 获取某个表的子表(即分别后的表)
	 * 
	 * @param tableName
	 * @param hotelID
	 * @return
	 */
	public String getSubTableName(String tableName, String hotelID) {
		return tableName + "_Sub" + getHotelIDMod(hotelID);
	}

	private int getHotelIDMod(String hotelID) {
		int hotelIDInt = Integer.parseInt(hotelID);
		int mod = hotelIDInt % 100;

		return mod;
	}

	public boolean isExistsOutterHotelID(String clientID, String client_hotelID, String outterHotelID)
			throws Exception {
		Hashtable<String, String> clientHotelK = new Hashtable<String, String>();
		clientHotelK.put("clientID", clientID);
		clientHotelK.put("outterHotelID", outterHotelID);
		String extendSql = "";
		if (!client_hotelID.equals("")) {
			extendSql = " and client_hotelID != " + client_hotelID;
		}

		return DBProxy.query(getConnection(), "client_hotel", clientHotelK, extendSql).size() > 0;
	}

	public String getMQServerSelect() throws Exception {
		Hashtable<String, String> k = new Hashtable<String, String>();
		Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), "mqServer", k);
		String str = makeSelectElementString("mqServerID", datas, "mqServerID", "name", "", "", true, "", "1");
		return str;
	}

	public String getQueryQueueServerSelect() throws Exception {
		Hashtable<String, String> k = new Hashtable<String, String>();
		Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), "mqServer", k);
		String str = makeSelectElementString("q_mqServerID", datas, "mqServerID", "name", "", "", true, "", "1");
		return str;
	}

	public String getQueueSelect() throws Exception {
		Hashtable<String, String> k = new Hashtable<String, String>();
		Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), "queue", k);
		String str = makeSelectElementString("queueID", datas, "queueID", "name", "", "", true, "", "1");
		return str;
	}

	public String getCallBackQueueSelect() throws Exception {
		Hashtable<String, String> k = new Hashtable<String, String>();
		Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), "queue", k);
		String str = makeSelectElementString("callbackQueueID", datas, "queueID", "name", "", "", true, "", "1");
		return str;
	}

	public String getQueryQueueSelect() throws Exception {
		Hashtable<String, String> k = new Hashtable<String, String>();
		Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), "queue", k);
		String str = makeSelectElementString("q_c_queueName", datas, "name", "name", "", "", true, "", "1");
		return str;
	}

	public String getMsgCenterSelect() throws Exception {
		Hashtable<String, String> k = new Hashtable<String, String>();
		Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), "msgCenter", k);
		String str = makeSelectElementString("msgCenterID", datas, "msgCenterID", "name", "", "", true, "", "1");
		return str;
	}

	public String getQueryMsgCenterSelect() throws Exception {
		Hashtable<String, String> k = new Hashtable<String, String>();
		Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), "msgCenter", k);
		String str = makeSelectElementString("q_msgCenterID", datas, "msgCenterID", "name", "", "", true, "", "1");
		return str;
	}

	public String[] getMsgCenterAndQueueSelect() throws Exception {
		Hashtable<String, String> k = new Hashtable<String, String>();
		Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), "msgCenter", k);
		String str = makeSelectElementString("msgCenterID", datas, "msgCenterID", "name", "doAction('selectQueue')", "",
				true, "", "1");

		Vector<Hashtable<String, String>> queues = new Vector<Hashtable<String, String>>();
		if (!getFormData("msgCenterID").equals("")) {
			k.put("msgCenterID", getFormData("msgCenterID"));
			Hashtable<String, String> msgCenter = DBProxy.query(getConnection(), "msgCenter", k).get(0);

			Hashtable<String, String> k2 = new Hashtable<String, String>();
			k2.put("mqServerID", msgCenter.get("mqServerID"));
			queues = DBProxy.query(getConnection(), "queue", k2);
		}
		String str2 = makeSelectElementString("queueID", queues, "queueID", "name", "", "", true, "", "1");

		return new String[] { str, str2 };
	}

	public boolean isExistsData(String table, String primaryKey, String primaryKeyValue, String columnName,
			String columnValue) throws Exception {
		Hashtable<String, String> k = new Hashtable<String, String>();
		k.put(columnName, columnValue);
		Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), table, k);
		if (datas.size() == 0) {
			return false;
		}

		Hashtable<String, String> data = datas.get(0);
		if (!data.get(primaryKey).equals(primaryKeyValue)) {
			return true;
		}

		return false;
	}
}
