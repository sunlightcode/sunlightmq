<%@page import="sunlightMQ.DataCache"%>
<%@ page contentType="text/html;charset=UTF-8"%>

<jsp:directive.page import="java.util.Vector" />
<jsp:directive.page import="java.util.Hashtable" />
<%@page import="java.util.Iterator"%>
<jsp:directive.page import="net.fckeditor.FCKeditor" />
<%@page import="SunlightFrame.web.JSPDataBean"%>

<jsp:useBean id="JSPDataBean" scope='request'
	class="SunlightFrame.web.JSPDataBean" />

<%@include file="common/commonHeader.jsp"%>
<div id="mainContent">

	<div class="headDiv" id="headDiv">
		<ul class="label">
			<li class="on"><span><a class="text"
					href="javascript:postModuleAndAction('client', 'defaultView');">接入系统管理</a></span></li>
		</ul>
	</div>

	<div class="main">
		<% if (JSPDataBean.getFormData("action").equals("list")){ %>
		<div class="search" id="searchDiv">
			<div class="right">
				<ul class="buttons">
					<li class="redo-icon"><a
						href="javascript:doAction('recache');">重启缓存</a></li>
					<li class="create-icon"><a
						href="javascript:postModuleAndAction('client','addView')">添加接入服务</a></li>
				</ul>
			</div>
			<div class="clear"></div>
		</div>
		<div id="contentArea" align="center">
			<fieldset>
				<legend align="left">查询条件</legend>
				<table class="maintab" align="center" cellpadding="0"
					cellspacing="0" border="0">
					<tr>
						<td width="100%" style="text-align: left; padding-left: 10px;">
							<!-- ID：<input type="text" name="q_clientID" id="q_clientID" value="<%= JSPDataBean.getFormData("q_clientID") %>" class="numberInput" onkeydown="javascript:if(event.keyCode==13)postModuleAndAction('client', 'search')"/> -->
							系统名称：<input type="text" class="shortInput" name="q_name"
							id="q_name" value="<%= JSPDataBean.getFormData("q_name") %>"
							onkeydown="javascript:if(event.keyCode==13)postModuleAndAction('client', 'search')" />
							服务秘钥：<input type="text" class="shortInput" name="q_appKey"
							id="q_appKey" value="<%= JSPDataBean.getFormData("q_appKey") %>"
							onkeydown="javascript:if(event.keyCode==13)postModuleAndAction('client', 'search')" />
							<!-- 
						开通状态：
						<select id="q_validFlag" name="q_validFlag">
							<option value="">全部</option>
							<option value="1" <%= JSPDataBean.getFormData("q_validFlag").equals("1") ? "selected=\"selected\"" : "" %>>开通</option>
							<option value="0"<%= JSPDataBean.getFormData("q_validFlag").equals("0") ? "selected=\"selected\"" : "" %>>停用</option>
						</select>
						 --> <a class="btn_y"
							onclick="javascript:postModuleAndAction('client', 'search');"><span>查询</span></a>
						</td>
					</tr>
				</table>
			</fieldset>

			<table class="list" border="0" cellpadding="0" cellspacing="1"
				id="tab1" width="97%"
				style="table-layout: fixed; word-break: break-all; overflow: hidden;">
				<tr>
					<!-- <th width="20%">ID</th> -->
					<th>系统名称</th>
					<th>服务秘钥(AppKey)</th>
					<th width="50%">接入服务</th>
					<!-- <th width="20%">状态</th> -->
					<th>操作</th>
				</tr>
				<%
						Vector clients = (Vector) JSPDataBean.getJSPData("clients");
						for (int i = 0; i < clients.size(); i++) {
							Hashtable data = (Hashtable) clients.get(i);
					%>
				<tr>
					<!-- <td><%= data.get("clientID") %></td> -->
					<td><%= data.get("name") %></td>
					<td><%= data.get("appKey") %></td>
					<td width=50%><%= DataCache.getInstance().getTableDataColumnsValue("c_method", data.get("methodIDs").toString(), "c_methodName") %></td>
					<!-- 
						<td>
							<% if (data.get("validFlag").equals("1")) { %>
							<a href="javascript:document.getElementById('clientID').value='<%=data.get("clientID")%>';postModuleAndAction('client','disable')">
							<% } else { %>
							<a href="javascript:document.getElementById('clientID').value='<%=data.get("clientID")%>';postModuleAndAction('client','enable')">
							<% } %>
							<img src="/images/<%= data.get("validFlag").equals("1") ? "yes" : "no"%>.gif" width="15px" border="none"/>
							</a>
						</td>
						 -->
					<td>
						<!-- <a href="javascript:document.getElementById('clientID').value='<%= data.get("clientID") %>';postModuleAndAction('client','detailView')">查看</a> -->
						<a
						href="javascript:document.getElementById('clientID').value='<%= data.get("clientID") %>';postModuleAndAction('client','editView')">编辑</a>
						<a
						href="javascript:if (confirm('是否删除')) {document.getElementById('clientID').value='<%= data.get("clientID") %>';postModuleAndAction('client','delete')}"
						style="color: red;">删除</a>
					</td>
				</tr>
				<%	} %>
			</table>
		</div>

		<div class="page blue" id="pageDiv">
			<p class="floatl"><%@include file="common/commonJumpPage.jsp"%></p>
			<div class="clear"></div>
		</div>
		<% } %>
		<% 
	   if (JSPDataBean.getFormData("action").equals("addView")
	    || JSPDataBean.getFormData("action").equals("editView")
	    || JSPDataBean.getFormData("action").equals("confirm")) {
	%>
		<div class="record">
			<div class="search" id="searchDiv">
				<div class="right">
					<ul class="buttons">
						<li class="submit-icon"><a
							href="javascript:doAction('confirmClient');">提交</a></li>
						<li class="back-icon"><a
							href="javascript: postModuleAndAction('client', 'defaultView')">返回</a></li>
					</ul>
				</div>
				<div class="clear"></div>
			</div>
			<div id="contentArea" align="center">
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<th><span class="red">* </span>名称：</th>
						<td><input type="text" name="name" id="name"
							value="<%=JSPDataBean.getFormData("name")%>" class="normalInput"
							maxlength="50" /></td>
					</tr>
					<% if (!JSPDataBean.getFormData("clientID").equals("")) { %>
					<tr>
						<th>AppKey：</th>
						<td><%= JSPDataBean.getFormData("appKey") %><a
							href="javascript:document.getElementById('clientID').value='<%= JSPDataBean.getFormData("clientID") %>';openInfoWindow('resetAppKeyWindow')">重置AppKey</a>
							<input type="hidden" name="appKey" id="appKey"
							value="<%=JSPDataBean.getFormData("appKey")%>" /></td>
					</tr>
					<tr>
						<th>PrivateKey：</th>
						<td><%= JSPDataBean.getFormData("privateKey") %><a
							href="javascript:document.getElementById('clientID').value='<%= JSPDataBean.getFormData("clientID") %>';openInfoWindow('resetPrivateKeyWindow')">重置PrivateKey</a>
							<input type="hidden" name="privateKey" id="privateKey"
							value="<%=JSPDataBean.getFormData("privateKey")%>" /></td>
					</tr>
					<% } %>

					<tr>
						<th><span class="red">* </span><label title="该平台可调用接口">可调用服务</label>：</th>
						<td>
							<%
					boolean allSelect2 = true;
					Vector methods = DataCache.getInstance().getTableDatas("c_method");
					for (int i = 0; i < methods.size(); ++i) {
						Hashtable data = (Hashtable) methods.get(i);
						boolean selected = ("," + JSPDataBean.getFormData("methodIDs") + ",").indexOf(data.get("c_methodID").toString()) != -1;
						if (!selected) {
							allSelect2 = false;
						}
				%> <input type="checkbox"
							onclick="javascript:setSelectedValues('methodID', 'methodIDs')"
							name="methodID" id="methodID"
							value="<%= data.get("c_methodID") %>"
							<%= selected ? "checked=\"checked\"" : "" %> /><label
							title="<%= data.get("c_methodInfo") %>"><%= data.get("c_methodInfo") %></label>&nbsp;
							<% if ((i + 1) % 5 == 0) { %> <br> <% } %> <% } %>
						</td>
					</tr>
				</table>
			</div>
		</div>

		<input type="hidden" name="methodIDs" id="methodIDs"
			value="<%= JSPDataBean.getFormData("methodIDs") %>" />
		<% } %>

		<% 
	   if (JSPDataBean.getFormData("action").equals("detailView")) {
	%>
		<div class="record">
			<div class="search" id="searchDiv">
				<div class="right">
					<ul class="buttons">
						<li class="back-icon"><a
							href="javascript: postModuleAndAction('client', 'defaultView')">返回</a></li>
					</ul>
				</div>
				<div class="clear"></div>
			</div>
			<div id="contentArea" align="center">
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<th>名称：</th>
						<td><%=JSPDataBean.getFormData("name")%></td>
					</tr>

					<% if (!JSPDataBean.getFormData("clientID").equals("")) { %>
					<tr>
						<th>AppKey：</th>
						<td><%= JSPDataBean.getFormData("appKey") %></td>
					</tr>
					<tr>
						<th>PrivateKey：</th>
						<td><%= JSPDataBean.getFormData("privateKey") %></td>
					</tr>
					<% } %>

					<tr>
						<th><span class="red">* </span><label title="该平台可调用接口">接入服务</label>：</th>
						<td>
							<%
					boolean allSelect2 = true;
					Vector methods = DataCache.getInstance().getTableDatas("c_method");
					for (int i = 0; i < methods.size(); ++i) {
						Hashtable data = (Hashtable) methods.get(i);
						boolean selected = ("," + JSPDataBean.getFormData("methodIDs") + ",").indexOf(data.get("c_methodID").toString()) != -1;
						if (!selected) {
							allSelect2 = false;
						}
				%> <input type="checkbox" name="methodID" id="methodID"
							value="<%= data.get("c_methodID") %>"
							<%= selected ? "checked=\"checked\"" : "" %> /><label
							title="<%= data.get("c_methodInfo") %>"><%= data.get("c_methodName") %></label>&nbsp;
							<% if ((i + 1) % 5 == 0) { %> <br> <% } %> <% } %>
						</td>
					</tr>

				</table>
			</div>
		</div>
		<% } %>
	</div>
</div>

<input type="hidden" id="clientID" name="clientID"
	value="<%= JSPDataBean.getFormData("clientID")%>" />

<% if (!JSPDataBean.getFormData("action").equals("list")) { %>
<%= JSPDataBean.getFormData("queryCondition") %>
<% } %>

<%@include file="common/commonFooter.jsp"%>
