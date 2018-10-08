package sunlightMQ;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import SunlightFrame.config.AppConfig;
import SunlightFrame.log.AppLogger;
import SunlightFrame.web.FrameKeys;

public class DownloadEntry extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		String fileName = request.getParameter("fileName");
		String dir = request.getParameter("dir");

		String downloadDir = AppKeys.UPLOAD_FILE_PATH + File.separator + dir + File.separator;

		if (dir.equals("key")) {
			downloadDir = AppConfig.getInstance().getApplicationRoot() + File.separator + "WEB-INF" + File.separator
					+ "sslFile" + File.separator;
		}

		if (request.getSession().getAttribute(FrameKeys.LOGIN_USER) == null) {
			AppLogger.getInstance().infoLog("donwload un logined...");
			return;
		}

		response.setContentType("APPLICATION/OCTET-STREAM");
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");

		FileInputStream fileInputStream = null;
		try {
			// 打开指定文件的流信息
			fileInputStream = new java.io.FileInputStream(downloadDir + fileName);

			// 写出流信息
			int i;
			while ((i = fileInputStream.read()) != -1) {
				response.getOutputStream().write(i);
			}

		} catch (Exception e) {
			AppLogger.getInstance().errorLog("error happened when download file: " + fileName, e);
		} finally {
			// try {
			// if (fileInputStream != null) {
			// fileInputStream.close();
			// if (dir.equals("key")) {
			// new File(downloadDir
			// + fileName).delete();
			// }
			// }
			// } catch (Exception e) {
			// }
			response.getOutputStream().close();
		}
	}

}
