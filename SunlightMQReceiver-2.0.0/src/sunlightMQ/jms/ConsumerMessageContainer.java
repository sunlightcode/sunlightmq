package sunlightMQ.jms;

import javax.jms.Message;

public class ConsumerMessageContainer {
	private Message[] msgArr = null;
	private Integer msgSize = null;
	private Integer msgCap = null;

	public ConsumerMessageContainer(Integer msgcap) {
		msgCap = msgcap;
		msgArr = new Message[msgCap];
		msgSize = 0;
	}

	public Boolean addMessage(Message msg) {
		if (msgSize < msgCap) {
			msgArr[msgSize] = msg;
			msgSize = msgSize + 1;
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public int getMsgSize() {
		return msgSize;
	}

	public Message getMessage(Integer msgId) {
		if (null != msgArr[msgId]) {
			msgSize = msgSize - 1;
			Message msg = msgArr[msgId];
			msgArr[msgId] = null;
			return msg;
		} else {
			return null;
		}
	}
}
