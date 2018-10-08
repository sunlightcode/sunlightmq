<%@ page contentType="text/html;charset=UTF-8"%>
<jsp:directive.page import="java.util.Vector" />
<jsp:directive.page import="java.util.Hashtable" />

<jsp:useBean id="JSPDataBean" scope='request'
	class="SunlightFrame.web.JSPDataBean" />

<div id="popwindow">
	<h2>
		<span>重置AppKey</span>
	</h2>

	<div style="width: 380px; padding: 10px">
		<div style="margin-top: 10px;">
			<table cellpadding="0" cellspacing="0" style="width: 380px;">
				<tr>
					<td width="32%" align="right"
						style="height: 30px; font-weight: bold;">原AppKey：</td>
					<td width="68%"><%= JSPDataBean.getFormData("appKey") %></td>
				</tr>
				<tr>
					<td align="right" style="height: 30px; font-weight: bold;"><span
						class="red">* </span>新AppKey：</td>
					<td><input type="text" name="newAppKey" id="newAppKey"
						value="<%=JSPDataBean.getFormData("newAppKey")%>"
						class="normalInput" maxlength="16" /></td>
				</tr>
			</table>
		</div>
	</div>

	<div style="text-align: center;" class="buttonsDIV">
		<a class="btn_y" onclick="javascript:doAction('resetAppKey');"><span>确认</span></a>&nbsp;
		<a class="btn_y" onclick="javascript:closeInfoWindow('infoWindow');"><span>取消</span></a>
	</div>
</div>
