<%@ page contentType="text/html;charset=UTF-8"%>
<jsp:directive.page import="b2cSystem.AppKeys" />
<jsp:useBean id="JSPDataBean" scope='request'
	class="SunlightFrame.web.JSPDataBean" />

<%= JSPDataBean.getFormData("action") %>