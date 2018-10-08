<%@ page contentType="text/html;charset=UTF-8"%>
<jsp:directive.page import="java.util.Vector" />
<jsp:directive.page import="java.util.Hashtable" />

<jsp:useBean id="JSPDataBean" scope='request'
	class="SunlightFrame.web.JSPDataBean" />

<div id="popwindow">
	<h2>
		<span>重置私钥</span>
	</h2>

	<div style="width: 380px; padding: 10px">
		<div style="margin-top: 10px;">
			<table cellpadding="0" cellspacing="0" style="width: 380px;">
				<tr>
					<td width="32%" align="right"
						style="height: 30px; font-weight: bold;">旧DES密钥：</td>
					<td width="68%"><%= JSPDataBean.getFormData("desKey") %></td>
				</tr>
				<tr>
					<td align="right" style="height: 30px; font-weight: bold;">新DES密钥：</td>
					<td id="newPrivateKeyID"><%= JSPDataBean.getFormData("newDesKey") %></td>
				</tr>
			</table>
		</div>
	</div>

	<input type="hidden" name="newDesKey" id="newDesKey"
		value="<%= JSPDataBean.getFormData("newDesKey") %>" />
	<div style="text-align: center;" class="buttonsDIV">
		<a class="btn_y" onclick="javascript:doAction('resetDesKey');"><span>确认</span></a>&nbsp;
		<a class="btn_y" onclick="javascript:closeInfoWindow('infoWindow');"><span>取消</span></a>
	</div>
</div>
