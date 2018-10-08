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
					href="javascript:postModuleAndAction('msgProcessor', 'defaultView');">首页&nbsp;->&nbsp;应用接口管理</a></span></li>
		</ul>
	</div>

	<div class="main">
		<% if (JSPDataBean.getFormData("action").equals("list")){ %>
		<div class="search" id="searchDiv">
			<div class="right">
				<ul class="buttons">
					<li class="create-icon"><a
						href="javascript:postModuleAndAction('msgProcessor','addView')">添加应用接口</a></li>
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
							onkeydown="javascript:if(event.keyCode==13)postModuleAndAction('msgProcessor', 'search')" />
							队列名称：<input type="text" class="shortInput" name="q_name"
							id="q_name" value="<%= JSPDataBean.getFormData("q_name") %>"
							onkeydown="javascript:if(event.keyCode==13)postModuleAndAction('msgProcessor', 'search')" />
							应用接口：<input type="text" class="shortInput" name="q_callUrl"
							id="q_callUrl"
							value="<%= JSPDataBean.getFormData("q_callUrl") %>"
							onkeydown="javascript:if(event.keyCode==13)postModuleAndAction('msgProcessor', 'search')" />
							<a class="btn_y"
							onclick="javascript:postModuleAndAction('msgProcessor', 'search');"><span>查询</span></a>
						</td>
					</tr>
				</table>
			</fieldset>
			<table class="list" border="0" cellpadding="0" cellspacing="1"
				id="tab1" width="97%" align="center">
				<tr>
					<th>消息队列</th>
					<!-- 
				<th>ID</th> 
				-->
					<th>队列名称</th>
					<th>消息中心</th>
					<th width="100px">应用接口</th>
					<!-- 
					<th>回调方式</th>
					<th width="100px">回调接口<br>回调队列</th>
					-->
					<th>最大并发数量</th>
					<!-- 
					<th>自动运行</th>
					<th>指向B2C</th>			
					<th>是否为混合队列</th>			
					<th>是否跨中心</th>
					<th>跨中心URL</th>
					<th>跨中心混合队列名称</th>	
					<th>当前状态</th>
					<th>是否有效</th>
					-->
					<th>运行状态</th>
					<th>队列操作</th>
				</tr>
				<%
				Vector datas = (Vector) JSPDataBean.getJSPData("datas");
				for (int i = 0; i < datas.size(); i++) {
					Hashtable data = (Hashtable) datas.get(i);
			%>
				<tr>
					<!-- <td><%= data.get("msgProcessorID") %></td> -->
					<td><%= data.get("queueName") %></td>
					<td><%= data.get("name") %></td>
					<td><%= data.get("msgCenterName") %></td>
					<td><%= data.get("callUrl") %></td>
					<!-- 
					<td><%= DataCache.getInstance().getTableDataColumnValue("c_callbackMethodType", data.get("callbackMethodTypeID").toString(), "c_callbackMethodTypeName")  %></td>
					<td><%= data.get("callbackMethodTypeID").equals(AppKeys.CALLBACKMETHODTYPE_API) ? data.get("callbackUrl") : data.get("callbackQueueName") %></td>				
					-->
					<td><%= data.get("startThreadNumber") %></td>
					<!-- 
					<td><%= data.get("autoRunning").equals("1") ? "是" : "否" %></td>
					<td><%= data.get("askb2cflag").equals("1") ? "是" : "否" %></td>				
					<td><%= data.get("remoteSendQueue").equals("1") ? "是" : "否" %></td>			 
					<td><%= data.get("toOtherCenter").equals("1") ? "是" : "否" %></td>
					<td><%= data.get("otherCenterUrl")%></td>
					<td><%= data.get("otherCenterQueue")%></td>	
					<td><%= data.get("status").equals("1") ? "运行中" : "已停止" %></td>
					<td>
					<% if (data.get("validFlag").equals("1")) { %>
					<a href="javascript:document.getElementById('msgProcessorID').value='<%=data.get("msgProcessorID")%>';postModuleAndAction('msgProcessor','disable')">
					<% } else { %>
					<a href="javascript:document.getElementById('msgProcessorID').value='<%=data.get("msgProcessorID")%>';postModuleAndAction('msgProcessor','enable')">
					<% } %>
					<img src="/images/<%= data.get("validFlag").equals("1") ? "yes" : "no"%>.gif" width="15px" border="none"/>
					</a>
					</td>
					-->
					<td><%= data.get("status").equals("1") ? "正常" : "停止" %></td>
					<td><a
						href="javascript:document.getElementById('msgProcessorID').value='<%= data.get("msgProcessorID") %>';postModuleAndAction('msgProcessor','editView')">编辑</a>
						<a
						href="javascript:document.getElementById('msgProcessorID').value='<%= data.get("msgProcessorID") %>';openInfoWindow('operationMsgProcessorWindow')">操作</a>
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
							href="javascript:postModuleAndAction('msgProcessor', 'confirm');">提交</a></li>
						<li class="back-icon"><a
							href="javascript: postModuleAndAction('msgProcessor', 'defaultView')">返回</a></li>
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
						<th><span class="red">* </span>消息中心：</th>
						<td><%= JSPDataBean.getFormData("msgCenterSelect") %></td>
					</tr>
					<tr>
						<th><span class="red">* </span>消息队列：</th>
						<td><%= JSPDataBean.getFormData("queueSelect") %></td>
					</tr>
					<tr>
						<th><span class="red">* </span>应用接口：</th>
						<td><input type="text" name="callUrl" id="callUrl"
							value="<%=JSPDataBean.getFormData("callUrl")%>"
							class="normalInput" maxlength="200" /></td>
					</tr>
					<tr>
						<th><span class="red">* </span>接口请求方式：</th>
						<td><%= JSPDataBean.getFormData("callRequestTypeSelect") %></td>
					</tr>
					<!-- 
			<tr>
				<th><span class="red">* </span>回调方式：</th>
				<td><%= JSPDataBean.getFormData("callbackMethodTypeSelect") %></td>
			</tr>
			<tr class="callbackUrl_tr" style="display: <%= JSPDataBean.getFormData("callbackMethodTypeID").equals(AppKeys.CALLBACKMETHODTYPE_API) ? "" : "none" %>">
				<th><span class="red">* </span>回调接口：</th>
				<td>
					<input type="text" name="callbackUrl" id="callbackUrl" value="<%=JSPDataBean.getFormData("callbackUrl")%>" class="normalInput" maxlength="200"/>
				</td>
			</tr>
			<tr class="callbackUrl_tr" style="display: <%= JSPDataBean.getFormData("callbackMethodTypeID").equals(AppKeys.CALLBACKMETHODTYPE_API) ? "" : "none" %>">
				<th><span class="red">* </span>回调接口请求方式：</th>
				<td><%= JSPDataBean.getFormData("callbackRequestTypeSelect") %></td>
			</tr>
			
			<tr class="queue_tr" style="display: <%= JSPDataBean.getFormData("callbackMethodTypeID").equals(AppKeys.CALLBACKMETHODTYPE_QUEUE) ? "" : "none" %>">
				<th><span class="red">* </span>回调队列：</th>
				<td><%= JSPDataBean.getFormData("callbackQueueSelect") %></td>
			</tr>
			-->
					<tr>
						<th><span class="red">* </span>是否为B2C接口：</th>
						<td><input type="radio" name="askb2cflag" id="askb2cflag"
							value="0"
							<%= !JSPDataBean.getFormData("askb2cflag").equals("1") ? "checked=\"checked\"" : "" %> />否
							<input type="radio" name="askb2cflag" id="askb2cflag" value="1"
							<%= JSPDataBean.getFormData("askb2cflag").equals("1") ? "checked=\"checked\"" : "" %> />是
						</td>
					</tr>
					<tr>
						<th><span class="red">* </span>是否跨中心：</th>
						<td><input type="radio" onclick="radio();" class="showXS"
							name="toOtherCenter" id="toOtherCenter1" value="0"
							<%= !JSPDataBean.getFormData("toOtherCenter").equals("1") ? "checked=\"checked\"" : "" %> />否
							<input type="radio" onclick="radio();" class="showXS"
							name="toOtherCenter" id="toOtherCenter2" value="1"
							<%= JSPDataBean.getFormData("toOtherCenter").equals("1") ? "checked=\"checked\"" : "" %> />是
						</td>
					</tr>

					<tr class="url_tr"
						style="display: <%= JSPDataBean.getFormData("toOtherCenter").equals("1") ? "" : "none" %>">
						<th width="30%"><span class="red">* </span>跨中心URL：</th>
						<td><input type="text" name="otherCenterUrl"
							id="otherCenterUrl"
							value="<%=JSPDataBean.getFormData("otherCenterUrl")%>"
							class="normalInput" maxlength="200" /></td>
					</tr>

					<tr class="queueName_tr"
						style="display: <%= JSPDataBean.getFormData("toOtherCenter").equals("1") ? "" : "none" %>">
						<td><input type="hidden" name="otherCenterQueue"
							id="otherCenterQueue" value="" /></td>
						<!--
				<th width="30%"><span class="red">* </span>跨中心混合队列名称：</th>
				<td>
					<input type="text" name="otherCenterQueue" id="	" value="<%=JSPDataBean.getFormData("otherCenterQueue")%>" class="normalInput" maxlength="200"/>
				</td>
				-->
					</tr>
					<tr>
					<!--
						<td><input type="hidden" name="startThreadNumber"
							id="startThreadNumber" value="10" /></td>
					--> 			
				<th><span class="red">* </span>最大并发数量：</th>
				<td>
					<input type="text" name="startThreadNumber" id="startThreadNumber" value="<%= JSPDataBean.getFormData("startThreadNumber").equals("") ? "50" : JSPDataBean.getFormData("startThreadNumber") %>" class="normalInput" maxlength="11"/>
				</td>
					</tr>
					<tr>
						<td><input type="hidden" name="autoRunning" id="autoRunning"
							value="1" /></td>
						<!-- 
				<th><span class="red">* </span>是否自动运行：</th>
				<td>
					<input type="radio" name="autoRunning" id="autoRunning" value="0" <%= !JSPDataBean.getFormData("autoRunning").equals("1") ? "checked=\"checked\"" : "" %> />否
					<input type="radio" name="autoRunning" id="autoRunning" value="1" <%= JSPDataBean.getFormData("autoRunning").equals("1") ? "checked=\"checked\"" : "" %> />是
				</td>
				-->
					</tr>
					<tr>
						<td><input type="hidden" name="remoteSendQueue"
							id="remoteSendQueue" value="0" /></td>
						<!-- 
				<th><span class="red">* </span>是否为混合队列：</th>
				<td>
					<input type="radio" name="remoteSendQueue" id="remoteSendQueue" value="0" <%= !JSPDataBean.getFormData("remoteSendQueue").equals("1") ? "checked=\"checked\"" : "" %> />否
					<input type="radio" name="remoteSendQueue" id="remoteSendQueue" value="1" <%= JSPDataBean.getFormData("remoteSendQueue").equals("1") ? "checked=\"checked\"" : "" %> />是
				</td>
				-->
					</tr>
				</table>
			</div>
		</div>
		<% } %>
	</div>
</div>

<script>
	function selectCallbackMethod() {
		var callbackMethodTypeID = getSelectValue('callbackMethodTypeID');
		if (callbackMethodTypeID == '<%= AppKeys.CALLBACKMETHODTYPE_API %>'){
			$('.callbackUrl_tr').show();
			$('.queue_tr').hide();
		}else if(callbackMethodTypeID == '<%= AppKeys.CALLBACKMETHODTYPE_QUEUE %>'){
			$('.callbackUrl_tr').hide();
			$('.queue_tr').show();
		}else{
			$('.callbackUrl_tr').hide();
			$('.queue_tr').hide();
		}
	}
</script>

<script>
	function radio() {
		var toOtherCenter = $('input[name="toOtherCenter"]:checked').val()
		if (toOtherCenter == '1'){
			$('.url_tr').show();
			$('.queueName_tr').show();
		}else if(toOtherCenter == '0'){
			$('.url_tr').hide();
			$('.queueName_tr').hide();
		}
	}
</script>
<input type="hidden" id="msgProcessorID" name="msgProcessorID"
	value="<%= JSPDataBean.getFormData("msgProcessorID")%>" />
<%@include file="common/commonFooter.jsp"%>
