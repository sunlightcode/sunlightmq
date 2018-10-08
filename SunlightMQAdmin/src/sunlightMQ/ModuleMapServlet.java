package sunlightMQ;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModuleMapServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Hashtable<String, String[]> servletPathToModuleHash = new Hashtable<String, String[]>();

	static {
		String[][] modules = { { "/rest.do", "jsonAPI", "rest" } };

		for (int i = 0; i < modules.length; i++) {
			servletPathToModuleHash.put(modules[i][0], modules[i]);
		}
	}

	protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		arg0.setCharacterEncoding("UTF-8"); // 解决客户端传递过来的中文乱码问题

		String servletPath = arg0.getServletPath();
		// String currentPath = arg0.getRequestURL().toString();

		String queryString = arg0.getQueryString();
		// if (queryString != null && !queryString.equals("")) {
		// currentPath += "?" + queryString;
		// }

		if (queryString != null && queryString.indexOf("<") > 0) {
			arg1.sendRedirect("/index.html");
			return;
		}

		if (servletPathToModuleHash.get(servletPath) != null) {
			String[] ss = servletPathToModuleHash.get(servletPath);
			dispatchByModuleAndAction(ss[1], ss[2], arg0, arg1);
		} else if (servletPath.equals("/router")) {
			getServletContext().getRequestDispatcher("/bussinessEntry?module=router&action=rest" + "&" + queryString)
					.forward(arg0, arg1);
		} else if (servletPath.equals("/bWRouter")) {
			getServletContext().getRequestDispatcher("/bussinessEntry?module=bWRouter&action=rest" + "&" + queryString)
					.forward(arg0, arg1);
		}
	}

	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doPost(arg0, arg1);
	}

	public void dispatchByModuleAndAction(String module, String action, HttpServletRequest arg0,
			HttpServletResponse arg1) throws ServletException, IOException {
		try {
			getServletContext().getRequestDispatcher("/bussinessEntry?module=" + module + "&action=" + action)
					.forward(arg0, arg1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
