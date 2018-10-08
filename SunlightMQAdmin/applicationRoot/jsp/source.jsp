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
					href="javascript:postModuleAndAction('source','defaultView')">来源管理</a></span></li>
		</ul>
	</div>

	<div class="main">
		<% if (JSPDataBean.getFormData("action").equals("list")) { %>
		<div class="search" id="searchDiv">
			<div class="right">
				<ul class="buttons">
				</ul>
			</div>
			<div class="clear"></div>
		</div>

		<div id="contentArea">
			<table class="list" cellpadding="0" cellspacing="1" width="100%"
				id="tab1">
				<tr>
					<th>来源名称</th>
					<th>状态</th>
				</tr>
				<%
			Vector datas = (Vector) JSPDataBean.getJSPData("datas");
			for (int i = 0; i < datas.size(); i++) {
				Hashtable data = (Hashtable) datas.get(i);
				String trClass = "tr_line" + (i % 2);
		%>
				<tr class="<%= trClass %>">
					<td><%= data.get("Source") %></td>
					<td>
						<%	if (data.get("validFlag").equals("1")) { %> <a
						href="javascript:document.getElementById('SourceID').value='<%= data.get("SourceID") %>';postModuleAndAction('source','disable')">
							<%	} else { %> <a
							href="javascript:document.getElementById('SourceID').value='<%= data.get("SourceID") %>';postModuleAndAction('source','enable')">
								<%	} %> <img
								src="/images/<%= data.get("validFlag").equals("1") ? "yes" : "no"%>.gif"
								width="15px" border="none" />
						</a>
					</td>
				</tr>
				<%	} %>
			</table>
		</div>
		<% } %>

		<input type="hidden" id="SourceID" name="SourceID"
			value="<%= JSPDataBean.getFormData("SourceID") %>" />

	</div>

</div>
<%@include file="common/commonFooter.jsp"%>
