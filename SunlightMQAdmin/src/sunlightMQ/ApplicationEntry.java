package sunlightMQ;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import SunlightFrame.log.AppLogger;
import SunlightFrame.web.AbstractEntry;
import SunlightFrame.web.FrameKeys;

public class ApplicationEntry extends AbstractEntry {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	public boolean checkPriority(String moduleName, String actionName, HttpServletRequest request) {
		if (!moduleName.equals("adminLogin") && !moduleName.equals("recache") && !moduleName.equals("ajax")
				&& !moduleName.equals("router") && !moduleName.equals("bWRouter") && !moduleName.equals("test")) {
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
		if (request.getParameter("module") != null && request.getParameter("action") != null) {
			if (request.getParameter("module").equals("router")) {
				request.setAttribute("totalMills", System.currentTimeMillis());
			}
			doPost(request, response);
		} else {
			if (request.getSession().getAttribute(FrameKeys.LOGIN_USER) != null) {
				getServletContext().getRequestDispatcher("/admin?module=index&action=defaultView").forward(request,
						response);
			} else {
				getServletContext().getRequestDispatcher("/admin?module=adminLogin&action=defaultView").forward(request,
						response);
			}
		}
	}

	@Override
	public void entryInit() {
		DataCache.getInstance().loadData();
		CheckMsgProcessorThread.getInstance().start();
		CheckTMQStatusThread.getInstance().start();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void destroy() {
		try {
			DataCache.getInstance().clear();
			CheckMsgProcessorThread.getInstance().stop();
			CheckTMQStatusThread.getInstance().stop();
		} catch (Exception e) {
			AppLogger.getInstance().errorLog("清空缓存出错", e);
		}
	}
}
