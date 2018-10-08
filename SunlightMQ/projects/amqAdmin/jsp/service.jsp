<%@ page contentType="text/html;charset=UTF-8"%>

<jsp:directive.page import="java.util.Vector" />
<jsp:directive.page import="java.util.Hashtable" />
<%@page import="java.util.Iterator"%>
<jsp:directive.page import="net.fckeditor.FCKeditor" />
<%@page import="SunlightFrame.web.JSPDataBean"%>
<%@page import="sunlightMQ.AppUtil"%>
<%@page import="sunlightMQ.AppKeys"%>
<%@page import="sunlightMQ.DataCache"%>

<jsp:useBean id="JSPDataBean" scope='request'
	class="SunlightFrame.web.JSPDataBean" />

<%@include file="common/commonHeader.jsp"%>
<div id="mainContent">

	<div class="headDiv" id="headDiv">
		<ul class="label">
			<li class="on"><span><a class="text"
					href="javascript:postModuleAndAction('service', 'defaultView');">首页&nbsp;->&nbsp;接入服务管理</a></span></li>
		</ul>
	</div>

	<div class="main">
		<% if (JSPDataBean.getFormData("action").equals("list")){ %>
		<div class="search" id="searchDiv">
			<div class="right">
				<ul class="buttons">
					<li class="create-icon"><a
						href="javascript:postModuleAndAction('service','addView')">添加服务</a></li>
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
							<!-- ID：<input type="text" name="q_c_methodID" id="q_c_methodID" value="<%= JSPDataBean.getFormData("q_c_methodID") %>" class="numberInput" onkeydown="javascript:if(event.keyCode==13)postModuleAndAction('service', 'search')"/> -->
							服务名称：<input type="text" class="shortInput" name="q_c_methodName"
							id="q_c_methodName"
							value="<%= JSPDataBean.getFormData("q_c_methodName") %>"
							onkeydown="javascript:if(event.keyCode==13)postModuleAndAction('service', 'search')" />
							队列名称： <%= JSPDataBean.getFormData("queueSelect") %> &nbsp;<a
							class="btn_y"
							onclick="javascript:postModuleAndAction('service', 'search');"><span>查询</span></a>
						</td>
					</tr>
				</table>
			</fieldset>

			<table class="list" border="0" cellpadding="0" cellspacing="1"
				id="tab1" width="97%" align="center">
				<tr>
					<!-- <th>ID</th> -->
					<th>服务名称(bussinessName)</th>
					<th>队列名称</th>
					<th>描述</th>
					<th>操作</th>
				</tr>
				<%
				Vector datas = (Vector) JSPDataBean.getJSPData("datas");
				for (int i = 0; i < datas.size(); i++) {
					Hashtable data = (Hashtable) datas.get(i);
			%>
				<tr>
					<!-- <td><%= data.get("c_methodID") %></td> -->
					<td><%= data.get("c_methodName") %></td>
					<td><%= data.get("c_queueName") %></td>
					<td><%= data.get("c_methodInfo") %></td>
					<td><a
						href="javascript:document.getElementById('c_methodID').value='<%= data.get("c_methodID") %>';postModuleAndAction('service','editView')">编辑</a>
						<a
						href="javascript:document.getElementById('c_methodID').value='<%= data.get("c_methodID") %>';postModuleAndAction('service','delete')">删除</a>
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
							href="javascript:postModuleAndAction('service', 'confirm');">提交</a></li>
						<li class="back-icon"><a
							href="javascript: postModuleAndAction('service', 'defaultView')">返回</a></li>
					</ul>
				</div>
				<div class="clear"></div>
			</div>
			<div id="contentArea" align="center">
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<th><span class="red">* </span>服务名称：</th>
						<td><input type="text" name="c_methodName" id="c_methodName"
							value="<%=JSPDataBean.getFormData("c_methodName")%>"
							class="normalInput" maxlength="100" /></td>
					</tr>
					<tr>
						<th><span class="red">* </span>队列名称：</th>
						<td><%= JSPDataBean.getFormData("queueSelect") %></td>
					</tr>
					<tr>
						<th><span class="red">* </span>描述：</th>
						<td><input type="text" name="c_methodInfo" id="c_methodInfo"
							value="<%=JSPDataBean.getFormData("c_methodInfo")%>"
							class="normalInput" maxlength="100" /></td>
					</tr>
				</table>
			</div>
		</div>
		<% } %>
	</div>
</div>

<input type="hidden" id="c_methodID" name="c_methodID"
	value="<%= JSPDataBean.getFormData("c_methodID")%>" />
<%@include file="common/commonFooter.jsp"%>
