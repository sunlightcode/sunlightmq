<%@ page contentType="text/html;charset=UTF-8"%>

<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td><span> 共 <%= JSPDataBean.getFormData("windowCount") %>
				个 <a
				href="javascript:document.getElementById('windowPageIndexInput').value='1';widnowJumpToPage()">首页</a>
				<a
				href="<%= Integer.parseInt(JSPDataBean.getFormData("windowPageIndex")) > 1 ? "javascript:document.getElementById('windowPageIndexInput').value='" + (Integer.parseInt(JSPDataBean.getFormData("windowPageIndex")) - 1) + "';widnowJumpToPage()" : "#" %>">上一页</a>
				<%= JSPDataBean.getFormData("windowPageIndex") %>/<%= JSPDataBean.getFormData("windowPageCount").equals("0") ? "1" : JSPDataBean.getFormData("windowPageCount") %>页
				<a
				href="<%= Integer.parseInt(JSPDataBean.getFormData("windowPageIndex")) < Integer.parseInt(JSPDataBean.getFormData("windowPageCount")) ? "javascript:document.getElementById('windowPageIndexInput').value='" + (Integer.parseInt(JSPDataBean.getFormData("windowPageIndex")) + 1) + "';widnowJumpToPage()" : "#" %>">下一页</a>
				<a
				href="javascript:document.getElementById('windowPageIndexInput').value='<%= JSPDataBean.getFormData("windowPageCount") %>';widnowJumpToPage()">末页</a>
				转到：第 <input type="text" id="windowPageIndexInput"
				name="windowPageIndexInput" size="3" /> 页 <input type="button"
				onclick="javascript:widnowJumpToPage()" value="Go" />
		</span>
			<p style="clear: both"></p></td>
	</tr>
</table>

<input type="hidden" id="windowPageCount" name="windowPageCount"
	value="<%= JSPDataBean.getFormData("windowPageCount") %>" />
<input type="hidden" id="windowPageIndex" name="windowPageIndex"
	value="<%= JSPDataBean.getFormData("windowPageIndex") %>" />
