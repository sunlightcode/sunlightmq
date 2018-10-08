<%@ page contentType="text/html;charset=UTF-8"%>

<jsp:useBean id="JSPDataBean" scope='request'
	class="SunlightFrame.web.JSPDataBean" />

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>SunlightAPI3 网关管理平台</title>
<link href="css/adminLogin/reset.css" type="text/css" rel="stylesheet">
<script src="javascript/jquery.js"></script>
<script src="javascript/adminLogin/common.js"></script>
<script type="text/javascript" src="javascript/frame.js"></script>
<script type="text/javascript" src="javascript/admin.js"></script>
<script type="text/javascript" src="javascript/ajax.js"></script>
<style>
div.window {
	clear: both;
	position: absolute;
	top: 0;
	left: 0;
	visibility: hidden;
	z-index: 100;
}
</style>
</head>



<body>
	<div class="head head_simple">
		SunlightAPI3 网关管理平台
		<div class="container_960">
			<div style="background-color: #DFDFDF;"></div>
		</div>
	</div>
	<div class="body bgNone">
		<div class="container_960">
			<div class="mainBlockTop"></div>

			<div class="mainBlock">
				<form action="admin" method="post" name="mainForm" id="mainForm">
					<!-- <div class="loginTitle">天狮MQ监控管理系统</div> -->
					<div class="loginForm">
						<div class="inputCont">
							<div class="key">账号：</div>
							<div class="value">
								<input tabindex="1" class="text" name="adminUserName"
									id="adminUserName"
									value="<%= JSPDataBean.getFormData("adminUserName") %>"
									type="text"> <span></span>
							</div>
						</div>
						<div class="inputCont">
							<div class="key">密码：</div>
							<div class="value">
								<input name="adminPassword" id="adminPassword" class="text"
									tabindex="2" value="" type="password"
									onkeydown="javascript:if(event.keyCode==13) postModuleAndAction('adminLogin', 'login');" />
							</div>
						</div>

						<div class="btnCont">
							<!-- <input id="password" class="text" name="password" type="hidden">  -->
							<input tabindex="3" class="submit Btn" value="登 录" type="button"
								onclick="postModuleAndAction('adminLogin', 'login');">
							&nbsp;&nbsp; <input tabindex="3" class="submit Btn" value="重 置"
								type="button" onclick=""> &nbsp;&nbsp;
						</div>

					</div>
			</div>
			<div class="mainBlockBot"></div>
		</div>
	</div>
	<div class="foot">
		<!-- <img src="/images/adminLogin/logo_foot.png"> -->
		Copyright&nbsp;&nbsp;sunlightcloud.com
	</div>
	<%@include file="common/commonFooter.jsp"%>