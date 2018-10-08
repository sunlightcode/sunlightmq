<%@page import="sunlightMQ.AppKeys"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<jsp:useBean id="JSPDataBean" scope='request'
	class="SunlightFrame.web.JSPDataBean" />

<%= JSPDataBean.getFormData(AppKeys.AJAX_RESULT) %>