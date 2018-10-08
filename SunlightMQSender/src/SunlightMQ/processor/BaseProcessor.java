package SunlightMQ.processor;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import SunlightMQ.DataCache;
import SunlightFrame.config.AppConfig;
import SunlightFrame.config.Module;
import SunlightFrame.config.QueryCondition;
import SunlightFrame.config.QueryDataList;
import SunlightFrame.database.DBProxy;
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

	public String getClusterRequestIPInfo() {
		if (getRequest().getHeader("X-Real-IP") != null) {
			return getRequest().getHeader("X-Real-IP");
		}
		return super.getRequestIPInfo();
	}

	public String getQueueServerSelect() throws Exception {
		Hashtable<String, String> k = new Hashtable<String, String>();
		// k.put("deletedFlag", "0");
		Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), "queueServer", k);
		String str = makeSelectElementString("queueServerID", datas, "queueServerID", "name", "", "", true, "", "1");
		return str;
	}

	public String getQueryQueueServerSelect() throws Exception {
		Hashtable<String, String> k = new Hashtable<String, String>();
		// k.put("deletedFlag", "0");
		Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), "queueServer", k);
		String str = makeSelectElementString("q_queueServerID", datas, "queueServerID", "name", "", "", true, "", "1");
		return str;
	}

	public String getQueueSelect() throws Exception {
		Hashtable<String, String> k = new Hashtable<String, String>();
		k.put("deletedFlag", "0");
		Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), "queue", k);
		String str = makeSelectElementString("queueID", datas, "queueID", "name", "", "", true, "", "1");
		return str;
	}

	public String getQueryQueueSelect() throws Exception {
		Hashtable<String, String> k = new Hashtable<String, String>();
		k.put("deletedFlag", "0");
		Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), "queue", k);
		String str = makeSelectElementString("q_queueID", datas, "queueID", "name", "", "", true, "", "1");
		return str;
	}

	public String getMsgCenterSelect() throws Exception {
		Hashtable<String, String> k = new Hashtable<String, String>();
		k.put("deletedFlag", "0");
		Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), "msgCenter", k);
		String str = makeSelectElementString("msgCenterID", datas, "msgCenterID", "appID", "", "", true, "", "1");
		return str;
	}

	public String getQueryMsgCenterSelect() throws Exception {
		Hashtable<String, String> k = new Hashtable<String, String>();
		k.put("deletedFlag", "0");
		Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), "msgCenter", k);
		String str = makeSelectElementString("q_msgCenterID", datas, "msgCenterID", "appID", "", "", true, "", "1");
		return str;
	}

	public String[] getMsgCenterAndQueueSelect() throws Exception {
		Hashtable<String, String> k = new Hashtable<String, String>();
		k.put("deletedFlag", "0");
		Vector<Hashtable<String, String>> datas = DBProxy.query(getConnection(), "msgCenter", k);
		String str = makeSelectElementString("msgCenterID", datas, "msgCenterID", "appID", "doAction('selectQueue')",
				"", true, "", "1");

		Vector<Hashtable<String, String>> queues = new Vector<Hashtable<String, String>>();
		if (!getFormData("msgCenterID").equals("")) {
			k.put("msgCenterID", getFormData("msgCenterID"));
			Hashtable<String, String> msgCenter = DBProxy.query(getConnection(), "msgCenter", k).get(0);

			Hashtable<String, String> k2 = new Hashtable<String, String>();
			k2.put("queueServerID", msgCenter.get("queueServerID"));
			queues = DBProxy.query(getConnection(), "queue", k2);
		}
		String str2 = makeSelectElementString("queueID", queues, "queueID", "name", "", "", true, "", "1");

		return new String[] { str, str2 };
	}
}
