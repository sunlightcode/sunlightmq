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
					href="javascript:postModuleAndAction('password','defaultView')">密码修改</a></span></li>
		</ul>
	</div>

	<div class="main">
		<div class="search" id="searchDiv">
			<div class="right">
				<ul class="buttons">
					<li class="submit-icon"><a
						href="javascript:postModuleAndAction('password', 'confirm')">提交</a></li>
				</ul>
			</div>
			<div class="clear"></div>
		</div>

		<div class="record">
			<div id="contentArea" align="center">
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<th>原密码<span class="red">*</span> ：
						</th>
						<td><input type="password" maxlength="20" name="password1"
							id="password1" value="" class="normalInput" /></td>
					</tr>
					<tr>
						<th>初始密码<span class="red">*</span> ：
						</th>
						<td><input type="password" maxlength="20" name="password2"
							id="password2" value="" class="normalInput" /> (6-20个英文字符)</td>
					</tr>
					<tr>
						<th>确认密码<span class="red">*</span> ：
						</th>
						<td><input type="password" maxlength="20" name="password3"
							id="password3" value="" class="normalInput" /></td>
					</tr>
				</table>
			</div>
		</div>
	</div>

	<input type="hidden" id="systemUserID" name="systemUserID"
		value="<%= JSPDataBean.getFormData("systemUserID") %>" />
</div>

<%@include file="common/commonFooter.jsp"%>
