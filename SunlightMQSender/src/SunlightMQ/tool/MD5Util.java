package SunlightMQ.tool;

import java.security.MessageDigest;

public class MD5Util {
	public static String MD5(String sourceString) throws Exception {
		if (sourceString.trim() == null) {
			return "null";
		}
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] buffer = sourceString.getBytes("UTF-8");
		byte[] digestBytes = md.digest(buffer);
		return bytes2Hex(digestBytes).toUpperCase();
	}

	private static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}
}
