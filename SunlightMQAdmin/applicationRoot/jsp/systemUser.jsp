<%@ page contentType="text/html;charset=UTF-8"%>
<jsp:directive.page import="java.util.Vector" />
<jsp:directive.page import="java.util.Hashtable" />
<%@page import="java.util.Iterator"%>
<jsp:useBean id="JSPDataBean" scope='request'
	class="SunlightFrame.web.JSPDataBean" />

<%@include file="common/commonHeader.jsp"%>
<div id="mainContent">
	<div class="headDiv" id="headDiv">
		<ul class="label">
			<li class="on"><span><a class="text"
					href="javascript:postModuleAndAction('systemUser','defaultView')">系统用户列表</a></span></li>
		</ul>
	</div>
	<div class="main">
		<% if (JSPDataBean.getFormData("action").equals("list")) { %>
		<div class="search" id="searchDiv">
			<div class="right">
				<ul class="buttons">
					<li class="create-icon"><a
						href="javascript:postModuleAndAction('systemUser','addView')">添加系统用户</a></li>
				</ul>
			</div>
			<div class="clear"></div>
		</div>

		<div id="contentArea" align="center">
			<table class="list" border="0" cellpadding="0" cellspacing="1"
				id="tab1" width="97%" align="center">
				<tr>
					<th>用户帐号</th>
					<th>姓名</th>
					<th>角色</th>
					<th>状态</th>
					<th>操作</th>
				</tr>
				<%
						Vector datas = (Vector) JSPDataBean.getJSPData("datas");
						for (int i = 0; i < datas.size(); i++) {
							Hashtable data = (Hashtable) datas.get(i);
							String trClass = "tr_line" + (i % 2);
					%>
				<tr class="<%= trClass %>">
					<td><%= data.get("userID") %></td>
					<td><%= data.get("userName") %></td>
					<td><%= data.get("name") %></td>
					<td>
						<% if (data.get("validFlag").equals("1")) { %> <a
						href="javascript:document.getElementById('systemUserID').value='<%=data.get("systemUserID")%>';postModuleAndAction('systemUser','disable')">
							<% } else { %> <a
							href="javascript:document.getElementById('systemUserID').value='<%=data.get("systemUserID")%>';postModuleAndAction('systemUser','enable')">
								<% } %> <img
								src="/images/<%= data.get("validFlag").equals("1") ? "yes" : "no"%>.gif"
								width="15px" border="none" />
						</a>
					</td>
					<td><a
						href="javascript:document.getElementById('systemUserID').value='<%= data.get("systemUserID") %>';postModuleAndAction('systemUser','editView')">编辑</a>
						<a
						href="javascript:document.getElementById('systemUserID').value='<%= data.get("systemUserID") %>';openInfoWindow('resetSystemUserPasswordWindow')">重置密码</a>
					</td>
				</tr>
				<%	} %>
			</table>
		</div>
		<% } else { %>
		<div class="record">
			<div class="search" id="searchDiv">
				<div class="right">
					<ul class="buttons">
						<li class="submit-icon"><a
							href="javascript:postModuleAndAction('systemUser', 'confirm')">提交</a></li>
						<li class="back-icon"><a
							href="javascript: postModuleAndAction('systemUser', 'defaultView')">返回</a></li>
					</ul>
				</div>
				<div class="clear"></div>
			</div>

			<div id="contentArea" align="center">
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<% if (JSPDataBean.getFormData("systemUserID").equals("")) { %>
					<tr>
						<th>用户帐号<span class="red">* </span>：
						</th>
						<td><input type="text" name="userID" id="userID"
							value="<%= JSPDataBean.getFormData("userID") %>" maxlength="20"
							class="normalInput" />(20个英文字符以内)</td>
					</tr>
					<tr>
						<th>初始密码<span class="red">* </span>：
						</th>
						<td><input type="password" maxlength="20" name="password"
							id="password" value="" class="normalInput" /> (6-20个英文字符)</td>
					</tr>
					<tr>
						<th>确认密码<span class="red">* </span>：
						</th>
						<td><input type="password" maxlength="20" name="password2"
							id="password2" value="" class="normalInput" /></td>
					</tr>
					<% } %>
					<tr>
						<th>姓名<span class="red">* </span>：
						</th>
						<td><input type="text" name="userName" id="userName"
							value="<%= JSPDataBean.getFormData("userName") %>" maxlength="50"
							class="normalInput" /></td>
					</tr>
					<tr>
						<th>系统角色<span class="red">* </span>：
						</th>
						<td><%= JSPDataBean.getFormData("systemRoleSelect") %></td>
					</tr>
				</table>
			</div>
		</div>
		<% } %>

		<input type="hidden" id="systemUserID" name="systemUserID"
			value="<%= JSPDataBean.getFormData("systemUserID") %>" />
	</div>
</div>

<%@include file="common/commonFooter.jsp"%>
