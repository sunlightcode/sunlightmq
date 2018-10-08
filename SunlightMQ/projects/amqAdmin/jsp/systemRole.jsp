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
					href="javascript:postModuleAndAction('systemRole','defaultView')">系统角色设置</a></span></li>
		</ul>
	</div>

	<div class="main">
		<% if (JSPDataBean.getFormData("action").equals("list")) { %>
		<div class="search" id="searchDiv">
			<div class="right">
				<ul class="buttons">
					<li class="create-icon"><a
						href="javascript:postModuleAndAction('systemRole', 'addView')">添加系统角色</a></li>
				</ul>
			</div>
			<div class="clear"></div>
		</div>

		<div id="contentArea" align="center">
			<table class="list" border="0" cellpadding="0" cellspacing="1"
				id="tab1" width="97%" align="center">
				<tr>
					<th>角色名称</th>
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
					<td><%= data.get("name") %></td>
					<td>
						<%	if (data.get("validFlag").equals("1")) { %> <a
						href="javascript:document.getElementById('systemRoleID').value='<%= data.get("systemRoleID") %>';postModuleAndAction('systemRole','disable')">
							<%	} else { %> <a
							href="javascript:document.getElementById('systemRoleID').value='<%= data.get("systemRoleID") %>';postModuleAndAction('systemRole','enable')">
								<%	} %> <img
								src="/images/<%= data.get("validFlag").equals("1") ? "yes" : "no"%>.gif"
								width="15px" border="none" />
						</a>
					</td>
					<td><a
						href="javascript:document.getElementById('systemRoleID').value='<%= data.get("systemRoleID") %>';postModuleAndAction('systemRole','editView')">编辑</a>
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
							href="javascript:postModuleAndAction('systemRole', 'confirm')">提交</a></li>
						<li class="back-icon"><a
							href="javascript: postModuleAndAction('systemRole', 'defaultView')">返回</a></li>
					</ul>
				</div>
				<div class="clear"></div>
			</div>

			<div id="contentArea" align="center">
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<th>角色名称<span class="red">* </span>：
						</th>
						<td><input type="text" name="name" id="name"
							value="<%= JSPDataBean.getFormData("name") %>"
							class="normalInput" maxlength="50" /></td>
					</tr>
					<tr>
						<th>权限（<input type="checkbox" id="selectAll"
							onclick="selectAllCheckBox('selectAll', 'selectChoice', 'selectedValues')" />全选）：
						</th>
						<td>
							<%
							Vector systemModuleDatas = (Vector) JSPDataBean.getJSPData("systemModuleDatas");
							int count = 0;
							String selectedValues = "," + JSPDataBean.getFormData("selectedValues") + ",";
							boolean allSelect = true;
							for (int i = 0; i < systemModuleDatas.size(); i++) {
								Hashtable systemModule = (Hashtable) systemModuleDatas.get(i);
								boolean selected = selectedValues.indexOf("," + systemModule.get("c_systemModuleName") + ",") != -1;
								if (!selected) {
									allSelect = false;
								}
						%> <input type="checkbox" name="selectChoice"
							onchange="setSelectedValues('selectChoice', 'selectedValues')"
							value="<%= systemModule.get("c_systemModuleName") %>"
							<%= selected ? "checked=\"checked\"" : "" %> /> <%= systemModule.get("systemModuleViewName") %>
							<%	
								if (systemModule.get("separateFlag").equals("1")) { 
									count = 0;
							%>
							<p />&nbsp;
							<p /> <%	} else { count++; %> &nbsp;<%= (count % 5 == 0 ) ? "<br/>" : "" %>
							<%	}} %> <% if (allSelect) { %> <script>
							document.getElementById("selectAll").checked = "checked";
						</script> <% } %>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
	<% } %>

	<input type="hidden" id="systemRoleID" name="systemRoleID"
		value="<%= JSPDataBean.getFormData("systemRoleID") %>" /> <input
		type="hidden" id="selectedValues" name="selectedValues"
		value="<%= JSPDataBean.getFormData("selectedValues") %>" />

</div>

</div>
<%@include file="common/commonFooter.jsp"%>
