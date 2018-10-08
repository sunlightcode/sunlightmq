<%@ page contentType="text/html;charset=UTF-8"%>
<jsp:directive.page import="b2cSystem.AppKeys" />
<jsp:useBean id="JSPDataBean" scope='request'
	class="SunlightFrame.web.JSPDataBean" />

<span style="display: none;" id="hiddenSpanInWindowInside"> <%= JSPDataBean.getFormData(AppKeys.AJAX_RESULT) %>
</span>
