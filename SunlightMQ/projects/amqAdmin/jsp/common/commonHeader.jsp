<%@ page contentType="text/html;charset=UTF-8"%>
<jsp:directive.page import="SunlightFrame.web.JSPDataBean" />
<%@page import="java.util.Hashtable"%>
<%@page import="SunlightFrame.web.FrameKeys"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>SunlightMQ3 服务管理中心</title>
<% if (JSPDataBean.getFormData("module").equals("left")) { %>
<link href="/css/left.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="/css/popup.css" type="text/css" />
<%-- <script type="text/javascript" src="/javascript/popup.js"></script>--%>
<% } else { %>
<link href="/css/admin.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="/css/popup.css" type="text/css" />
<% } %>

<script type="text/javascript" src="/javascript/jquery.js"></script>
<script type="text/javascript" src="/javascript/frame.js"></script>
<script type="text/javascript" src="/javascript/admin.js"></script>
<script type="text/javascript" src="/javascript/ajax.js"></script>
<script type="text/javascript" src="/javascript/WdatePicker.js"></script>
<% if (JSPDataBean.getFormData("module").equals("stuff") && JSPDataBean.getFormData("action").equals("addRegisterFormView")) { %>
<script type="text/javascript" src="/javascript/formCheck.js"></script>
<% } %>
</head>

<%
	Hashtable loginSystemUser = ((Hashtable)request.getSession().getAttribute(FrameKeys.LOGIN_USER));
	String priority = "," + loginSystemUser.get("priority").toString() + ",";
	String action = JSPDataBean.getFormData("action");
%>

<body>

	<form action="/admin" method="post" name="mainForm" id="mainForm"
		target="_self" onSubmit="return false;">