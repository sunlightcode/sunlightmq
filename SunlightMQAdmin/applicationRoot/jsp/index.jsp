<%@ page contentType="text/html;charset=UTF-8"%>
<jsp:directive.page import="java.util.Vector" />
<jsp:directive.page import="java.util.Hashtable" />

<jsp:useBean id="JSPDataBean" scope='request'
	class="SunlightFrame.web.JSPDataBean" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<title>SunlightAPI3 网关管理平台</title>
</head>
<frameset rows="61,*,20" frameborder="no" border="0" framespacing="0">
<frame src="admin?module=top&action=defaultView" name="topFrame"
	noresize="noresize" id="topFrame" scrolling="No" /> <frameset
	cols="180,6,*" id="frame" frameborder="no" border="0" framespacing="0">
<frame src="admin?module=left&action=defaultView" name="leftFrame"
	id="leftFrame" scrolling="no" /> <frame
	src="admin?module=middle&action=defaultView" name="midFrame"
	id="midFrame" scrolling="no" /> <frame
	src="admin?module=right&action=defaultView" name="rightFrame"
	id="rightFrame" scrolling="no" /> </frameset> <frame
	src="admin?module=bottom&action=defaultView" name="bottomFrame"
	noresize="noresize" id="bottomFrame" scrolling="No" /> </frameset>
</html>
