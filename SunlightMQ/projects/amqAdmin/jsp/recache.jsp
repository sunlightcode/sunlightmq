<%@ page contentType="text/html;charset=UTF-8"%>
<jsp:directive.page import="SunlightFrame.web.FrameKeys" />

<jsp:useBean id="JSPDataBean" scope='request'
	class="SunlightFrame.web.JSPDataBean" />

<%= JSPDataBean.getControlData(FrameKeys.INFO_MESSAGE) %>
