package SunlightMQ.tool;

public class CatchExceptionBean {
	private String processExceptionReportModeID;
	private String processExceptionUrl;
	private String msgCenterName;
	private String msgProcessorName;
	private String messageID;
	private String msgProcessorIP;
	private String messageJson;
	private String exceptionType;
	private String exceptionAddress;

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

	public String getExceptionAddress() {
		return exceptionAddress;
	}

	public void setExceptionAddress(String exceptionAddress) {
		this.exceptionAddress = exceptionAddress;
	}

	public String getProcessExceptionReportModeID() {
		return processExceptionReportModeID;
	}

	public void setProcessExceptionReportModeID(String processExceptionReportModeID) {
		this.processExceptionReportModeID = processExceptionReportModeID;
	}

	public String getProcessExceptionUrl() {
		return processExceptionUrl;
	}

	public void setProcessExceptionUrl(String processExceptionUrl) {
		this.processExceptionUrl = processExceptionUrl;
	}

	public String getMsgCenterName() {
		return msgCenterName;
	}

	public void setMsgCenterName(String msgCenterName) {
		this.msgCenterName = msgCenterName;
	}

	public String getMsgProcessorName() {
		return msgProcessorName;
	}

	public void setMsgProcessorName(String msgProcessorName) {
		this.msgProcessorName = msgProcessorName;
	}

	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public String getMsgProcessorIP() {
		return msgProcessorIP;
	}

	public void setMsgProcessorIP(String msgProcessorIP) {
		this.msgProcessorIP = msgProcessorIP;
	}

	public String getMessageJson() {
		return messageJson;
	}

	public void setMessageJson(String messageJson) {
		this.messageJson = messageJson;
	}

	public String getExceptionContent() {
		return exceptionContent;
	}

	public void setExceptionContent(String exceptionContent) {
		this.exceptionContent = exceptionContent;
	}

	private String exceptionContent;
}
