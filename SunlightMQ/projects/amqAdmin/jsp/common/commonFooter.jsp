<%@ page contentType="text/html;charset=UTF-8"%>
<%@page import="SunlightFrame.web.JSPDataBean"%>

<% if (JSPDataBean.getFormData("module").equals("left")) { %>
<script>
	document.getElementById('menu').style.height=(window.document.body.clientHeight * 98 / 100) + "px";
</script>
<% } else { %>
<script>
	if (document.getElementById('mainContent') && document.getElementById('contentArea')) {
		var mainContentHeight = window.document.documentElement.clientHeight==0 ? parseInt(window.document.body.clientHeight): parseInt(window.document.documentElement.clientHeight);
		var headDivHeight = 0;
		var searchDivHeight = 0;
		var pageDivHeight = 0;
		if (document.getElementById('headDiv')) {
			headDivHeight = parseInt(document.getElementById('headDiv').offsetHeight);
		}
		
		if (document.getElementById('searchDiv')) {
			searchDivHeight = parseInt(document.getElementById('searchDiv').offsetHeight);
		}
		
		if (document.getElementById('pageDiv')) {
			pageDivHeight = parseInt(document.getElementById('pageDiv').offsetHeight);
		}
		
		document.getElementById('contentArea').style.height=(mainContentHeight  - headDivHeight - searchDivHeight - pageDivHeight) * 98 / 100 + "px";
	}
</script>
<% } %>

<%
	String frameHiddenHtml = JSPDataBean.getFrameHiddenHtml();
	if (frameHiddenHtml.equals("")) {
%>
<input type="hidden" name="module" id="module"
	value="<%= JSPDataBean.getFormData("module") %>" />
<input type="hidden" id="action" name="action"
	value="<%= JSPDataBean.getFormData("action") %>" />
<%	} else { %>
<%= frameHiddenHtml %>
<%	} %>

<input type="hidden" name="moduleBeforeAjax" id="moduleBeforeAjax"
	value="<%= JSPDataBean.getFormData("module") %>" />
<input type="hidden" id="pageModule" name="pageModule"
	value="<%= JSPDataBean.getFormData("module") %>" />
<input type="hidden" id="pageIndex" name="pageIndex"
	value="<%= JSPDataBean.getFormData("pageIndex") %>" />
<input type="hidden" name="ajaxAction" id="ajaxAction" value="" />
<input type="hidden" name="checkUserID" id="checkUserID" value="" />

<%@include file="commonPageInit.jsp"%>

<span style="display: none;" id="hiddenSpan"></span>
<div id="mask"></div>
<div id="infoWindow" class="window">
	<table class="window">
		<tr>
			<td class="left_top"></td>
			<td class="border"></td>
			<td class="right_top"></td>
		</tr>
		<tr>
			<td class="border"></td>
			<td class="content" id="windowInsideDIV"></td>
			<td class="border"></td>
		</tr>
		<tr>
			<td class="left_bottom"></td>
			<td class="border"></td>
			<td class="right_bottom"></td>
		</tr>
	</table>
</div>


<% if (JSPDataBean.getFormData("module").equals("accessLog")) { %>
<div id="infoWindow2" class="window">
	<table class="window">
		<tr>
			<td class="left_top"></td>
			<td class="border"></td>
			<td class="right_top"></td>
		</tr>
		<tr>
			<td class="border"></td>
			<td class="content" id="windowInsideDIV2">
				<div id="popwindow2">
					<h2>
						<span>等待中</span><a style="" class="close-button"
							href="javascript:closeInfoWindow2('infoWindow2');">关闭</a>
					</h2>

					<div style="width: 350px; padding: 10px">
						<div style="margin-top: 10px; text-align: center;" align="center">
							<div>
								<img src="/images/waitSendEmail.gif">
							</div>
							<div>数据导出中,请耐心等待...</div>
						</div>
					</div>
				</div>
			</td>
			<td class="border"></td>
		</tr>
		<tr>
			<td class="left_bottom"></td>
			<td class="border"></td>
			<td class="right_bottom"></td>
		</tr>
	</table>
</div>
<% } %>

</form>
<iframe name="hiddenIframe" style="display: none"></iframe>

<div id="mytips"></div>
</body>
</html>

