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
					href="javascript:postModuleAndAction('exceptionLog', 'defaultView');">首页&nbsp;->&nbsp;错误日志查询</a></span></li>
		</ul>
	</div>

	<div class="main">
		<% if (JSPDataBean.getFormData("action").equals("list")){ %>
		<!-- 
		<div class="search" id="searchDiv">
			<div class="clear"></div>
		</div>
		 -->
		<div id="contentArea" align="center">
			<fieldset>
				<legend align="left">查询条件</legend>
				<table class="maintab" align="center" cellpadding="0"
					cellspacing="0" border="0">
					<tr>
						<td width="100%" style="text-align: left; padding-left: 10px;">
							<!-- 
						ID：<input type="text" name="q_processExceptionLogID" id="q_processExceptionLogID" value="<%= JSPDataBean.getFormData("q_processExceptionLogID") %>" class="numberInput" onkeydown="javascript:if(event.keyCode==13)postModuleAndAction('exceptionLog', 'search')"/>
						消息中心名称：<input type="text" class="shortInput" name="q_msgCenterName" id="q_msgCenterName" value="<%= JSPDataBean.getFormData("q_msgCenterName") %>"  onkeydown="javascript:if(event.keyCode==13)postModuleAndAction('exceptionLog', 'search')"/>
					 --> 消息队列：<input type="text" class="shortInput"
							name="q_msgProcessorName" id="q_msgProcessorName"
							value="<%= JSPDataBean.getFormData("q_msgProcessorName") %>"
							onkeydown="javascript:if(event.keyCode==13)postModuleAndAction('exceptionLog', 'search')" />
							消息内容：<input type="text" class="shortInput" name="q_messageJson"
							id="q_messageJson"
							value="<%= JSPDataBean.getFormData("q_messageJson") %>"
							onkeydown="javascript:if(event.keyCode==13)postModuleAndAction('exceptionLog', 'search')" />
							异常内容：<input type="text" class="shortInput"
							name="q_exceptionContent" id="q_exceptionContent"
							value="<%= JSPDataBean.getFormData("q_exceptionContent") %>"
							onkeydown="javascript:if(event.keyCode==13)postModuleAndAction('exceptionLog', 'search')" />
							<a class="btn_y"
							onclick="javascript:postModuleAndAction('exceptionLog', 'search');"><span>查询</span></a>
						</td>
					</tr>
				</table>
			</fieldset>
			<table class="list" border="0" cellpadding="0" cellspacing="1"
				id="tab1" width="97%" align="center">
				<tr>
					<!-- <th>ID</th> -->
					<th>异常时间</th>
					<th>消息对列</th>
					<th>异常位置</th>
					<th>异常类型</th>
					<th>所在消息中心</th>
					<!--<th>消息处理器IP</th>
				<th>消息ID</th> -->
					<th>消息内容</th>
					<th>异常内容</th>

					<!-- <th>操作</th> -->
				</tr>
				<%
				Vector datas = (Vector) JSPDataBean.getJSPData("datas");
				for (int i = 0; i < datas.size(); i++) {
					Hashtable data = (Hashtable) datas.get(i);
			%>
				<tr>
					<!-- <td><%= data.get("processExceptionLogID") %></td> -->
					<td><%= data.get("logTime") %></td>
					<td><%= data.get("msgProcessorName") %></td>
					<td><%= data.get("exceptionAddress") %></td>
					<td><%= data.get("exceptionType") %></td>
					<td><%= data.get("msgCenterName") %></td>
					<!--<td><%= data.get("msgProcessorIP") %></td>
				<td><%= data.get("messageID") %></td> -->
					<td title=<%= data.get("messageJson") %>><p
							style="display: inline-block; width: 240px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;"><%= data.get("messageJson") %></p></td>
					<td title=<%= data.get("exceptionContent") %>><p
							style="display: inline-block; width: 240px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;"><%= data.get("exceptionContent") %></p></td>

					<!-- 
				<td>
					<a href="javascript:document.getElementById('processExceptionLogID').value='<%= data.get("processExceptionLogID") %>';postModuleAndAction('exceptionLog','delete')">删除</a>
				</td>
				 -->
				</tr>
				<%	} %>
			</table>
		</div>

		<div class="page blue" id="pageDiv">
			<p class="floatl"><%@include file="common/commonJumpPage.jsp"%></p>
			<div class="clear"></div>
		</div>
		<% } %>
	</div>
</div>
<input type="hidden" id="processExceptionLogID"
	name="processExceptionLogID"
	value="<%= JSPDataBean.getFormData("processExceptionLogID")%>" />
<%@include file="common/commonFooter.jsp"%>
