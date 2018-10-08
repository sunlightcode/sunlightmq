package sunlightMQ.tool;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CmdExcuter {
	public static void genOpenSSL(String dir, String privateKeyFileName, String privateKeyPKCS8FileName,
			String publicKeyFileName) throws Exception {
		String[] cmd = { "sh", dir + "/genrsa.sh", privateKeyFileName, privateKeyPKCS8FileName, publicKeyFileName };

		Process pro = Runtime.getRuntime().exec(cmd);

		pro.waitFor();
		InputStream in = pro.getInputStream();
		BufferedReader read = new BufferedReader(new InputStreamReader(in));
		while (read.readLine() != null) {
		}
		return;
	}
}
