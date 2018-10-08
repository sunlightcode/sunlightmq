package sunlightMQ.tool;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

public class MyHttpClient {
	public static final String PROTOCOL_HTTP = "http";
	public static final String PROTOCOL_HTTPS = "https";

	public static HttpClient getClient(String protocol, int port, String charSet) {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme(protocol, PlainSocketFactory.getSocketFactory(), port));

		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, (charSet == null || charSet.equals("")) ? "UTF-8" : charSet);
		HttpProtocolParams.setUseExpectContinue(params, true);
		params.setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
		// 的网站会先判别用户的请求是否是来自浏览器，如不是，则返回不正确的文本
		HttpProtocolParams.setUserAgent(params, "Mozilla/5.0 (Windows; U;" + " Windows NT 5.1; zh-CN; rv:1.9.1.2)");

		ClientConnectionManager connMgr = new ThreadSafeClientConnManager(params, schemeRegistry);
		return new DefaultHttpClient(connMgr, params);
	}

	public static HttpClient getClient() {
		return new DefaultHttpClient();
	}

	public static String excuteGetMethod(String url, String requestCharSet) throws Exception {
		String res = "";

		String[] urlInfo = getUrlInfo(url);

		String protocol = urlInfo[0];
		String hostStr = urlInfo[1];
		String port = urlInfo[2];
		String uri = urlInfo[3];

		HttpClient client = null;

		HttpGet get = null;
		try {
			client = getClient(protocol, Integer.parseInt(port), requestCharSet);

			HttpHost host = new HttpHost(hostStr);

			get = new HttpGet(uri);
			HttpResponse response = client.execute(host, get);
			HttpEntity entity = response.getEntity();

			String redirect = getRedirectLocation(response);
			if (redirect.equals("")) {
				res = getResponseContent(entity);
			} else {
				if (!redirect.startsWith("http://") && !redirect.startsWith("https://")) {
					redirect = protocol + "://" + hostStr + (port.equals("") ? "" : (":" + port))
							+ (redirect.startsWith("/") ? redirect : ("/" + redirect));
				}
				res = excuteGetMethod(redirect, getResponseContentCharSet(entity));
			}
		} finally {
			if (get != null) {
				get.abort();
			}

			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}

		return res;
	}

	public static String excutePostMethod(Hashtable<String, String> paras, String url, String requestCharSet)
			throws Exception {
		String res = "";

		String[] urlInfo = getUrlInfo(url);

		String protocol = urlInfo[0];
		String hostStr = urlInfo[1];
		String port = urlInfo[2];
		String uri = urlInfo[3];

		HttpClient client = null;

		HttpPost post = null;
		try {
			client = getClient(protocol, Integer.parseInt(port), requestCharSet);

			HttpHost host = new HttpHost(hostStr);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();

			Iterator<String> iter = paras.keySet().iterator();
			while (iter.hasNext()) {
				String name = iter.next();
				String value = paras.get(name);
				nvps.add(new BasicNameValuePair(name, value));
			}

			post = new HttpPost(uri);
			post.setEntity(new UrlEncodedFormEntity(nvps, "GB2312"));
			HttpResponse response = client.execute(host, post);
			HttpEntity entity = response.getEntity();

			String redirect = getRedirectLocation(response);
			if (redirect.equals("")) {
				res = getResponseContent(entity);
			} else {
				if (!redirect.startsWith("http://") && !redirect.startsWith("https://")) {
					redirect = protocol + "://" + hostStr + (port.equals("") ? "" : (":" + port))
							+ (redirect.startsWith("/") ? redirect : ("/" + redirect));
				}
				res = excuteGetMethod(redirect, getResponseContentCharSet(entity));
			}
		} finally {
			if (post != null) {
				post.abort();
			}

			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}

		return res;
	}

	public static String[] getUrlInfo(String url) {
		if (url == null || url.equals("")) {
			return new String[0];
		}

		String[] info = new String[4];
		String protocol = "http";
		String host = "";
		String port = "80";
		String uri = "";
		if (url.startsWith("http://")) {
			protocol = "http";
			url = url.replaceFirst(protocol + "://", "");

			int index = url.indexOf("/");
			if (index != -1) {
				host = url.substring(0, index);
				uri = url.substring(index);
			} else {
				host = url;
				uri = "/";
			}

			int mIndex = url.indexOf(":");
			if (mIndex != -1) {
				String oldHost = host;
				host = host.substring(0, mIndex);
				port = oldHost.substring(mIndex + 1);
			} else {
				port = "80";
			}
		} else if (url.equals("https://")) {
			protocol = "https";
			url = url.replaceFirst(protocol + "://", "");

			int index = url.indexOf("/");
			if (index != -1) {
				host = url.substring(0, index);
				uri = url.substring(index);
			} else {
				host = url;
				uri = "/";
			}

			int mIndex = url.indexOf(":");
			if (mIndex != -1) {
				String oldHost = host;
				host = host.substring(0, mIndex);
				port = oldHost.substring(mIndex + 1);
			} else {
				port = "443";
			}
		} else {
			protocol = "http";

			int index = url.indexOf("/");
			if (index != -1) {
				host = url.substring(0, index);
				uri = url.substring(index);
			} else {
				host = url;
				uri = "/";
			}

			int mIndex = url.indexOf(":");
			if (mIndex != -1) {
				String oldHost = host;
				host = host.substring(0, mIndex);
				port = oldHost.substring(mIndex + 1);
			} else {
				port = "80";
			}
		}

		info[0] = protocol;
		info[1] = host;
		info[2] = port;
		info[3] = uri;

		return info;
	}

	public static String getRedirectLocation(HttpResponse response) throws Exception {
		String sReturn;
		Header locationHeader = response.getFirstHeader("Location");
		if (locationHeader == null) {
			sReturn = "";
		} else {
			sReturn = locationHeader.getValue();
		}
		return sReturn;
	}

	private static String getResponseContent(HttpEntity responseEntity) throws Exception {
		byte[] bytes = EntityUtils.toByteArray(responseEntity);
		return new String(bytes, getResponseContentCharSet(responseEntity));
	}

	private static String getResponseContentCharSet(HttpEntity responseEntity) {
		String charSetName = EntityUtils.getContentCharSet(responseEntity);
		return charSetName == null ? "UTF-8" : charSetName;
	}
}
