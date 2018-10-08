<%@ page contentType="text/html;charset=UTF-8"%>
<jsp:directive.page import="java.util.Vector" />
<jsp:directive.page import="java.util.Hashtable" />
<%@page import="SunlightFrame.util.DateTimeUtil"%>
<%@page import="SunlightFrame.web.FrameKeys"%>

<jsp:useBean id="JSPDataBean" scope='request'
	class="SunlightFrame.web.JSPDataBean" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title></title>
<link rel="stylesheet" type="text/css" href="/css/top.css" media="all" />
<script type="text/javascript" src="javascript/frame.js"></script>
<%-- <script type="text/javascript" src="javascript/common.js"></script>--%>
<script type="text/javascript" src="javascript/ajax.js"></script>
<script type="text/javascript">
		function ctnMenu(index) {
			var leftFrame = parent.document.getElementById('leftFrame').contentDocument;
			var allMenus = parent.leftFrame.document.getElementById('menu').childNodes;
			for (var i = 1; i < 10; ++i) {
					if (parent.leftFrame.document.getElementById("leftMenu" + i)) {
						parent.leftFrame.document.getElementById("leftMenu" + i).style.display = 'none';
					}	
			}
		
			parent.leftFrame.document.getElementById("leftMenu" + index).style.display = '';
		}
	</script>

</head>
<body>
	<form action="admin" method="post" name="mainForm" id="mainForm">
		<div class="header">
			<div class="logo">SunlightMQ3 服务管理中心</div>
			<div class="vista-toolbar">
				<ul>
				</ul>
				<div class="clear"></div>
			</div>
			<div id="banner" class="top_banner"
				style="width: 350px; text-align: right;" align="right">
				<table cellspacing="0" style="width: 100%;">
					<tr>
						<td>
							<div class="ytb-spacer"></div>
						</td>
						<td><font style="color: #FFF;"><%= DateTimeUtil.getCurrentDate() %>&nbsp;<%= JSPDataBean.getFormData("dayOfWeekStr") %></font></td>
						<td><span class="ytb-sep"></span></td>
						<td style="text-align: center;"><font style="color: #FFF;">当前用户：<%= ((Hashtable)request.getSession().getAttribute(FrameKeys.LOGIN_USER)).get("userName") %></font>
						</td>
						<td><span class="ytb-sep"></span></td>
						<td style="text-align: center;"><a
							href="javascript:postModuleAndActionToTarget('adminLogin','logoff','_parent');"><font
								style="color: #FFF;">退出</font></a>&nbsp;</td>
					</tr>
				</table>
			</div>
		</div>
		<input type="hidden" name="stuffTypeID" id="stuffTypeID" value="" />
		<%= JSPDataBean.getFrameHiddenHtml() %>
	</form>
</body>
</html>
