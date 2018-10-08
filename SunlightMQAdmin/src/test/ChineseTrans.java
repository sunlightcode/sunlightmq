package test;

import java.math.BigInteger;

public class ChineseTrans {
	public static void main(String[] args) throws Exception {
		String Mtext = "ninhao!123您好！";

		Mtext = java.net.URLEncoder.encode(Mtext, "UTF-8");
		byte ptext[] = Mtext.getBytes("UTF-8");// 将字符串转换成byte类型数组，实质是各个字符的二进制形式
		BigInteger m = new BigInteger(ptext);// 二进制串转换为一个大整数
		String mStr = m.toString();
		String z = CompressUtil.compress(mStr);
		System.out.println(z);

		System.out.println("------------------------------------");

		mStr = CompressUtil.uncompress(z);
		BigInteger n = new BigInteger(mStr);
		byte[] mt = n.toByteArray();// m为密文的BigInteger类型
		String str = (new String(mt, "UTF-8"));
		str = java.net.URLDecoder.decode(str, "UTF-8");
		System.out.println(str);

		System.out.println("************************************");

		 stringToBytesTest();
		// compressTest();
	}

	private static void stringToBytesTest() throws Exception {
		String Mtext = "ninhao!123您好！";

		Mtext = java.net.URLEncoder.encode(Mtext, "UTF-8");
		byte ptext[] = Mtext.getBytes("UTF-8");// 将字符串转换成byte类型数组，实质是各个字符的二进制形式
		System.out.println(ptext.toString());

		String str = (new String(ptext, "UTF-8"));
		str = java.net.URLDecoder.decode(str, "UTF-8");
		System.out.println(str);

	}

	private static void compressTest() throws Exception {
		String Mtext = "ninhao!123您好！";

		String z = CompressUtil.compress(Mtext);
		System.out.println(z);

		String m = CompressUtil.uncompress(z);
		System.out.println(m);
	}
}
