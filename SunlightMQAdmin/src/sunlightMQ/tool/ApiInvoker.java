package sunlightMQ.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.security.GeneralSecurityException;  
import java.security.KeyStore;  
  
import javax.net.ssl.HostnameVerifier;  
import javax.net.ssl.HttpsURLConnection;  
import javax.net.ssl.KeyManagerFactory;  
import javax.net.ssl.SSLContext;  
import javax.net.ssl.TrustManagerFactory;

public class ApiInvoker {
	public static String postData(Map<String, String> requestParas, String postUrl) throws Exception {
		HttpClient httpclient = null;
		try {
			httpclient = new DefaultHttpClient();

			StringBuffer queryString = new StringBuffer();
			Iterator<String> iter = requestParas.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				String value = requestParas.get(key).toString();
				queryString.append("&").append(key).append("=").append(URLEncoder.encode(value, "utf-8"));
			}

			String getUrl = postUrl
					+ (postUrl.indexOf("?") != -1 ? queryString : queryString.toString().replaceFirst("&", "?"));
			System.out.println(getUrl);
			HttpGet getMethod = new HttpGet(getUrl);

			HttpResponse response = httpclient.execute(getMethod);
			HttpEntity entity = response.getEntity();
			String statusCode = response.getStatusLine().toString();
			if (statusCode.indexOf("200") == -1) {
				return "";
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
		} catch (SocketException e) {
			return getSocketTimeOutCode();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpclient != null) {
				httpclient.getConnectionManager().shutdown();
			}
		}
		return "";
	}

	public static String postData2(String param, String url) {
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuffer buffer = new StringBuffer();

		try {
			URL realUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			conn.setDoInput(true);// 创建输入流，必须有
			conn.setDoOutput(true);// 创建输出流，必须有
			conn.setUseCaches(false);// 不缓存
			conn.setConnectTimeout(60000);// 连接超时 //1000 * 60 * 5 =
			conn.setReadTimeout(60000);// 响应超时
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Length", String.valueOf(param.getBytes("UTF-8").length));// 文件大小
			conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");// json;x-www-form-urlencoded
			conn.addRequestProperty("Connection", "Keep-Alive");// 连接方式，此处为长连接
			out = new PrintWriter(conn.getOutputStream());
			out.print(param);
			out.flush();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return buffer.toString();
	}

	/** 
     * 获得KeyStore. 
     * @param keyStorePath 
     *            密钥库路径 
     * @param password 
     *            密码 
     * @return 密钥库 
     * @throws Exception 
     */  
    public static KeyStore getKeyStore(String password, String keyStorePath)  
            throws Exception {  
        // 实例化密钥库 KeyStore用于存放证书，创建对象时 指定交换数字证书的加密标准 
        //指定交换数字证书的加密标准 
        KeyStore ks = KeyStore.getInstance("JKS");  
        // 获得密钥库文件流  
        FileInputStream is = new FileInputStream(keyStorePath);  
        // 加载密钥库  
        ks.load(is, password.toCharArray());  
        // 关闭密钥库文件流  
        is.close();  
        return ks;  
    }
    
    /** 
     * 获得SSLSocketFactory. 
     * @param password 
     *            密码 
     * @param keyStorePath 
     *            密钥库路径 
     * @param trustStorePath 
     *            信任库路径 
     * @return SSLSocketFactory 
     * @throws Exception 
     */  
    public static SSLContext getSSLContext(String password,  
            String keyStorePath, String trustStorePath) throws Exception {  
        // 实例化密钥库   KeyManager选择证书证明自己的身份
        KeyManagerFactory keyManagerFactory = KeyManagerFactory  
                .getInstance(KeyManagerFactory.getDefaultAlgorithm());  
        // 获得密钥库  
        KeyStore keyStore = getKeyStore(password, keyStorePath);  
        // 初始化密钥工厂  
        keyManagerFactory.init(keyStore, password.toCharArray());  
  
        // 实例化信任库    TrustManager决定是否信任对方的证书
        TrustManagerFactory trustManagerFactory = TrustManagerFactory  
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());  
        // 获得信任库  
        KeyStore trustStore = getKeyStore(password, trustStorePath);  
        // 初始化信任库  
        trustManagerFactory.init(trustStore);  
        // 实例化SSL上下文  
        SSLContext ctx = SSLContext.getInstance("TLS");  
        // 初始化SSL上下文  
        ctx.init(keyManagerFactory.getKeyManagers(),  
                trustManagerFactory.getTrustManagers(), null);  
        // 获得SSLSocketFactory  
        return ctx;  
    }
	
    /** 
     * 初始化HttpsURLConnection. 
     * @param password 
     *            密码 
     * @param keyStorePath 
     *            密钥库路径 
     * @param trustStorePath 
     *            信任库路径 
     * @throws Exception 
     */  
    public static void initHttpsURLConnection(String password,  
            String keyStorePath, String trustStorePath) throws Exception {  
        // 声明SSL上下文  
        SSLContext sslContext = null;  
        // 实例化主机名验证接口  
        HostnameVerifier hnv = new HttpsHostnameVerifier();  
        try {  
            sslContext = getSSLContext(password, keyStorePath, trustStorePath);  
        } catch (GeneralSecurityException e) {  
            e.printStackTrace();  
        }  
        if (sslContext != null) {  
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext  
                    .getSocketFactory());  
        }  
        HttpsURLConnection.setDefaultHostnameVerifier(hnv);  
    } 
	
	public static String postDataAndCert(String param, String url) {

        // 密码  
        String password = "comecho";  
        // 密钥库  
        String keyStorePath = "F:\\echoes\\SunlightMQ\\product\\keystore\\echoes.keystore";  
        // 信任库  
        String trustStorePath = "F:\\echoes\\SunlightMQ\\product\\keystore\\echoes.keystore";  

//		try {
//			initHttpsURLConnection(password, keyStorePath, trustStorePath);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
        // 声明SSL上下文  
        SSLContext sslContext = null;  

        try {  
            sslContext = getSSLContext(password, keyStorePath, trustStorePath);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        
        if (sslContext != null) {  
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext  
                    .getSocketFactory());  
        } 
        
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuffer buffer = new StringBuffer();

		try {
			URL realUrl = new URL(url);
			HttpsURLConnection conn = (HttpsURLConnection) realUrl.openConnection();
			conn.setDoInput(true);// 创建输入流，必须有
			conn.setDoOutput(true);// 创建输出流，必须有
			conn.setUseCaches(false);// 不缓存
			conn.setConnectTimeout(60000);// 连接超时 //1000 * 60 * 5 =
			conn.setReadTimeout(60000);// 响应超时
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Length", String.valueOf(param.getBytes("UTF-8").length));// 文件大小
			conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");// json;x-www-form-urlencoded
			conn.addRequestProperty("Connection", "Keep-Alive");// 连接方式，此处为长连接
			out = new PrintWriter(conn.getOutputStream());
			out.print(param);
			out.flush();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return buffer.toString();
	}
	
	public static String postData3(String param, String url) {
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuffer buffer = new StringBuffer();

		try {
			URL realUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			conn.setDoInput(true);// 创建输入流，必须有
			conn.setDoOutput(true);// 创建输出流，必须有
			conn.setUseCaches(false);// 不缓存
			conn.setConnectTimeout(60000);// 连接超时
			conn.setReadTimeout(60000);// 响应超时
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Length", String.valueOf(param.getBytes("UTF-8").length));// 文件大小
			conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");// json;x-www-form-urlencoded
			conn.addRequestProperty("Connection", "Keep-Alive");// 连接方式，此处为长连接
			out = new PrintWriter(conn.getOutputStream());
			out.print(param);
			out.flush();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return buffer.toString();
	}

	public static String getSocketTimeOutCode() {
		return "{\"code\":\"00100\",\"msg\":\"网络错误\"}";
	}

	public static String postData1(String param, String url) {

		PrintWriter out = null;
		BufferedReader in = null;
		StringBuffer buffer = new StringBuffer();
		try {

			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			// 设置通用的请求属性

			conn.setDoInput(true);// 创建输入流，必须有
			conn.setDoOutput(true);// 创建输出流，必须有
			conn.setUseCaches(false);// 不缓存
			conn.setConnectTimeout(60000);// 连接超时
			conn.setReadTimeout(60000);// 响应超时
			conn.setRequestMethod("POST");

			conn.addRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Length", String.valueOf(param.getBytes("UTF-8").length));// 文件大小
			conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.addRequestProperty("Connection", "Keep-Alive");// 连接方式，此处为长连接

			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = "";
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}

		} catch (Exception ex) {
			System.out.println("出错");
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
			}
		}
		System.out.println(buffer.toString());
		return buffer.toString();
	}

	public static void main(String[] args) throws Exception {
		String param = readTxtFile("D:/workspace/B2BSystem/src/b2BSystem/test/test.txt");
		postData1(param, "http://192.168.3.218:8080/router");
	}

	public static String readTxtFile(String filePath) {
		StringBuffer sb = new StringBuffer();
		try {
			String encoding = "UTF-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					sb.append(lineTxt);
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return sb.toString();

	}
}
