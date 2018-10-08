package SunlightMQ;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import SunlightFrame.config.AppConfig;
import SunlightFrame.log.AppLogger;
import SunlightFrame.web.AbstractEntry;
import SunlightFrame.web.FrameKeys;
import flowController.core.FlowControllerServer;

public class ApplicationEntry extends AbstractEntry {
	private static final long serialVersionUID = 1L;

	public boolean checkPriority(String moduleName, String actionName, HttpServletRequest request) {
		if (!moduleName.equals("adminLogin") && !moduleName.equals("recache") && !moduleName.equals("ajax")
				&& !moduleName.equals("router") && !moduleName.equals("bWRouter") && !moduleName.equals("test")
				&& !moduleName.equals("check") && !moduleName.equals("queue")) {
			if (request.getSession().getAttribute(FrameKeys.LOGIN_USER) == null) {
				return false;
			}
		}

		return true;
	}

	public String getPriorityErorrPage() {
		return "expired.jsp";
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String servletPath = request.getServletPath();
		if (request.getParameter("module") != null && request.getParameter("action") != null) {
			if (request.getParameter("module").equals("router")) {
				request.setAttribute("totalMills", System.currentTimeMillis());
			}
			if (request.getParameter("module").equals("queue")) {
				request.setAttribute("totalMills", System.currentTimeMillis());
			}
			doPost(request, response);
		} else {
			if ("/router".equals(servletPath)) {
				getServletContext().getRequestDispatcher("/admin?module=router&action=rest").forward(request, response);
			} else if ("/queue".equals(servletPath)) {
				getServletContext().getRequestDispatcher("/admin?module=queue&action=rest").forward(request, response);
			} else if (request.getSession().getAttribute(FrameKeys.LOGIN_USER) != null) {
				getServletContext().getRequestDispatcher("/admin?module=index&action=defaultView").forward(request,
						response);
			} else {
				getServletContext().getRequestDispatcher("/admin?module=adminLogin&action=defaultView").forward(request,
						response);
			}
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String servletPath = request.getServletPath();
		response.setHeader("Access-Control-Allow-Origin", "*");//Ajax跨域问题处理
		if ("/router".equals(servletPath)) {
			getServletContext().getRequestDispatcher("/admin?module=router&action=rest").forward(request, response);
			return;
		}

		if ("/queue".equals(servletPath)) {
			getServletContext().getRequestDispatcher("/admin?module=queue&action=rest").forward(request, response);
			return;
		}
		super.doPost(request, response);
	}

	@Override
	public void entryInit() {
		try {
			DataCache.getInstance().reload();
			// CheckMQConnectionProcessorThread thread = new
			// CheckMQConnectionProcessorThread();
			// thread.start();

			Boolean startJmxActiveMQ = Boolean.valueOf(
					AppConfig.getInstance().getParameterConfig().getParameter("flowController.startJmxActiveMQ"));
			String jmxServiceURL = AppConfig.getInstance().getParameterConfig()
					.getParameter("flowController.jmxServiceURL");
			String objectName = AppConfig.getInstance().getParameterConfig().getParameter("flowController.objectName");
			Integer timeInterval = Integer
					.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("flowController.timeInterval"));
			AppLogger.getInstance().infoLog("SunlightMQManager: startJmxActiveMQ=" + startJmxActiveMQ + "; jmxServiceURL="
					+ jmxServiceURL + "; objectName=" + objectName + "; timeInterval=" + timeInterval);
			
			FlowControllerServer fcs = new FlowControllerServer();
			fcs.setStartJmxActiveMQ(startJmxActiveMQ);
			fcs.setJmxServiceURL(jmxServiceURL);
			fcs.setObjectName(objectName);
			fcs.setTimeInterval(timeInterval);
			fcs.startServer();
			AppLogger.getInstance().infoLog("SunlightMQManager: FlowControllerServer is running!");
		} catch (Exception e) {
			AppLogger.getInstance().errorLog("缓存加载失败", e);
		}
	}

	@Override
	public void destroy() {
		try {
			DataCache.getInstance().clear();
		} catch (Exception e) {
			AppLogger.getInstance().errorLog("清空缓存出错", e);
		}
	}
}
