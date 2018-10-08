package sunlightMQ.tool;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import SunlightFrame.log.AppLogger;
import SunlightFrame.util.StringUtil;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class HostInfoGetter {
	public static String excute(String hostIP, String userName, String userPwd, String cmd) {
		InputStream in = null;
		BufferedReader in2 = null;
		Connection conn = new Connection(hostIP);
		Session ssh = null;

		StringBuffer sbf = new StringBuffer();
		try {
			conn.connect();
			boolean isconn = conn.authenticateWithPassword(userName, userPwd);
			if (!isconn) {
				return null;
			} else {
				ssh = conn.openSession();
				ssh.execCommand(cmd);
				in = new StreamGobbler(ssh.getStdout());
				in2 = new BufferedReader(new InputStreamReader(in));

				String str;
				while ((str = in2.readLine()) != null) {
					sbf.append(str + "\r\n");
				}
			}

		} catch (Exception e) {
			AppLogger.getInstance().errorLog("远程执行失败", e);
		} finally {
			try {
				if (in2 != null) {
					in2.close();
				}

				if (in != null) {
					in.close();
				}

				ssh.close();
				conn.close();
			} catch (Exception e2) {
			}
		}

		return sbf.toString();
	}

	public static String[] getCpuAndMemUsage(String hostIP, String userName, String userPwd) {
		double cpuUsed = 0;
		double idleUsed = 0.0;

		long memUsed = 0;
		long memTotal = 0;
		double memUsage = 0.0;

		try {
			String cmdResult = excute(hostIP, userName, userPwd, "top -b -n 1");
			if (cmdResult == null) {
				return null;
			}
			String[] lineStr = StringUtil.split(cmdResult, "\r\n");

			String str = null;
			int linecount = 0;
			for (int i = 0; i < lineStr.length; ++i) {
				linecount++;
				str = lineStr[i];
				if (linecount == 3 || linecount == 4) {
					if (linecount == 3) {
						String[] s = StringUtil.split(str, ",");
						String idlestr = s[3];
						idleUsed = Double.parseDouble(idlestr.replace("%id", ""));
						cpuUsed = 100 - idleUsed;
					}

					if (linecount == 4) {
						String[] s = StringUtil.split(str, ",");
						String memTotalstr = s[0];
						String memUsedstr = s[1];

						String memTotalstr1[] = StringUtil.split(memTotalstr, " ");
						memTotal = Long.parseLong(memTotalstr1[1].replace("k", ""));
						String memUsedstr1[] = StringUtil.split(memUsedstr, " ");
						memUsed = Long.parseLong(memUsedstr1[memUsedstr1.length - 2].replace("k", ""));

						memUsage = memUsed * 100 / memTotal;
					}
				}

				if (linecount > 4) {
					break;
				}
			}
		} catch (Exception e) {
			AppLogger.getInstance().errorLog("获取cpu以及内存出错:" + hostIP, e);
		}

		return new String[] { cpuUsed + "", memUsage + "" };
	}
}
