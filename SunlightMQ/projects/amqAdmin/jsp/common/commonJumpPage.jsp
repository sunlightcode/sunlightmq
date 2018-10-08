<%@ page contentType="text/html;charset=UTF-8"%>
<%@page import="SunlightFrame.web.JSPDataBean"%>

<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td><span style="float: left"> 共 <%= JSPDataBean.getFormData("count") %>
				个 <a
				href="javascript:document.getElementById('pageIndexInput').value='1';jumpToPage()">首页</a>
				<a
				href="<%= Integer.parseInt(JSPDataBean.getFormData("pageIndex")) > 1 ? "javascript:document.getElementById('pageIndexInput').value='" + (Integer.parseInt(JSPDataBean.getFormData("pageIndex")) - 1) + "';jumpToPage()" : "#" %>">上一页</a>
				<%= JSPDataBean.getFormData("pageIndex") %>/<%= JSPDataBean.getFormData("pageCount") %>页
				<a
				href="<%= Integer.parseInt(JSPDataBean.getFormData("pageIndex")) < Integer.parseInt(JSPDataBean.getFormData("pageCount")) ? "javascript:document.getElementById('pageIndexInput').value='" + (Integer.parseInt(JSPDataBean.getFormData("pageIndex")) + 1) + "';jumpToPage()" : "#" %>">下一页</a>
				<a
				href="javascript:document.getElementById('pageIndexInput').value='<%= JSPDataBean.getFormData("pageCount") %>';jumpToPage()">末页</a>
				转到：第 <input type="text" id="pageIndexInput" name="pageIndexInput"
				size="3" /> 页 <input type="button"
				onclick="javascript:jumpToPage()" value="Go" />
		</span>
			<p style="clear: both"></p></td>
	</tr>
</table>

<input type="hidden" id="pageCount" name="pageCount"
	value="<%= JSPDataBean.getFormData("pageCount") %>" />
