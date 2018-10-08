package SunlightMQ.bean;

public class MsgProcessorBean {
	private String msgProcessorID;
	private String msgCenterID;
	private QueueBean queue;
	private String name;
	private String startThreadNumber;
	private String callUrl;
	private String callRequestTypeID;
	private String callbackMethodTypeID;
	private QueueBean callbackQueue;
	private String callbackUrl;
	private String callbackeRequestTypeID;
	private boolean autoRunning;
	private String validFlag;
	private String status;

	public MsgProcessorBean() {
	}

	public String getMsgProcessorID() {
		return msgProcessorID;
	}

	public void setMsgProcessorID(String msgProcessorID) {
		this.msgProcessorID = msgProcessorID;
	}

	public String getMsgCenterID() {
		return msgCenterID;
	}

	public void setMsgCenterID(String msgCenterID) {
		this.msgCenterID = msgCenterID;
	}

	public QueueBean getQueue() {
		return queue;
	}

	public void setQueue(QueueBean queue) {
		this.queue = queue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartThreadNumber() {
		return startThreadNumber;
	}

	public void setStartThreadNumber(String startThreadNumber) {
		this.startThreadNumber = startThreadNumber;
	}

	public String getCallUrl() {
		return callUrl;
	}

	public void setCallUrl(String callUrl) {
		this.callUrl = callUrl;
	}

	public String getCallRequestTypeID() {
		return callRequestTypeID;
	}

	public void setCallRequestTypeID(String callRequestTypeID) {
		this.callRequestTypeID = callRequestTypeID;
	}

	public String getCallbackMethodTypeID() {
		return callbackMethodTypeID;
	}

	public void setCallbackMethodTypeID(String callbackMethodTypeID) {
		this.callbackMethodTypeID = callbackMethodTypeID;
	}

	public QueueBean getCallbackQueue() {
		return callbackQueue;
	}

	public void setCallbackQueue(QueueBean callbackQueue) {
		this.callbackQueue = callbackQueue;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getCallbackeRequestTypeID() {
		return callbackeRequestTypeID;
	}

	public void setCallbackeRequestTypeID(String callbackeRequestTypeID) {
		this.callbackeRequestTypeID = callbackeRequestTypeID;
	}

	public boolean isAutoRunning() {
		return autoRunning;
	}

	public void setAutoRunning(boolean autoRunning) {
		this.autoRunning = autoRunning;
	}

	public String getValidFlag() {
		return validFlag;
	}

	public void setValidFlag(String validFlag) {
		this.validFlag = validFlag;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
