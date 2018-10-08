<%@ page contentType="text/html;charset=UTF-8"%>
<jsp:directive.page import="java.util.Vector" />
<jsp:directive.page import="java.util.Hashtable" />

<jsp:useBean id="JSPDataBean" scope='request'
	class="SunlightFrame.web.JSPDataBean" />

<div id="popwindow">
	<h2>
		<span>队列操作</span>
	</h2>

	<div style="width: 380px; padding: 10px">
		<div style="margin-top: 10px;">
			<table cellpadding="0" cellspacing="0" style="width: 380px;">
				<tr>
					<td width="32%" align="right"
						style="height: 30px; font-weight: bold;">操作：</td>
					<td width="68%"><select name="operType" id="operType">
							<option value="start">启动</option>
							<option value="stop">停止</option>
							<option value="restart">重启</option>
					</select></td>
				</tr>
			</table>
		</div>
	</div>

	<div style="text-align: center;" class="buttonsDIV">
		<a class="btn_y"
			onclick="javascript:doAction('operationMsgProcessor');"><span>确认</span></a>&nbsp;
		<a class="btn_y" onclick="javascript:closeInfoWindow('infoWindow');"><span>取消</span></a>
	</div>
</div>
