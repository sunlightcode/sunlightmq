package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import SunlightFrame.util.MD5Util;
import sunlightMQ.tool.BrandwisdomClient;
import sunlightMQ.tool.HttpsUtil;

public class MQTest {
    public static String MD5_KYE = "comecho519";// 消息体加密密钥

    public static void main(String[] args) throws Exception {
        // sendEchoesMsgTest();
    	// sendEchoesMsgProduct();
        sendSyncMsgLocalTest();
    }

    /**
     * 发送同步消息测试
     */
    private static void sendEchoesMsgProduct() throws Exception {
        for (int i = 1; i < 2; i++) {
        	//System.setProperty("javax.net.ssl.trustStore","F:\\echoes\\SunlightMQ\\debug\\appServer\\keystoreFile\\echoes.keystore");
        	//System.setProperty("javax.net.ssl.trustStorePassword", "comecho");         
        	//String url1 = "mq.echoesnet.com:8443/queue";// sender.mq.com.cn:8099/queue // 106.75.65.32:8080/queue //sender.mq.com.cn:8443/queue
        	String url1 = "mq.echoesnet.com:8080/queue";// 106.75.65.32:8080/queue
        	
//            readTxtFile("C:/aa.txt");
//            String json = "{\"head\":{\"reqName\":\"DishC/editDishClass\"},\"body\":{\"11\":\"1201040002\",\"56\":\"七合轩芝士肋排\",\"75\":[{\"className\":\"套餐\",\"sequence\":\"1\"}," +
//                    "{\"className\":\"单点\",\"sequence\":\"2\"},{\"className\":\"烤肉\",\"sequence\":\"3\"}," +
//                    "{\"className\":\"烤蔬菜\",\"sequence\":\"4\"}],\"98\":\"1998e605-f064-448d-a8f9-135c0d462730\"," +
//                    "\"02\":\"117702dc-d29d-4975-ad70-1460ffebf2ad\",\"05\":\"pc\"}}";
//
//            json = "{\"head\":{\"reqName\":\"DishC/editDish\"},\"body\":{\"11\":\"1201010001\"," +
//                    "\"98\":\"4f19eabc-a39d-4b34-8524-ed1ba45f4f4e\",\"02\":\"cd9c3b55-fbd7-4941-98c8-0797a7e36c6b\"," +
//                    "\"05\":\"pc\",\"dishBean\":{\"dishId\":\"12010100010004\",\"dishName\":\"烤全羊\",\"dishPrice\":\"100\"," +
//                    "\"dishClass\":\"一品料理\",\"billing\":\"0.1\",\"dishStatus\":2," +
//                    "\"dishUrls\":\"http://huisheng.ufile.ucloud.cn/147796885373581M97d.jpg\",\"dishMemo\":\"烤全羊\"}}}\n";

            // 发送红包
//            String json = "{\"head\":{\"reqName\":\"UserC/payRed\"}," +
//                    "\"body\":{\"04\":\"3acd0be86de7dcccdbf91b20f94a68cea535922d\"," +
//                    "\"05\":\"869139024107241\",\"27\":\"0\",\"34\":\"668c7c16-e376-49c8-8a92-043298c9e792\"," +
//                    "\"02\":\"35a210ea-477c-43bc-aedb-715819d3dc2f\",\"29\":\"\",\"28\":\"1.00\"," +
//                    "\"33\":\"cef5c04b-cb26-41ac-9f7c-48d3cc616fa3\"}}";

            // String json = "{\"head\":{\"reqName\":UserC/payRed},\"body\":{\"04\":\"1496aa696d9d35aa2c23b0f1ef3020df7f26f869\",\"27\":\"0\",\"28\":\"2.0\",\"29\":\"\",\"34\":\"cef5c04b-cb26-41ac-9f7c-48d3cc616fa3\"}}";

            // json = CompressUtil.compress(json);
            // json = CompressUtil.uncompress(json);
            String json = "defaultMessageJson";
            HashMap<String, String> requestParas1 = new HashMap<String, String>();
            String md5 = "";
            md5 = MD5Util.MD5(json.trim() + MD5_KYE);
            System.out.println("md5=" + md5);

            requestParas1.put("businessName", "Ios_UserC_payRed");//And_UserC_payRed
            requestParas1.put("syncFlag", "1");//同步
            requestParas1.put("appKey", "1002HGJJD5HVJ6Y3");
            requestParas1.put("md5", md5);
            requestParas1.put("messageJson", URLEncoder.encode(json.trim(), "UTF-8"));

            String str1 = BrandwisdomClient.callServiceHttp(makeJson(requestParas1), url1);
            System.out.println("Return Message " + i + ": " + str1);
        }
    }

    /**
     * 发送同步消息测试
     */
    private static void sendEchoesMsgTest() throws Exception {
        for (int i = 1; i < 2; i++) {
        	//System.setProperty("javax.net.ssl.trustStore","F:\\echoes\\SunlightMQ\\debug\\appServer\\keystoreFile\\echoes.keystore");
        	//System.setProperty("javax.net.ssl.trustStorePassword", "comecho");
        	//String url1 = "mq.echoesnet.com:8443/queue";// sender.mq.com.cn:8099/queue // 106.75.65.32:8080/queue //sender.mq.com.cn:8443/queue
        	String url1 = "106.75.87.149:8080/queue"; //106.75.87.149
        	
//            readTxtFile("C:/aa.txt");
//            String json = "{\"head\":{\"reqName\":\"DishC/editDishClass\"},\"body\":{\"11\":\"1201040002\",\"56\":\"七合轩芝士肋排\",\"75\":[{\"className\":\"套餐\",\"sequence\":\"1\"}," +
//                    "{\"className\":\"单点\",\"sequence\":\"2\"},{\"className\":\"烤肉\",\"sequence\":\"3\"}," +
//                    "{\"className\":\"烤蔬菜\",\"sequence\":\"4\"}],\"98\":\"1998e605-f064-448d-a8f9-135c0d462730\"," +
//                    "\"02\":\"117702dc-d29d-4975-ad70-1460ffebf2ad\",\"05\":\"pc\"}}";
//
//            json = "{\"head\":{\"reqName\":\"DishC/editDish\"},\"body\":{\"11\":\"1201010001\"," +
//                    "\"98\":\"4f19eabc-a39d-4b34-8524-ed1ba45f4f4e\",\"02\":\"cd9c3b55-fbd7-4941-98c8-0797a7e36c6b\"," +
//                    "\"05\":\"pc\",\"dishBean\":{\"dishId\":\"12010100010004\",\"dishName\":\"烤全羊\",\"dishPrice\":\"100\"," +
//                    "\"dishClass\":\"一品料理\",\"billing\":\"0.1\",\"dishStatus\":2," +
//                    "\"dishUrls\":\"http://huisheng.ufile.ucloud.cn/147796885373581M97d.jpg\",\"dishMemo\":\"烤全羊\"}}}\n";

            // 发送红包
//            String json = "{\"head\":{\"reqName\":\"UserC/payRed\"}," +
//                    "\"body\":{\"04\":\"3acd0be86de7dcccdbf91b20f94a68cea535922d\"," +
//                    "\"05\":\"869139024107241\",\"27\":\"0\",\"34\":\"668c7c16-e376-49c8-8a92-043298c9e792\"," +
//                    "\"02\":\"35a210ea-477c-43bc-aedb-715819d3dc2f\",\"29\":\"\",\"28\":\"1.00\"," +
//                    "\"33\":\"cef5c04b-cb26-41ac-9f7c-48d3cc616fa3\"}}";

            String json = "{\"head\":{\"reqName\":UserC/payRed},\"body\":{\"04\":\"1496aa696d9d35aa2c23b0f1ef3020df7f26f869\"," +
                    "\"27\":\"0\",\"28\":\"2.0\",\"29\":\"\",\"34\":\"cef5c04b-cb26-41ac-9f7c-48d3cc616fa3\"}}";

            // json = CompressUtil.compress(json);
            // json = CompressUtil.uncompress(json);
            
            HashMap<String, String> requestParas1 = new HashMap<String, String>();
            String md5 = "";
            md5 = MD5Util.MD5(json.trim() + MD5_KYE);
            System.out.println("md5=" + md5);

            requestParas1.put("businessName", "Test_EchoesTest1");//And_UserC_payRed
            requestParas1.put("syncFlag", "1");//同步
            requestParas1.put("appKey", "1000EG6YZFENYPQQ");
            requestParas1.put("md5", md5);
            requestParas1.put("messageJson", URLEncoder.encode(json.trim(), "UTF-8"));

            // String str1 = BrandwisdomClient.callServiceHttps(makeJson(requestParas1), url1);

            HttpsUtil.post("https://" + url1, makeJson(requestParas1), "UTF-8");

            // System.out.println("Return Message " + i + ": " + str1);
        }
    }

    
    /**
     * 发送同步消息测试
     */
    private static void sendSyncMsgLocalTest() throws Exception {
        for (int i = 1; i < 2; i++) {
            String url1 = "sender.mq.com.cn:8080/queue";// sender.mq.com.cn:8090/router // 106.75.65.32:8080/queue
//             readTxtFile("C:/aa.txt");
//             String json = "{\"head\":{\"reqName\":\"DishC/editDishClass\"},\"body\":{\"11\":\"1201040002\",\"56\":\"七合轩芝士肋排\",\"75\":[{\"className\":\"套餐\",\"sequence\":\"1\"}," +
//                    "{\"className\":\"单点\",\"sequence\":\"2\"},{\"className\":\"烤肉\",\"sequence\":\"3\"}," +
//                    "{\"className\":\"烤蔬菜\",\"sequence\":\"4\"}],\"98\":\"1998e605-f064-448d-a8f9-135c0d462730\"," +
//                    "\"02\":\"117702dc-d29d-4975-ad70-1460ffebf2ad\",\"05\":\"pc\"}}";
//
//            json = "{\"head\":{\"reqName\":\"DishC/editDish\"},\"body\":{\"11\":\"1201010001\"," +
//                    "\"98\":\"4f19eabc-a39d-4b34-8524-ed1ba45f4f4e\",\"02\":\"cd9c3b55-fbd7-4941-98c8-0797a7e36c6b\"," +
//                    "\"05\":\"pc\",\"dishBean\":{\"dishId\":\"12010100010004\",\"dishName\":\"烤全羊\",\"dishPrice\":\"100\"," +
//                    "\"dishClass\":\"一品料理\",\"billing\":\"0.1\",\"dishStatus\":2," +
//                    "\"dishUrls\":\"http://huisheng.ufile.ucloud.cn/147796885373581M97d.jpg\",\"dishMemo\":\"烤全羊\"}}}\n";

            // 接收红包
            String json = "{\"head\":{\"reqName\":\"UserC/getRed\"},\"body\":{\"33\":\"9cf5df4f-2c1e-4184-9bba-226224030d1a\"," +
                    "\"05\":\"351702074885175\",\"34\":\"668c7c16-e376-49c8-8a92-043298c9e792\"," +
                    "\"02\":\"1a7fc29d-d7b2-44e7-bfaf-5deda9e3f446\",\"29\":\"1000101478658282701\"}}";

            HashMap<String, String> requestParas1 = new HashMap<String, String>();
            String md5 = "";
            md5 = MD5Util.MD5(json.trim() + MD5_KYE);
            System.out.println("md5=" + md5);

            requestParas1.put("businessName", "UserC_getRed");
            requestParas1.put("syncFlag", "0");
            requestParas1.put("appKey", "100148SHY3FH4PPA");
            requestParas1.put("md5", md5);
            requestParas1.put("messageJson", URLEncoder.encode(json, "UTF-8"));

            String str1 = BrandwisdomClient.callServiceHttp("", url1);
            System.out.println("Return Message " + i + ": " + str1);
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
