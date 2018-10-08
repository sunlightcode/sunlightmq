package SunlightMQ.service;

import java.lang.reflect.Constructor;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import SunlightFrame.config.AppConfig;

public class ServiceFactory {
	public ServiceFactory() {
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Service create(String className, String method, Hashtable<String, String> paras,
			Hashtable<String, String> sessionKey, HttpServletRequest request) throws Exception {
		Class class1 = Service.class.getClassLoader().loadClass(
				new StringBuffer(AppConfig.getInstance().getParameterConfig().getParameter("applicationName"))
						.append(".service.").append(className).toString());
		Constructor constructor = class1.getConstructor(
				new Class[] { String.class, String.class, Hashtable.class, Hashtable.class, HttpServletRequest.class });
		Service service = (Service) constructor.newInstance(
				new Object[] { className, new StringBuffer(method).toString(), paras, sessionKey, request });
		return service;
	}
}
