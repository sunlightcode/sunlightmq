package sunlightMQ;

import java.util.concurrent.TimeUnit;

import SunlightFrame.config.AppConfig;

public class AppKeys {
	public static String AJAX_RESULT = "AJAX_RESULT";

	public static String UPLOAD_FILE_PATH = "";

	public static String DOMAIN_ADMIND = "";
	public static String DOMAIN_WWW = "";

	public static String SESSIONKEY = "SESSIONKEY";

	public static String CMD_QUEUE = "CMD_QUEUE";
	public static String STATUS_QUEUE = "STATUS_QUEUE";

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

	public static String ROUTER_USERNAME = "tiens";
	public static String ROUTER_PASSWORD = "com.tiens.www3";

	public static String LOCAL = "消息接收器";

	public static String LOCAL_IP = "127.0.0.1";

	public static TimeUnit UNIT = TimeUnit.SECONDS;

	public static String RECEIVER_NAME = "Receiver";
	public static String ROUTER_NAME = "跨中心路由器";
	public static String MUTIL_QUEUE = "混合队列";
	public static String BUSSINESS_API = "业务系统接口";
	public static String B2C_API = "B2C系统接口";

	public static int TIME_OUT_INVOKER = Integer
			.valueOf(AppConfig.getInstance().getParameterConfig().getParameter("TIME_OUT_INVOKER"));// 20
																									// *
																									// 1000

	public static int MQ_PREFETCHSIZE = 1;

	public static String RESPONSE_TOPIC_NAME = "RESPONSE_TOPIC_NAME";

	public static String RESPONSE_IDENTITY = "RESPONSE_IDENTITY";

	public static long msgIDIndex = 1000;

	public static String ROUTER_URL = "";

	static {
		DOMAIN_ADMIND = AppConfig.getInstance().getParameterConfig().getParameter("adminDomain");
		DOMAIN_WWW = AppConfig.getInstance().getParameterConfig().getParameter("wwwDomain");
		ROUTER_URL = AppConfig.getInstance().getParameterConfig().getParameter("router_url");
		// LOCAL_IP = GetIP.getMacAddr();
	}

	public static void setUploadFilePath(String path) {
		UPLOAD_FILE_PATH = path;
	}
}
