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
					href="javascript:postModuleAndAction('msgCenter', 'defaultView');">消息中心管理</a></span></li>
		</ul>
	</div>

	<div class="main">
		<% if (JSPDataBean.getFormData("action").equals("list")){ %>
		<div class="search" id="searchDiv">
			<div class="right">
				<ul class="buttons">
					<li class="create-icon"><a
						href="javascript:postModuleAndAction('msgCenter','addView')">添加消息中心</a></li>
				</ul>
			</div>
			<div class="clear"></div>
		</div>
		<div id="contentArea" align="center">
			<table class="list" border="0" cellpadding="0" cellspacing="1"
				id="tab1" width="97%" align="center">
				<tr>
					<th>ID</th>
					<th>名称</th>
					<th>消息服务器</th>
					<th>状态</th>
					<th>操作</th>
				</tr>
				<%
				Vector datas = (Vector) JSPDataBean.getJSPData("datas");
				for (int i = 0; i < datas.size(); i++) {
					Hashtable data = (Hashtable) datas.get(i);
			%>
				<tr>
					<td><%= data.get("msgCenterID") %></td>
					<td><%= data.get("name") %></td>
					<td><%= data.get("mqServerName") %></td>
					<td>
						<% if (data.get("validFlag").equals("1")) { %> <a
						href="javascript:document.getElementById('msgCenterID').value='<%=data.get("msgCenterID")%>';postModuleAndAction('msgCenter','disable')">
							<% } else { %> <a
							href="javascript:document.getElementById('msgCenterID').value='<%=data.get("msgCenterID")%>';postModuleAndAction('msgCenter','enable')">
								<% } %> <img
								src="/images/<%= data.get("validFlag").equals("1") ? "yes" : "no"%>.gif"
								width="15px" border="none" />
						</a>
					</td>
					<td><a
						href="javascript:document.getElementById('msgCenterID').value='<%= data.get("msgCenterID") %>';postModuleAndAction('msgCenter','editView')">编辑</a>
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
							href="javascript:postModuleAndAction('msgCenter', 'confirm');">提交</a></li>
						<li class="back-icon"><a
							href="javascript: postModuleAndAction('msgCenter', 'defaultView')">返回</a></li>
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
							maxlength="100" /></td>
					</tr>
					<tr>
						<th><span class="red">* </span>消息服务器：</th>
						<td><%= JSPDataBean.getFormData("mqServerSelect") %></td>
					</tr>
				</table>
			</div>
		</div>
		<% } %>
	</div>
</div>

<input type="hidden" id="msgCenterID" name="msgCenterID"
	value="<%= JSPDataBean.getFormData("msgCenterID")%>" />
<%@include file="common/commonFooter.jsp"%>
