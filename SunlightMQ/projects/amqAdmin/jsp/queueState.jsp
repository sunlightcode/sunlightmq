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
					href="javascript:postModuleAndAction('queueState', 'defaultView');">首页&nbsp;->&nbsp;队列状态监控</a></span></li>
		</ul>
	</div>

	<div class="main">
		<% if (JSPDataBean.getFormData("action").equals("list")){ %>
		<div class="search" id="searchDiv">
			<div class="right">
				<ul class="buttons">
					<li class="refresh-icon"><a
						href="javascript:postModuleAndAction('queueState','refreshView')">刷新队列状态</a></li>
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
							消息队列：<input type="text" class="shortInput" name="q_queueName"
							id="q_queueName"
							value="<%= JSPDataBean.getFormData("q_queueName") %>"
							onkeydown="javascript:if(event.keyCode==13)postModuleAndAction('queueState', 'search')" />
							剩余消息数量：<input type="text" class="shortInput" name="q_queueSize"
							id="q_queueSize"
							value="<%= JSPDataBean.getFormData("q_queueSize") %>"
							onkeydown="javascript:if(event.keyCode==13)postModuleAndAction('queueState', 'search')" />
							<a class="btn_y"
							onclick="javascript:postModuleAndAction('queueState', 'search');"><span>查询</span></a>
						</td>
					</tr>
				</table>
			</fieldset>
			<table class="list" border="0" cellpadding="0" cellspacing="1"
				id="tab1" width="97%" align="center">
				<tr>
					<th>消息队列</th>
					<!-- <th>内存总量</th>
				<th>已使用内存</th>
				<th>内存占用百分比</th>
				<th>平均入队时间</th>
				<th>生产者数量</th> -->
					<th>消费者数量</th>
					<th>入队消息数量</th>
					<th>出队消息数量</th>
					<th>剩余消息数量</th>
				</tr>
				<%
				Vector datas = (Vector) JSPDataBean.getJSPData("datas");
				for (int i = 0; i < datas.size(); i++) {
					Hashtable data = (Hashtable) datas.get(i);
			%>
				<tr>
					<td><%= data.get("queueName") %></td>
					<!-- <td><%= data.get("memoryLimit") %></td>
				<td><%= data.get("memoryUsagePortion") %></td>
				<td><%= data.get("memoryPercentUsage") %></td>
				<td><%= data.get("averageEnqueueTime") %></td>
				<td><%= data.get("producerCount") %></td> -->
					<td><%= data.get("consumerCount") %></td>
					<td><%= data.get("enqueueCount") %></td>
					<td><%= data.get("dequeueCount") %></td>
					<td><%= data.get("queueSize") %></td>
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
<%@include file="common/commonFooter.jsp"%>