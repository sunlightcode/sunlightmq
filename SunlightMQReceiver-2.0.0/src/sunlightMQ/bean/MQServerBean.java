package sunlightMQ.bean;

public class MQServerBean {
	private String mqServerID;
	private String name;
	private String userName;
	private String password;
	private String url;
	private String validFlag;

	public MQServerBean() {
	}

	public String getMqServerID() {
		return mqServerID;
	}

	public void setMqServerID(String mqServerID) {
		this.mqServerID = mqServerID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getValidFlag() {
		return validFlag;
	}

	public void setValidFlag(String validFlag) {
		this.validFlag = validFlag;
	}
}
