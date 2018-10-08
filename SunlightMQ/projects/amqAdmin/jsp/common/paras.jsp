<%@ page contentType="text/html;charset=UTF-8"%>
<%@page import="b2cSystem.DataCache"%>
<%@page import="SunlightFrame.web.JSPDataBean"%>
<%@page import="java.util.Hashtable"%>
<%@page import="java.util.Vector"%>
<% 
	Vector topAspects = DataCache.getInstance().getTopAspects();
	Hashtable aspectHash = (Hashtable) JSPDataBean.getJSPData("aspectHash");
	for (int i = 0; i < topAspects.size(); ++i) {
		Hashtable data = (Hashtable) topAspects.get(i);
		Hashtable aspectHashTmp = (Hashtable) aspectHash.get(data.get("AspectID").toString());
%>
<tr>
	<th><input type="checkbox" name="aspectID<%= i %>"
		id="aspectID<%= i %>" value="<%= data.get("AspectID") %>"
		<%= aspectHashTmp != null ? "checked=\"checked\"" : "" %> /><%= data.get("AspectValue") %>：
		<input type="hidden" name="aspectName<%= i %>" id="aspectName<%= i %>"
		value="<%= data.get("AspectValue") %>" /></th>
	<td>排序值：<input type="text" class="numberInput"
		name="sortIndex<%= i %>" id="sortIndex<%= i %>"
		value="<%= aspectHashTmp != null ? aspectHashTmp.get("sortIndex") : "" %>"
		maxlength="3" /> &nbsp;<label
		title="该平台能获取到的该维度下的好评卖点数,最小为0,最大为<%= AppKeys.MAX_ASPECT_COUNT %>">好评卖点</label>：<input
		type="text" class="numberInput" name="positiveNumber<%= i %>"
		id="positiveNumber<%= i %>"
		value="<%= aspectHashTmp != null ? aspectHashTmp.get("positiveNumber") : "" %>"
		maxlength="11" />个 &nbsp;<label
		title="该平台能获取到的该维度下的差评卖点数,最小为0,最大为<%= AppKeys.MAX_ASPECT_COUNT %>">差评卖点</label>：<input
		type="text" class="numberInput" name="negativeNumber<%= i %>"
		id="negativeNumber<%= i %>"
		value="<%= aspectHashTmp != null ? aspectHashTmp.get("negativeNumber") : "" %>"
		maxlength="11" />个
	</td>
</tr>
<% } %>

<tr>
	<th><span class="red">* </span><label
		title="该平台获取单个维度卖点时候可获取到的好评卖点数，最小为0,最大为<%= AppKeys.MAX_ASPECT_COUNT %>">好评卖点数</label>：</th>
	<td><input type="text" name="positiveAspectCount"
		id="positiveAspectCount"
		value="<%= JSPDataBean.getFormData("positiveAspectCount") %>"
		maxlength="11" class="numberInput" />条</td>
</tr>

<tr>
	<th><span class="red">* </span><label
		title="该平台获取单个维度卖点时候可获取到的差评卖点数，最小为0,最大为<%= AppKeys.MAX_ASPECT_COUNT %>">差评卖点数</label>：</th>
	<td><input type="text" name="negativeAspectCount"
		id="negativeAspectCount"
		value="<%= JSPDataBean.getFormData("negativeAspectCount") %>"
		maxlength="11" class="numberInput" />条</td>
</tr>

<tr>
	<th><span class="red">* </span><label title="该平台获取点评的来源网站">点评来源</label>：</th>
	<td>
		<%
		boolean allSelect = true;
		Vector sources = DataCache.getInstance().getSouces();
		for (int i = 0; i < sources.size(); ++i) {
			Hashtable data = (Hashtable) sources.get(i);
			boolean selected = ("," + JSPDataBean.getFormData("sourceIDs") + ",").indexOf("," + data.get("SourceID").toString() + ",") != -1;
			if (!selected) {
				allSelect = false;
			}
	%> <input type="checkbox"
		onclick="javascript:setSelectedValues('sourceID', 'sourceIDs')"
		name="sourceID" id="sourceID" value="<%= data.get("SourceID") %>"
		<%= selected ? "checked=\"checked\"" : "" %> /><%= data.get("Source") %>&nbsp;
		<% if ((i + 1) % 5 == 0) { %> <br> <% } %> <% } %> <input
		type="checkbox" id="selectAll_window"
		<%= allSelect ? "checked=\"checked\"" : "" %>
		onclick="selectAllCheckBox('selectAll_window', 'sourceID', 'sourceIDs')" /><font
		style="color: red">全选</font>
	</td>
</tr>
<% if (!JSPDataBean.getFormData("viewSelfConfWindow").equals("1")) { %>
<tr>
	<th><span class="red">* </span><label
		title="该平台能获取到的最新点评条数,最小为1,最大为<%= AppKeys.MAX_NEWEST_REVIEWS_COUNT %>">最新点评数</label>：</th>
	<td><input type="text" name="newestCount" id="newestCount"
		value="<%= JSPDataBean.getFormData("newestCount") %>" maxlength="11"
		class="numberInput" />条</td>
</tr>

<tr>
	<th><span class="red">* </span><label
		title="单个卖点可获取的最大点评数量,最大值为<%= AppKeys.MAX_REVIEWS_COUNT %>">单个卖点显示点评数</label>：</th>
	<td><input type="text" class="numberInput" name="reviewCount"
		id="reviewCount" value="<%= JSPDataBean.getFormData("reviewCount") %>"
		maxlength="11" />条&nbsp; <input type="radio" name="showSegmentFlag"
		id="showSegmentFlag" value="0"
		<%= !JSPDataBean.getFormData("showSegmentFlag").equals("1") ? "checked=\"checked\"" : "" %> /><label
		title="在获取点评数据时候将整条点评内容返回">详细</label>&nbsp; <input type="radio"
		name="showSegmentFlag" id="showSegmentFlag" value="1"
		<%= JSPDataBean.getFormData("showSegmentFlag").equals("1") ? "checked=\"checked\"" : "" %> /><label
		title="在获取点评数据时候将点评的部分内容返回">摘要</label>&nbsp;</td>
</tr>
<% } %>

<tr>
	<th><span class="red">* </span><label title="推荐点评可获取的最大数量">推荐点评数量</label>：</th>
	<td><input type="text" class="numberInput"
		name="recommendReviewCount" id="recommendReviewCount"
		value="<%= JSPDataBean.getFormData("recommendReviewCount") %>"
		maxlength="11" />条&nbsp;</td>
</tr>

<tr>
	<th><span class="red">* </span><label title="推荐微博可获取的最大数量">推荐微博数量</label>：</th>
	<td><input type="text" class="numberInput" name="recommendWbCount"
		id="recommendWbCount"
		value="<%= JSPDataBean.getFormData("recommendWbCount") %>"
		maxlength="11" />条&nbsp;</td>
</tr>

<tr>
	<th>显示排名：</th>
	<td><input type="checkbox" name="showRankNoFlag"
		id="showRankNoFlag" value="1"
		<%= JSPDataBean.getFormData("showRankNoFlag").equals("1") ? "checked=\"checked\"" : "" %> />同城排名
		<input type="checkbox" name="showRandNoStarFlag"
		id="showRandNoStarFlag" value="1"
		<%= JSPDataBean.getFormData("showRandNoStarFlag").equals("1") ? "checked=\"checked\"" : "" %> />同城同星级排名
		<% if (!JSPDataBean.getFormData("clientTypeID").equals("10")) { %> <input
		type="checkbox" name="showRandNoGroupFlag" id="showRandNoGroupFlag"
		value="1"
		<%= JSPDataBean.getFormData("showRandNoGroupFlag").equals("1") ? "checked=\"checked\"" : "" %> />集团排名
		<% } %></td>
</tr>

<% if (!JSPDataBean.getFormData("viewSelfConfWindow").equals("1")) { %>
<tr>
	<th><span class="red">* </span>是否为外部酒店ID：</th>
	<td><input type="radio" name="outterHotelIDFlag"
		id="outterHotelIDFlag" value="0"
		<%= !JSPDataBean.getFormData("outterHotelIDFlag").equals("1") ? "checked=\"checked\"" : "" %> />否
		<input type="radio" name="outterHotelIDFlag" id="outterHotelIDFlag"
		value="1"
		<%= JSPDataBean.getFormData("outterHotelIDFlag").equals("1") ? "checked=\"checked\"" : "" %> />是
	</td>
</tr>

<tr>
	<th><span class="red">* </span><label title="该平台可调用接口">可调用接口</label>：</th>
	<td>
		<%
		boolean allSelect2 = true;
		Vector methods = DataCache.getInstance().getTableDatas("c_method");
		for (int i = 0; i < methods.size(); ++i) {
			Hashtable data = (Hashtable) methods.get(i);
			boolean selected = ("," + JSPDataBean.getFormData("methodIDs") + ",").indexOf("," + data.get("c_methodID").toString() + ",") != -1;
			if (!selected) {
				allSelect2 = false;
			}
	%> <input type="checkbox"
		onclick="javascript:setSelectedValues('methodID', 'methodIDs')"
		name="methodID" id="methodID" value="<%= data.get("c_methodID") %>"
		<%= selected ? "checked=\"checked\"" : "" %> /><label
		title="<%= data.get("c_methodInfo") %>">接口<%= i + 1 %></label>&nbsp; <% if ((i + 1) % 5 == 0) { %>
		<br> <% } %> <% } %> <input type="checkbox" id="selectAll_window2"
		<%= allSelect2 ? "checked=\"checked\"" : "" %>
		onclick="selectAllCheckBox('selectAll_window2', 'methodID', 'methodIDs')" /><font
		style="color: red">全选</font>
	</td>
</tr>
<% } %>

<input type="hidden" name="para_clientTypeID" id="para_clientTypeID"
	value="<%= JSPDataBean.getFormData("clientTypeID") %>" />
<input type="hidden" name="viewSelfConfWindow" id="viewSelfConfWindow"
	value="<%= JSPDataBean.getFormData("viewSelfConfWindow") %>" />