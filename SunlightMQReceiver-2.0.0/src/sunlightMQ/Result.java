package sunlightMQ;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class Result {
	private String status;
	private String message;
	private Object value;

	public Result() {
		status = "0";
		message = "";
		value = null;
	}

	public Result(String status, String message, Object value) {
		this.status = status;
		this.message = message;
		this.value = value;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String toJSON() {
		Map<String, Object> responseObj = new HashMap<String, Object>();
		responseObj.put("status", status);
		responseObj.put("message", message);

		String key = "messageJson";
		if (value != null) {
			responseObj.put(key, value);
		}

		return JSON.toJSONString(responseObj);
	}
}
