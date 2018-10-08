package sunlightMQ.bean;

public class QueueBean {
	private String queueID;
	private MQServerBean mqServerBean;
	private String name;
	private String queueType;
	private boolean persistentFlag;
	private String priorityID;
	private long timeToLive;
	private boolean durableFlag;
	private String acknowledTypeID;
	private String processExceptionModeID;
	private String processExceptionReportModeID;
	private String processExceptionUrl;
	private String processExceptionRequestTypeID;
	private String validFlag;

	public QueueBean() {
	}

	public String getQueueID() {
		return queueID;
	}

	public void setQueueID(String queueID) {
		this.queueID = queueID;
	}

	public MQServerBean getMqServerBean() {
		return mqServerBean;
	}

	public void setMqServerBean(MQServerBean mqServerBean) {
		this.mqServerBean = mqServerBean;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQueueType() {
		return queueType;
	}

	public void setQueueType(String queueType) {
		this.queueType = queueType;
	}

	public boolean isPersistentFlag() {
		return persistentFlag;
	}

	public void setPersistentFlag(boolean persistentFlag) {
		this.persistentFlag = persistentFlag;
	}

	public String getPriorityID() {
		return priorityID;
	}

	public void setPriorityID(String priorityID) {
		this.priorityID = priorityID;
	}

	public long getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(long timeToLive) {
		this.timeToLive = timeToLive;
	}

	public boolean isDurableFlag() {
		return durableFlag;
	}

	public void setDurableFlag(boolean durableFlag) {
		this.durableFlag = durableFlag;
	}

	public String getAcknowledTypeID() {
		return acknowledTypeID;
	}

	public void setAcknowledTypeID(String acknowledTypeID) {
		this.acknowledTypeID = acknowledTypeID;
	}

	public String getProcessExceptionModeID() {
		return processExceptionModeID;
	}

	public void setProcessExceptionModeID(String processExceptionModeID) {
		this.processExceptionModeID = processExceptionModeID;
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

	public String getProcessExceptionRequestTypeID() {
		return processExceptionRequestTypeID;
	}

	public void setProcessExceptionRequestTypeID(String processExceptionRequestTypeID) {
		this.processExceptionRequestTypeID = processExceptionRequestTypeID;
	}

	public String getValidFlag() {
		return validFlag;
	}

	public void setValidFlag(String validFlag) {
		this.validFlag = validFlag;
	}
}
