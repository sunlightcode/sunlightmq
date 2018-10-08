package sunlightMQ;

import SunlightFrame.config.AppConfig;

public class AppKeys {
	public static String AJAX_RESULT = "AJAX_RESULT";

	public static String UPLOAD_FILE_PATH = "";

	public static String DOMAIN_ADMIND = "";
	public static String DOMAIN_WWW = "";

	public static String SESSIONKEY = "SESSIONKEY";

	public static String CMD_QUEUE = "CMD_QUEUE";

	public static String STATUS_QUEUE = "STATUS_QUEUE";

	public static int ACK_AUTOACKNOWLEDGE = 1;
	public static int ACK_DUPSOKACKNOWLEDGE = 2;
	public static int ACK_CLIENTACKNOWLEDGE = 3;
	public static int ACK_TRANSACTIONAL = 4;
	public static int ACK_INDIVIDUALACKNOWLEDGE = 5;

	public static String REQUESTTYPE_GET = "1";
	public static String REQUESTTYPE_POST = "2";
	public static String REQUESTTYPE_PUT = "3";
	public static String REQUESTTYPE_DELETE = "4";

	public static String PROCESSEXCEPTIONMODE_IGNORE = "1";
	public static String PROCESSEXCEPTIONMODE_BLOCKING = "2";

	public static String PROCESSEXCEPTIONREPORTMODE_CALLBACK = "1";
	public static String PROCESSEXCEPTIONREPORTMODE_EMAIL = "2";
	public static String PROCESSEXCEPTIONREPORTMODE_SMS = "3";

	public static String CALLBACKMETHODTYPE_API = "1";
	public static String CALLBACKMETHODTYPE_QUEUE = "2";

	static {
		DOMAIN_ADMIND = AppConfig.getInstance().getParameterConfig().getParameter("adminDomain");
		DOMAIN_WWW = AppConfig.getInstance().getParameterConfig().getParameter("wwwDomain");
	}

	public static void setUploadFilePath(String path) {
		UPLOAD_FILE_PATH = path;
	}
}
