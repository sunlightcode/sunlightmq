<%@ page contentType="text/html;charset=UTF-8"%>
<script lang="javascript">
function showMessage() {
	<% if (!JSPDataBean.getControlData("ERROR_MESSAGE").equals("")) { %>
		alert("<%=JSPDataBean.getControlData("ERROR_MESSAGE") %>");
	<% } %>
	
	<% if (!JSPDataBean.getControlData("INFO_MESSAGE").equals("")) { %>
		alert("<%=JSPDataBean.getControlData("INFO_MESSAGE") %>");
	<% } %>
	
	<% if (!JSPDataBean.getControlData("FOCUS_ITEM").equals("")) { %>
		var element = document.getElementById("<%=JSPDataBean.getControlData("FOCUS_ITEM") %>");
		if (element) {
			try {
				element.focus();
			} catch(e) {
			}
		}
	<% } %>
}

function downloadFile() {
	<% if (!JSPDataBean.getControlData("DOWNLOAD_FILE").equals("")) { %>
		window.location="/download?fileName=" + "<%=JSPDataBean.getControlData("DOWNLOAD_FILE")%>";
	<% } %>
}

showMessage();
downloadFile();

<% if (!JSPDataBean.getControlData("INIT_FUNCTION").equals("")) { %>
	eval("<%=JSPDataBean.getControlData("INIT_FUNCTION")%>");
<% } %>

</script>
