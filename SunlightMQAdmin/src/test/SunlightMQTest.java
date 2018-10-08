package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sunlightMQ.tool.BrandwisdomClient;

public class SunlightMQTest {

	public static void main(String[] args) throws Exception {
		sendSyncMsgTest(); //同步消息不会有剩余消息
		// sendNonsyncMsgTest();
		// sendNoSyncMsgTest_218();
	}

	/**
	 * 发送同步消息测试
	 */
	@SuppressWarnings("unused")
	private static void sendNoSyncMsgTest_218() {
		for (int i = 1; i < 1001; i++) {
			String url1 = "192.168.3.218:8080/router";
			String messageJson1 = readTxtFile("C:/aa.txt"); // readTxtFile("C:/aa.txt");//"test";//
			// readTxtFile("C:/aa.txt");
			HashMap<String, String> requestParas1 = new HashMap<String, String>();
			requestParas1.put("businessName", "testqueue1");
			requestParas1.put("syncFlag", "0");
			requestParas1.put("appKey", "1022GZDGPQABSPDH");
			try {
				requestParas1.put("messageJson", URLEncoder.encode(messageJson1, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			requestParas1.put("md5", "");
			String str1 = BrandwisdomClient.callService1(makeJson(requestParas1), url1);
			System.out.println("回值" + i + ": " + str1);
		}
	}

	/**
	 * 发送同步消息测试
	 */
	private static void sendSyncMsgTest() {
		for (int i = 1; i < 2; i++) {
			String url1 = "sender.mq.com.cn:8090/router";
			String messageJson1 = "www.sunlightcloud.com"; // readTxtFile("C:/aa.txt");//"test";//
												// readTxtFile("C:/aa.txt");
			HashMap<String, String> requestParas1 = new HashMap<String, String>();
			requestParas1.put("businessName", "wjjtest1");
			requestParas1.put("syncFlag", "1");
			requestParas1.put("appKey", "1011ER9L89XJ25PB");
			try {
				requestParas1.put("messageJson", URLEncoder.encode(messageJson1, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			requestParas1.put("md5", "");
			String str1 = BrandwisdomClient.callService1(makeJson(requestParas1), url1);
			System.out.println("回值" + i + ": " + str1);
		}
	}

	@SuppressWarnings("unused")
	private static void sendSyncMsgTest8099() {
		for (int i = 1; i < 51; i++) {
			String url1 = "sender.mq.com.cn:8099/router";
			String messageJson1 = "天狮集团信息中心"; // readTxtFile("C:/aa.txt");//"test";//
												// readTxtFile("C:/aa.txt");
			HashMap<String, String> requestParas1 = new HashMap<String, String>();
			requestParas1.put("businessName", "wjjtest1");
			requestParas1.put("syncFlag", "1");
			requestParas1.put("appKey", "1011ER9L89XJ25PB");
			try {
				requestParas1.put("messageJson", URLEncoder.encode(messageJson1, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			requestParas1.put("md5", "");
			String str1 = BrandwisdomClient.callService1(makeJson(requestParas1), url1);
			System.out.println("回值" + i + ": " + str1);
		}
	}

	/**
	 * 发送异步消息测试
	 */
	@SuppressWarnings("unused")
	private static void sendNonsyncMsgTest() {
		for (int i = 1; i < 1001; i++) {
			String url1 = "sender.mq.com.cn:8090/router";// sender.mq.com.cn:8090/router
			String messageJson1 = "test";// readTxtFile("C:/aa.txt");
			HashMap<String, String> requestParas1 = new HashMap<String, String>();
			requestParas1.put("businessName", "wjjtest1");
			requestParas1.put("syncFlag", "0");
			requestParas1.put("appKey", "1011ER9L89XJ25PB");
			// requestParas1.put("timeToLive", "172800000");
			try {
				requestParas1.put("messageJson", URLEncoder.encode(messageJson1, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			requestParas1.put("md5", "");
			String str1 = BrandwisdomClient.callService1(makeJson(requestParas1), url1);
			System.out.println("回值" + i + ": " + str1);
		}
	}

	public static String makeJson(Map<String, String> requestParas) {
		Iterator<String> itor = requestParas.keySet().iterator();
		StringBuffer sbf = new StringBuffer();
		while (itor.hasNext()) {
			String key = itor.next();
			sbf.append("&");
			sbf.append(key);
			sbf.append("=");
			sbf.append(requestParas.get(key));
		}
		return sbf.toString().replaceFirst("&", "");
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
