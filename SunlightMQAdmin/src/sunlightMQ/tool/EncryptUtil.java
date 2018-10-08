package sunlightMQ.tool;

import java.security.MessageDigest;

public class EncryptUtil {

	public EncryptUtil() {
	}

	public static String MD5(String s) throws Exception {
		return encrypt(s, "MD5");
	}

	public static String SHA1(String s) throws Exception {
		return encrypt(s, "SHA1");
	}

	private static String encrypt(String s, String type) throws Exception {
		if (s.trim() == null) {
			return "null";
		} else {
			MessageDigest messagedigest = MessageDigest.getInstance(type);
			byte abyte0[] = s.getBytes("utf-8");
			byte abyte1[] = messagedigest.digest(abyte0);
			return bytes2Hex(abyte1).toUpperCase();
		}
	}

	private static String bytes2Hex(byte abyte0[]) {
		String s = "";
		for (int i = 0; i < abyte0.length; i++) {
			String s1 = Integer.toHexString(abyte0[i] & 0xff);
			if (s1.length() == 1)
				s = (new StringBuilder(String.valueOf(s))).append("0").toString();
			s = (new StringBuilder(String.valueOf(s))).append(s1).toString();
		}

		return s;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(MD5("admin123"));
		System.out.println(SHA1("a"));
	}
}
