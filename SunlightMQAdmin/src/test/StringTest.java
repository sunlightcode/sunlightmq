package test;

import java.io.UnsupportedEncodingException;

public class StringTest {
	// 二进制转字符串
	public static String byte2hex(byte[] b) {
		StringBuffer sb = new StringBuffer();
		String tmp = "";
		for (int i = 0; i < b.length; i++) {
			tmp = Integer.toHexString(b[i] & 0XFF);
			if (tmp.length() == 1) {
				sb.append("0" + tmp);
			} else {
				sb.append(tmp);
			}

		}
		return sb.toString();
	}

	// 字符串转二进制
	public static byte[] hex2byte(String str) {
		if (str == null) {
			return null;
		}

		str = str.trim();
		int len = str.length();

		if (len == 0 || len % 2 == 1) {
			return null;
		}

		byte[] b = new byte[len / 2];
		try {
			for (int i = 0; i < str.length(); i += 2) {
				b[i / 2] = (byte) Integer.decode("0X" + str.substring(i, i + 2)).intValue();
			}
			return b;
		} catch (Exception e) {
			return null;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String string = "王";
		byte[] b = null;
		try {
			b = string.getBytes("gbk");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < b.length; i++) {
			System.out.print(Integer.toBinaryString(b[i] & 0xff));
		}
		String fString = new String(b);
		System.out.print(fString);

	}
}
