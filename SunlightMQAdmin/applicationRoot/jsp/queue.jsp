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
					href="javascript:postModuleAndAction('queue', 'defaultView');">消息队列管理</a></span></li>
		</ul>
	</div>

	<div class="main">
		<% if (JSPDataBean.getFormData("action").equals("list")){ %>
		<div class="search" id="searchDiv">
			<div class="right">
				<ul class="buttons">
					<li class="create-icon"><a
						href="javascript:postModuleAndAction('queue','addView')">添加消息队列</a></li>
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
							<!-- ID：<input type="text" name="q_queueID" id="q_queueID" value="<%= JSPDataBean.getFormData("q_queueID") %>" class="numberInput" onkeydown="javascript:if(event.keyCode==13)postModuleAndAction('queue', 'search')"/> -->
							队列名称：<input type="text" class="shortInput" name="q_name"
							id="q_name" value="<%= JSPDataBean.getFormData("q_name") %>"
							onkeydown="javascript:if(event.keyCode==13)postModuleAndAction('queue', 'search')" />

							队列类型： <select id="q_queueType" name="q_queueType">
								<option value="">全部类型</option>
								<option value="queue"
									<%= JSPDataBean.getFormData("q_queueType").equals("queue") ? "selected=\"selected\"" : "" %>>普通对列</option>
								<option value="topic"
									<%= JSPDataBean.getFormData("q_queueType").equals("topic") ? "selected=\"selected\"" : "" %>>订阅队列</option>
						</select> 队列描述：<input type="text" class="shortInput" name="q_info"
							id="q_info" value="<%= JSPDataBean.getFormData("q_info") %>"
							onkeydown="javascript:if(event.keyCode==13)postModuleAndAction('queue', 'search')" />
							<!-- 
						是否持久化：
						<select id="q_persistentFlag" name="q_persistentFlag">
							<option value="">全部</option>
							<option value="1" <%= JSPDataBean.getFormData("q_persistentFlag").equals("1") ? "selected=\"selected\"" : "" %>>是</option>
							<option value="0" <%= JSPDataBean.getFormData("q_persistentFlag").equals("0") ? "selected=\"selected\"" : "" %>>否</option>
						</select>
						--> <a class="btn_y"
							onclick="javascript:postModuleAndAction('queue', 'search');"><span>查询</span></a>
						</td>
					</tr>
				</table>
			</fieldset>

			<table class="list" border="0" cellpadding="0" cellspacing="1"
				id="tab1" width="97%" align="center">
				<tr>
					<!--
				<th>ID</th>
				-->
					<th>队列名称</th>
					<th>队列类型</th>
					<th>队列描述</th>
					<!--
				<th>是否持久化</th>
				<th>优先级</th>
				-->
					<th>消息有效期(ms)</th>
					<!--				
				<th>是否持久订阅</th>
				<th>签收模式</th>
				-->
					<!--
				<th>异常处理方式</th>
				<th>异常报告方式</th>
				<th>异常回调地址</th>
				<th>异常回调请求方法</th>
				<th>状态</th>
				-->
					<th>操作</th>
				</tr>
				<%
				Vector datas = (Vector) JSPDataBean.getJSPData("datas");
				for (int i = 0; i < datas.size(); i++) {
					Hashtable data = (Hashtable) datas.get(i);
			%>
				<tr>
					<!--
				<td><%= data.get("queueID") %></td>
				-->
					<td><%= data.get("name") %></td>
					<td><%= data.get("queueType").equals("queue") ? "普通队列" : "订阅队列" %></td>
					<td><%= data.get("info") %></td>
					<!-- 
				<td><%= data.get("persistentFlag").equals("1") ? "是" : "否" %></td>
				<td><%= DataCache.getInstance().getTableDataColumnValue("c_priority", data.get("priorityID").toString(), "c_priorityName") %></td>				
				-->
					<td><%= data.get("timeToLive") %></td>
					<!-- 
				<td><%= data.get("durableFlag").equals("1") ? "是" : "否" %></td>
				<td><%= DataCache.getInstance().getTableDataColumnValue("c_acknowledType", data.get("acknowledTypeID").toString(), "c_acknowledTypeName") %></td>
				-->
					<!--
				<td><%= DataCache.getInstance().getTableDataColumnValue("c_processExceptionMode", data.get("processExceptionModeID").toString(), "c_processExceptionModeName") %></td>
				<td><%= DataCache.getInstance().getTableDataColumnValue("c_processExceptionReportMode", data.get("processExceptionReportModeID").toString(), "c_processExceptionReportModeName") %></td>
				<td><%= data.get("processExceptionUrl") %></td>
				<td><%= DataCache.getInstance().getTableDataColumnValue("c_requestType", data.get("processExceptionRequestTypeID").toString(), "c_requestTypeName") %></td>
				<td>
					<% if (data.get("validFlag").equals("1")) { %>
					<a href="javascript:document.getElementById('queueID').value='<%=data.get("queueID")%>';postModuleAndAction('queue','disable')">
					<% } else { %>
					<a href="javascript:document.getElementById('queueID').value='<%=data.get("queueID")%>';postModuleAndAction('queue','enable')">
					<% } %>
					<img src="/images/<%= data.get("validFlag").equals("1") ? "yes" : "no"%>.gif" width="15px" border="none"/>
					</a>
				</td>
				-->
					<td><a
						href="javascript:document.getElementById('queueID').value='<%= data.get("queueID") %>';postModuleAndAction('queue','editView')">编辑</a>
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
							href="javascript:postModuleAndAction('queue', 'confirm');">提交</a></li>
						<li class="back-icon"><a
							href="javascript: postModuleAndAction('queue', 'defaultView')">返回</a></li>
					</ul>
				</div>
				<div class="clear"></div>
			</div>
			<div id="contentArea" align="center">
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<th width="30%"><span class="red">* </span>队列名称：</th>
						<td><input type="text" name="name" id="name"
							value="<%=JSPDataBean.getFormData("name")%>" class="normalInput"
							maxlength="50" /></td>
					</tr>
					<tr>
						<th><span class="red">* </span>队列描述：</th>
						<td><input type="text" name="info" id="info"
							value="<%=JSPDataBean.getFormData("info")%>" class="normalInput"
							maxlength="100" /></td>
					</tr>
					<tr>
						<th><span class="red">* </span>队列类型：</th>
						<td><select id="queueType" name="queueType">
								<option value="queue"
									<%= JSPDataBean.getFormData("queueType").equals("queue") ? "selected=\"selected\"" : "" %>>普通对列</option>
								<option value="topic"
									<%= JSPDataBean.getFormData("queueType").equals("topic") ? "selected=\"selected\"" : "" %>>订阅队列</option>
						</select></td>
					</tr>
					<tr>
						<!-- 
				<td>
					<input type="hidden" name="timeToLive" id="timeToLive" value="10000" />				
				</td>
			 -->
						<th><span class="red">* </span>消息有效期(ms)：</th>
						<td><input type="text" name="timeToLive" id="timeToLive"
							value="<%= JSPDataBean.getFormData("timeToLive").equals("") ? "86400000" : JSPDataBean.getFormData("timeToLive") %>"
							class="normalInput" maxlength="11" /></td>
					</tr>
					<tr>
						<td><input type="hidden" name="persistentFlag"
							id="persistentFlag" value="1" /></td>
						<!-- 
				<th><span class="red">* </span>是否持久化：</th>
				<td>
					<input type="radio" name="persistentFlag" id="persistentFlag" value="1" <%= !JSPDataBean.getFormData("persistentFlag").equals("1") ? "checked=\"checked\"" : "" %> />是
					<input type="radio" name="persistentFlag" id="persistentFlag" value="0" <%= JSPDataBean.getFormData("persistentFlag").equals("1") ? "checked=\"checked\"" : "" %> />否
				</td>
				-->
					</tr>
					<tr>
						<td><input type="hidden" name="priorityID" id="priorityID"
							value="6" /></td>
						<!-- 
				<th><span class="red">* </span>优先级：</th>
				<td><%= JSPDataBean.getFormData("prioritySelect") %></td>
				 -->
					</tr>
					<tr>
						<td><input type="hidden" name="durableFlag" id="durableFlag"
							value="1" /></td>
						<!--
				<th><span class="red">* </span>是否持久订阅：</th>
				<td>
					<input type="radio" name="durableFlag" id="durableFlag" value="1" <%= !JSPDataBean.getFormData("durableFlag").equals("1") ? "checked=\"checked\"" : "" %> />是
					<input type="radio" name="durableFlag" id="durableFlag" value="0" <%= JSPDataBean.getFormData("durableFlag").equals("1") ? "checked=\"checked\"" : "" %> />否
				</td>
				-->
					</tr>
					<tr>
						<td><input type="hidden" name="acknowledTypeID"
							id="acknowledTypeID" value="3" /></td>
						<!--
				<th><span class="red">* </span>签收模式：</th>
				<td><%= JSPDataBean.getFormData("acknowledTypeSelect") %></td>
				-->
					</tr>
					<tr>
						<td><input type="hidden" name="processExceptionModeID"
							id="processExceptionModeID" value="1" /></td>
						<!-- 			
				<th><span class="red">* </span>异常处理方式：</th>
				<td><%= JSPDataBean.getFormData("processExceptionModeSelect") %></td>
				 -->
					</tr>
					<tr>
						<td><input type="hidden" name="processExceptionReportModeID"
							id="processExceptionReportModeID" value="2" /></td>
						<!--			
				<th><span class="red">* </span>异常报告方式：</th>
				<td><%= JSPDataBean.getFormData("processExceptionReportModeSelect") %></td>
				-->
					</tr>
					<tr class="processException_tr"
						style="display: <%= JSPDataBean.getFormData("processExceptionReportModeID").equals(AppKeys.PROCESSEXCEPTIONREPORTMODE_CALLBACK) ? "" : "none" %>">
						<th><span class="red">* </span>异常回调地址：</th>
						<td><input type="text" name="processExceptionUrl"
							id="processExceptionUrl"
							value="<%=JSPDataBean.getFormData("processExceptionUrl")%>"
							class="normalInput" maxlength="200" /></td>
					</tr>
					<tr class="processException_tr"
						style="display: <%= JSPDataBean.getFormData("processExceptionReportModeID").equals(AppKeys.PROCESSEXCEPTIONREPORTMODE_CALLBACK) ? "" : "none" %>">
						<th><span class="red">* </span>异常回调请求方法：</th>
						<td><%= JSPDataBean.getFormData("processExceptionRequestTypeSelect") %></td>
					</tr>

				</table>
			</div>
		</div>
		<% } %>
	</div>
</div>

<script>
	function selectProcessExceptionReportMode() {
		var processExceptionReportModeID = getSelectValue('processExceptionReportModeID');
		if (processExceptionReportModeID == '<%= AppKeys.PROCESSEXCEPTIONREPORTMODE_CALLBACK %>') {
			$('.processException_tr').show();
		} else {
			$('.processException_tr').hide();
		}
	}
</script>
<input type="hidden" id="queueID" name="queueID"
	value="<%= JSPDataBean.getFormData("queueID")%>" />
<%@include file="common/commonFooter.jsp"%>
