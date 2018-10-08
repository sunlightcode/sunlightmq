package sunlightMQ.jms;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import SunlightFrame.log.AppLogger;
import SunlightFrame.util.StringUtil;
import sunlightMQ.processor.MsgProcessor;

public class CmdMessageListener implements MessageListener {
	Connection con;
	Session session;
	Hashtable<String, MsgProcessor> msgProcessorHash;

	public CmdMessageListener(Connection con, Session session, Hashtable<String, MsgProcessor> msgProcessorHash) {
		this.con = con;
		this.session = session;
		this.msgProcessorHash = msgProcessorHash;
	}

	public void onMessage(Message message) {
		try {
			String cmdStr = ((TextMessage) message).getText();
			String[] msgProcessorAndCmd = StringUtil.split(cmdStr, "-");
			String msgProcessorName = msgProcessorAndCmd[0];
			String cmd = msgProcessorAndCmd[1];

			MsgProcessor msgProcessor = msgProcessorHash.get(msgProcessorName);

			AppLogger.getInstance().infoLog(
					"消息中心[" + msgProcessor.getMsgCenterName() + "]消息处理器[" + msgProcessor.getName() + "]收到命令：" + cmdStr);
			if (msgProcessor != null) {
				if (cmd.equals("start")) {
					msgProcessor.start();
				} else if (cmd.equals("stop")) {
					msgProcessor.stop();
				} else if (cmd.equals("restart")) {
					msgProcessor.restart();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
