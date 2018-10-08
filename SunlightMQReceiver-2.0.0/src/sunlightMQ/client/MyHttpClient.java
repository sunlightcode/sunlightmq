package sunlightMQ.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import SunlightFrame.log.AppLogger;
import sunlightMQ.AppKeys;
import sunlightMQ.AppUtils;
import sunlightMQ.DataCache;
import sunlightMQ.Result;
import sunlightMQ.bean.CatchExceptionBean;
import sunlightMQ.jms.CatchExcption;

public class MyHttpClient {
	public static String postData(Map<String, String> requestParas, String postUrl, CatchExceptionBean bean)
			throws Exception {
		String trace = AppUtils.getTrace(AppKeys.RECEIVER_NAME, AppKeys.BUSSINESS_API);
		AppLogger.getInstance().infoLog(trace + " [" + postUrl + "] [" + requestParas + "]");

		HttpClient httpclient = null;
		HttpGet getMethod = null;
		try {
			httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, AppKeys.TIME_OUT_INVOKER);
			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, AppKeys.TIME_OUT_INVOKER);

			StringBuffer queryString = new StringBuffer();
			Iterator<String> iter = requestParas.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				String value = requestParas.get(key).toString();
				queryString.append("&").append(key).append("=").append(URLEncoder.encode(value, "utf-8"));
			}

			String getUrl = postUrl
					+ (postUrl.indexOf("?") != -1 ? queryString : queryString.toString().replaceFirst("&", "?"));
			getMethod = new HttpGet(getUrl);

			HttpResponse response = httpclient.execute(getMethod);
			HttpEntity entity = response.getEntity();
			String statusCode = response.getStatusLine().toString();
			if (statusCode.indexOf("200") == -1) {
				return new Result("00501", "Get方式调用接口失败", null).toJSON();
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();
			String json = sb.toString();
			return json;
		} catch (Exception e) {
			AppLogger.getInstance().infoLog(trace + "出错:" + postUrl + " error info：" + e.getMessage());
			AppLogger.getInstance().errorLog(trace + "出错:" + postUrl, e);

			bean.setExceptionContent("Get方式调用接口");
			CatchExcption.log2DB(bean);
			return new Result("00502", "Get方式调用接口出错", null).toJSON();
		} finally {
			if (httpclient != null) {
				httpclient.getConnectionManager().shutdown();
			}
		}
	}

	public static String postDataSelf(String param, String url, CatchExceptionBean bean) {
		String trace = AppUtils.getTrace(AppKeys.RECEIVER_NAME, AppKeys.MUTIL_QUEUE);
		AppLogger.getInstance().infoLog(trace + " [" + url + "] [" + param + "]");

		HttpURLConnection conn = null;
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuffer buffer = new StringBuffer();
		try {
			URL realUrl = new URL(url);
			conn = (HttpURLConnection) realUrl.openConnection();

			conn.setDoInput(true);// 创建输入流，必须有
			conn.setDoOutput(true);// 创建输出流，必须有
			conn.setUseCaches(false);// 不缓存
			conn.setConnectTimeout(AppKeys.TIME_OUT_INVOKER);// 连接超时
			conn.setReadTimeout(AppKeys.TIME_OUT_INVOKER);// 响应超时
			conn.setRequestMethod("POST");

			conn.addRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Length", String.valueOf(param.getBytes("UTF-8").length));// 文件大小
			conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// conn.addRequestProperty("Connection", "Keep-Alive");//
			// 连接方式，此处为长连接

			out = new PrintWriter(conn.getOutputStream());
			out.print(param);
			out.flush();

			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = "";
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
		} catch (Exception ex) {
			AppLogger.getInstance().infoLog(trace + "出错:" + url + " error info：" + ex.getMessage());
			AppLogger.getInstance().errorLog(trace + "出错:" + url, ex);

			bean.setExceptionContent("调用混合队列出错:" + ex.getMessage());
			CatchExcption.log2DB(bean);
			return new Result("00503", "调用混合队列出错", null).toJSON();
		} finally {
			closeResource(out, in, conn);
		}

		return buffer.toString();
	}

	public static String postB2CData(String param, String url, CatchExceptionBean bean) {
		String trace = AppUtils.getTrace(AppKeys.RECEIVER_NAME, AppKeys.B2C_API);
		AppLogger.getInstance().infoLog(trace + " [" + url + "] [" + param + "]");

		HttpURLConnection conn = null;
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuffer buffer = new StringBuffer();
		try {
			URL realUrl = new URL(url);
			conn = (HttpURLConnection) realUrl.openConnection();

			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(AppKeys.TIME_OUT_INVOKER);
			conn.setReadTimeout(AppKeys.TIME_OUT_INVOKER);
			conn.setRequestMethod("POST");

			conn.addRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Length", String.valueOf(param.getBytes("UTF-8").length));// 文件大小
			conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// conn.addRequestProperty("Connection", "Keep-Alive");//
			// 连接方式，此处为长连接

			out = new PrintWriter(conn.getOutputStream());
			out.print(param);
			out.flush();

			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = "";
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}

		} catch (Exception ex) {
			AppLogger.getInstance().infoLog(trace + "出错:" + url + " error info：" + ex.getMessage());
			AppLogger.getInstance().errorLog(trace + "出错:" + url, ex);

			bean.setExceptionContent("调用B2C接口出错:" + ex.getMessage());
			CatchExcption.log2DB(bean);
			return new Result("00504", "调用B2C接口出错", null).toJSON();
		} finally {
			closeResource(out, in, conn);
		}
		return buffer.toString();

	}

	public static String postJsonData(String param, String url, CatchExceptionBean bean) {
		String trace = AppUtils.getTrace(AppKeys.RECEIVER_NAME, AppKeys.BUSSINESS_API);
		AppLogger.getInstance().infoLog(trace + " [" + url + "] [" + param + "]");

		HttpURLConnection conn = null;
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuffer buffer = new StringBuffer();
		try {

			URL realUrl = new URL(url);
			conn = (HttpURLConnection) realUrl.openConnection();

			conn.setDoInput(true);// 创建输入流，必须有
			conn.setDoOutput(true);// 创建输出流，必须有
			conn.setUseCaches(false);// 不缓存
			conn.setConnectTimeout(AppKeys.TIME_OUT_INVOKER);// 连接超时
			conn.setReadTimeout(AppKeys.TIME_OUT_INVOKER);// 响应超时
			conn.setRequestMethod("POST");

			conn.addRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Length", String.valueOf(param.getBytes("UTF-8").length));// 文件大小
			conn.addRequestProperty("Content-Type", "application/json");
			// conn.addRequestProperty("Connection", "Keep-Alive");//
			// 连接方式，此处为长连接

			out = new PrintWriter(conn.getOutputStream());
			out.print(param);
			out.flush();

			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = "";
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
		} catch (Exception ex) {
			AppLogger.getInstance().infoLog(trace + "出错:" + url + " error info：" + ex.getMessage());
			AppLogger.getInstance().errorLog(trace + "出错:" + url, ex);

			bean.setExceptionContent("调用业务系统接口出错:" + ex.getMessage());
			CatchExcption.log2DB(bean);
			return new Result("00505", "调用业务系统接口出错", null).toJSON();
		} finally {
			closeResource(out, in, conn);
		}
		return buffer.toString();
	}

	public static void closeResource(PrintWriter out, BufferedReader in, HttpURLConnection conn) {
		try {
			if (out != null) {
				out.close();
				AppLogger.getInstance().infoLog("关闭out");
			}
		} catch (Exception e2) {
		}

		try {
			if (in != null) {
				in.close();
				AppLogger.getInstance().infoLog("关闭in");
			}
		} catch (Exception e2) {
		}

		try {
			if (conn != null) {
				conn.disconnect();
				AppLogger.getInstance().infoLog("关闭conn");
			}
		} catch (Exception e2) {
		}
	}

	/**
	 * 发送跨中心路由器
	 * 
	 * @param bean
	 * @return
	 */
	public static String postRouteData(String url, String sendMessage, String syncFlag, String otherCenterUrl,
			CatchExceptionBean bean) {
		String trace = AppUtils.getTrace(AppKeys.RECEIVER_NAME, AppKeys.ROUTER_NAME);
		AppLogger.getInstance().infoLog(trace + sendMessage);

		org.apache.commons.httpclient.HttpClient client = null;
		PostMethod postMethod = null;
		String msg = null;
		try {
			String targetURL = url;
			postMethod = new PostMethod(targetURL);
			Map<String, String> sendMessageParam = JSON.parseObject(sendMessage,
					new TypeReference<Map<String, String>>() {
					});
			Hashtable<String, String> business = DataCache.getInstance()
					.getBusiness(sendMessageParam.get("businessName"));
			// Map<String, String> message = new HashMap<String, String>();
			// message.put("userName", "a");
			// message.put("password","a");
			// message.put("queueName",business.get("c_queueName"));
			// message.put("isTopic","0");
			// message.put("persistent","1");
			// message.put("ackType","1");
			// message.put("text",sendMessage);
			// message.put("sync",syncFlag);

			postMethod.setParameter("message", sendMessage);
			postMethod.setParameter("masterServer", otherCenterUrl);
			// postMethod.setParameter("queueName", otherCenterQueue);
			postMethod.setParameter("queueName", business.get("c_queueName"));
			postMethod.setParameter("username", AppKeys.ROUTER_USERNAME);
			postMethod.setParameter("password", AppKeys.ROUTER_PASSWORD);
			postMethod.setParameter("sync", "0");

			postMethod.setParameter("isTopic", "0");
			postMethod.setParameter("persistent", "0");
			postMethod.setParameter("ackType", "1");
			postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
			client = new org.apache.commons.httpclient.HttpClient();
			// client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
			// AppKeys.TIME_OUT_INVOKER);
			// client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
			// AppKeys.TIME_OUT_INVOKER);

			int status = client.executeMethod(postMethod);
			if (status == HttpStatus.SC_OK) {
				msg = postMethod.getResponseBodyAsString();
				return msg;
			} else {
				return new Result("00506", "调用跨中心路由器失败", null).toJSON();
			}
		} catch (Exception e) {
			AppLogger.getInstance().infoLog(trace + "错误:" + url + " error info：" + e.getMessage());
			AppLogger.getInstance().errorLog(trace + "错误:" + url, e);
			bean.setExceptionContent("发送跨中心数据至路由器错误:" + e.getMessage());
			CatchExcption.log2DB(bean);
			e.printStackTrace();
			return new Result("00507", "调用跨中心路由器出错", null).toJSON();
		} finally {
			if (postMethod != null) {
				postMethod.releaseConnection();
			}

			if (client != null) {
				client.getHttpConnectionManager().closeIdleConnections(0);
			}
		}
	}

	public static String postRouteData(String url, String masterUrl, String sendMessage) {
		String trace = AppUtils.getTrace(AppKeys.RECEIVER_NAME, AppKeys.ROUTER_NAME);
		AppLogger.getInstance().infoLog(trace + sendMessage);

		org.apache.commons.httpclient.HttpClient client = null;
		PostMethod postMethod = null;
		String msg = null;
		try {
			postMethod = new PostMethod(url);

			postMethod.setParameter("message", sendMessage);
			postMethod.setParameter("masterServer", masterUrl);
			postMethod.setParameter("queueName", AppKeys.RESPONSE_TOPIC_NAME);
			postMethod.setParameter("username", AppKeys.ROUTER_USERNAME);
			postMethod.setParameter("password", AppKeys.ROUTER_PASSWORD);
			postMethod.setParameter("sync", "0");

			postMethod.setParameter("isTopic", "1");
			postMethod.setParameter("persistent", "0");
			postMethod.setParameter("ackType", "1");
			postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
			client = new org.apache.commons.httpclient.HttpClient();
			// client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
			// AppKeys.TIME_OUT_INVOKER);
			// client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
			// AppKeys.TIME_OUT_INVOKER);

			int status = client.executeMethod(postMethod);
			if (status == HttpStatus.SC_OK) {
				msg = postMethod.getResponseBodyAsString();
				return msg;
			} else {
				return new Result("00506", "调用跨中心路由器失败", null).toJSON();
			}
		} catch (Exception e) {
			AppLogger.getInstance().infoLog(trace + "错误:" + url + " error info：" + e.getMessage());
			AppLogger.getInstance().errorLog(trace + "错误:" + url, e);
			return new Result("00507", "调用跨中心路由器出错", null).toJSON();
		} finally {
			if (postMethod != null) {
				postMethod.releaseConnection();
			}

			if (client != null) {
				client.getHttpConnectionManager().closeIdleConnections(0);
			}
		}

	}
}
