<%@ page contentType="text/html;charset=UTF-8"%>
<jsp:directive.page import="java.util.Vector" />
<jsp:directive.page import="java.util.Hashtable" />
<jsp:directive.page import="SunlightFrame.config.AppConfig" />
<jsp:directive.page import="SunlightFrame.web.FrameKeys" />
<jsp:directive.page import="sunlightMQ.AppKeys" />
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>

<jsp:useBean id="JSPDataBean" scope='request'
	class="SunlightFrame.web.JSPDataBean" />

<%@include file="common/commonHeader.jsp"%>

<script type="text/javascript">
function ctnTree(obj) {
}
</script>

<div id="menu">

	<div id="leftMenu5">

		<div class="x-tree-node-folder">消息队列监控</div>

		<!--

<div class="x-tree-node-leaf" onclick=""><a href="javascript:postModuleAndActionToTarget('systemRole', 'defaultView', 'rightFrame');">角色管理</a></div>

<div class="x-tree-node-leaf" onclick=""><a href="javascript:doAction('recache');">重启缓存</a></div>

<div class="x-tree-node-leaf" onclick=""><a href="javascript:postModuleAndActionToTarget('password', 'defaultView', 'rightFrame');">修改密码</a></div>
-->
		<div class="x-tree-node-leaf" onclick="">
			<a
				href="javascript:postModuleAndActionToTarget('queueState', 'defaultView', 'rightFrame');">队列状态监控</a>
		</div>

		<div class="x-tree-node-leaf3" onclick="">
			<a
				href="javascript:postModuleAndActionToTarget('exceptionLog', 'defaultView', 'rightFrame');">错误日志查询</a>
		</div>


		<div class="x-tree-node-folder">消息队列管理</div>

		<div class="x-tree-node-leaf5" onclick="">
			<a
				href="javascript:postModuleAndActionToTarget('queue', 'defaultView', 'rightFrame');">消息队列管理</a>
		</div>

		<div class="x-tree-node-leaf2" onclick="">
			<a
				href="javascript:postModuleAndActionToTarget('msgProcessor', 'defaultView', 'rightFrame');">应用接口管理</a>
		</div>

		<div class="x-tree-node-leaf7" onclick="">
			<a
				href="javascript:postModuleAndActionToTarget('service', 'defaultView', 'rightFrame');">接入服务管理</a>
		</div>

		<div class="x-tree-node-leaf6" onclick="">
			<a
				href="javascript:postModuleAndActionToTarget('client', 'defaultView', 'rightFrame');">服务秘钥管理</a>
		</div>


		<div class="x-tree-node-folder">消息中心管理</div>

		<div class="x-tree-node-leaf8" onclick="">
			<a
				href="javascript:postModuleAndActionToTarget('mqServer', 'defaultView', 'rightFrame');">服务器组管理</a>
		</div>

		<div class="x-tree-node-leaf4" onclick="">
			<a
				href="javascript:postModuleAndActionToTarget('msgCenter', 'defaultView', 'rightFrame');">消息中心管理</a>
		</div>


		<div class="x-tree-node-folder">系统用户管理</div>

		<div class="x-tree-node-leaf9" onclick="">
			<a
				href="javascript:postModuleAndActionToTarget('systemUser', 'defaultView', 'rightFrame');">系统用户管理</a>
		</div>

		<div class="x-tree-node-leaf10" onclick="">
			<a
				href="javascript:postModuleAndActionToTarget('systemRole', 'defaultView', 'rightFrame');">系统角色管理</a>
		</div>

	</div>

</div>

<input type="hidden" name="recacheSystemUserID" id="recacheSystemUserID"
	value="<%= ((Hashtable) request.getSession().getAttribute(FrameKeys.LOGIN_USER)).get("systemUserID") %>" />
<input type="hidden" name="recachePassword" id="recachePassword"
	value="<%= ((Hashtable) request.getSession().getAttribute(FrameKeys.LOGIN_USER)).get("password") %>" />

<input type="hidden" name="q_clientTypeID" id="q_clientTypeID"
	value="<%= JSPDataBean.getFormData("q_clientTypeID") %>" />

<%
	Calendar c = Calendar.getInstance();
	Date toDate = c.getTime();
	c.setTime(new Date());
	c.add(Calendar.DATE, -15);
	Date fromDate = c.getTime();
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String q_addTimeFrom = sdf.format(fromDate);
	String q_addTimeTo = sdf.format(toDate);
%>

<input type="hidden" name="q_addTimeFrom" id="q_addTimeFrom"
	value="<%= q_addTimeFrom %>" />
<input type="hidden" name="q_addTimeTo" id="q_addTimeTo"
	value="<%= q_addTimeTo %>" />
<%@include file="common/commonFooter.jsp"%>
