package SunlightMQ;

import SunlightFrame.config.AppConfig;

public class AppKeys {
	public static String MD5_KYE = "comecho519";// 消息体加密密钥

	public static long JMSEXPIRATION_TIME = 30 * 1000;

	public static String RESPONSE_TOPIC_NAME = "RESPONSE_TOPIC_NAME";

	public static String RESPONSE_IDENTITY = "RESPONSE_IDENTITY";

	public static long msgIDIndex = 1000;

	public static String MSG_IDENTITY_PRE = "";

	static {
		MSG_IDENTITY_PRE = AppConfig.getInstance().getParameterConfig().getParameter("MSG_IDENTITY_PRE");
	}
}
