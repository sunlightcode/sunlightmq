<%@ page contentType="text/html;charset=UTF-8"%>
<jsp:directive.page import="java.util.Vector" />
<jsp:directive.page import="java.util.Hashtable" />
<jsp:directive.page import="SunlightFrame.util.StringUtil" />

<jsp:useBean id="JSPDataBean" scope='request'
	class="SunlightFrame.web.JSPDataBean" />

<%
	if (AppConfig.getInstance().getParameterConfig().isDebugModel()) {
		System.out.println(request.getAttribute("javax.servlet.forward.servlet_path"));
	} else {
	}
%>