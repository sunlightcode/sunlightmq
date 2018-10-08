package sunlightMQ.tool;

import java.util.HashMap;

public class BrandwisdomClient {

    public static String callService(String messageJson, String url) throws Exception {
        HashMap<String, String> requestParas = new HashMap<String, String>();
        requestParas.put("messageJson", messageJson);
        return ApiInvoker.postData(requestParas, "http://" + url);
    }

    public static String callService1(String param, String url) {

        return ApiInvoker.postData2(param, "http://" + url);
    }
    
    public static String callServiceHttp(String param, String url) {

        return ApiInvoker.postData2(param, "http://" + url);
    }
    
    public static String callServiceHttps(String param, String url) {

        return ApiInvoker.postDataAndCert(param, "https://" + url);
    }
}
