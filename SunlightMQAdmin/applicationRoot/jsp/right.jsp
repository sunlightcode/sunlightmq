<%@ page contentType="text/html;charset=UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>SunlightAPI3 网关管理平台</title>

<link href="/css/admin.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="/css/popup.css" type="text/css" />


<script type="text/javascript" src="/javascript/jquery.js"></script>
<script type="text/javascript" src="/javascript/frame.js"></script>
<script type="text/javascript" src="/javascript/admin.js"></script>
<script type="text/javascript" src="/javascript/ajax.js"></script>
<script type="text/javascript" src="/javascript/WdatePicker.js"></script>

</head>



<body>

	<form action="/admin" method="post" name="mainForm" id="mainForm"
		target="_self" onSubmit="return false;">
		<div id="mainContent">

			<div class="headDiv" id="headDiv">
				<ul class="label">
					<!-- <li class="on"><span><a class="text" href="javascript:void(0)">天狮MQ监控管理系统</a></span></li> -->
				</ul>
			</div>

			<div class="main">

				<div id="contentArea">
					<div style="margin-top: 10px" align="center">
						<div style="margin-top: 200px">
							<div style="font-size: 16px; font-weight: bold;">欢迎登陆 SunlightAPI3 网关管理平台</div>
							<!-- <div><img src="/images/adminLogin/logo_login.gif" /></div> -->
						</div>
					</div>
				</div>
			</div>

			<input type="hidden" id="systemUserID" name="systemUserID" value="" />
		</div>




		<script>
	if (document.getElementById('mainContent') && document.getElementById('contentArea')) {
		var mainContentHeight = window.document.documentElement.clientHeight==0 ? parseInt(window.document.body.clientHeight): parseInt(window.document.documentElement.clientHeight);
		var headDivHeight = 0;
		var searchDivHeight = 0;
		var pageDivHeight = 0;
		if (document.getElementById('headDiv')) {
			headDivHeight = parseInt(document.getElementById('headDiv').offsetHeight);
		}
		
		if (document.getElementById('searchDiv')) {
			searchDivHeight = parseInt(document.getElementById('searchDiv').offsetHeight);
		}
		
		if (document.getElementById('pageDiv')) {
			pageDivHeight = parseInt(document.getElementById('pageDiv').offsetHeight);
		}
		
		document.getElementById('contentArea').style.height=(mainContentHeight  - headDivHeight - searchDivHeight - pageDivHeight) * 98 / 100 + "px";
	}
</script>


		<input type="hidden" name="moduleBeforeAjax" id="moduleBeforeAjax"
			value="password" /> <input type="hidden" id="pageModule"
			name="pageModule" value="password" /> <input type="hidden"
			id="pageIndex" name="pageIndex" value="" /> <input type="hidden"
			name="ajaxAction" id="ajaxAction" value="" /> <input type="hidden"
			name="checkUserID" id="checkUserID" value="" />


		<script lang="javascript">
function showMessage() {
	
	
	
	
	
}

function downloadFile() {
	
}

showMessage();
downloadFile();



</script>


		<span style="display: none;" id="hiddenSpan"></span>
		<div id="mask"></div>
		<div id="infoWindow" class="window">
			<table class="window">
				<tr>
					<td class="left_top"></td>
					<td class="border"></td>
					<td class="right_top"></td>
				</tr>
				<tr>
					<td class="border"></td>
					<td class="content" id="windowInsideDIV"></td>
					<td class="border"></td>
				</tr>
				<tr>
					<td class="left_bottom"></td>
					<td class="border"></td>
					<td class="right_bottom"></td>
				</tr>
			</table>
		</div>
	</form>
	<iframe name="hiddenIframe" style="display: none"></iframe>

	<div id="mytips"></div>
</body>
</html>


