package test;

public class EncryptTest {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(decrypt("150%61%131%128%124%127%61%85%150%61%141%128%140%105%124%136%128%61%85%61%95%132%142%131%94%74%128%127%132%143%95%132%142%131%94%135%124%142%142%61%152%71%61%125%138%127%148%61%85%150%61%76%76%61%85%61%76%77%75%76%75%79%75%75%75%77%61%71%61%80%81%61%85%61%-1%-45%-98%0%-85%-93%3%-40%-60%3%-91%-72%0%-66%-58%3%-99%-90%1%-87%-83%61%71%61%82%80%61%85%118%150%61%126%135%124%142%142%105%124%136%128%61%85%61%0%-64%-78%4%-65%-85%61%71%61%142%128%140%144%128%137%126%128%61%85%61%76%61%152%71%150%61%126%135%124%142%142%105%124%136%128%61%85%61%0%-88%-80%2%-99%-44%61%71%61%142%128%140%144%128%137%126%128%61%85%61%77%61%152%71%150%61%126%135%124%142%142%105%124%136%128%61%85%61%2%-98%-65%3%-99%-92%61%71%61%142%128%140%144%128%137%126%128%61%85%61%78%61%152%71%150%61%126%135%124%142%142%105%124%136%128%61%85%61%2%-98%-65%3%-81%-57%3%-86%-73%61%71%61%142%128%140%144%128%137%126%128%61%85%61%79%61%152%120%71%61%84%83%61%85%61%76%84%84%83%128%81%75%80%72%129%75%81%79%72%79%79%83%127%72%124%83%129%84%72%76%78%80%126%75%127%79%81%77%82%78%75%61%71%61%75%77%61%85%61%76%76%82%82%75%77%127%126%72%127%77%84%127%72%79%84%82%80%72%124%127%82%75%72%76%79%81%75%129%129%128%125%129%77%124%127%61%71%61%75%80%61%85%61%139%126%61%152%152%"));

		System.out.println(encrypt("{\"head\":{\"reqName\":\"DishC/editDishClass\"},\"body\":{\"11\":\"1201040002\",\"56\":\"七合轩芝士肋排\",\"75\":[{\"className\":\"套餐\",\"sequence\":\"1\"}," +
                    "{\"className\":\"单点\",\"sequence\":\"2\"},{\"className\":\"烤肉\",\"sequence\":\"3\"}," +
                    "{\"className\":\"烤蔬菜\",\"sequence\":\"4\"}],\"98\":\"1998e605-f064-448d-a8f9-135c0d462730\"," +
                    "\"02\":\"117702dc-d29d-4975-ad70-1460ffebf2ad\",\"05\":\"pc\"}}"));
	}

	/**
	 * 用户名解密
	 * 
	 * @param ssoToken
	 *            字符串
	 * @return String 返回加密字符串
	 */
	public static String decrypt(String ssoToken) {
		try {
			String name = new String();
			java.util.StringTokenizer st = new java.util.StringTokenizer(ssoToken, "%");
			while (st.hasMoreElements()) {
				int asc = Integer.parseInt((String) st.nextElement()) - 27;
				name = name + (char) asc;
			}

			return name;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 用户名加密
	 * 
	 * @param ssoToken
	 *            字符串
	 * @return String 返回加密字符串
	 */
	public static String encrypt(String ssoToken) {
		try {
			byte[] _ssoToken = ssoToken.getBytes("UTF-8");//ISO-8859-1
			String name = new String();
			// char[] _ssoToken = ssoToken.toCharArray();
			for (int i = 0; i < _ssoToken.length; i++) {
				int asc = _ssoToken[i];
				_ssoToken[i] = (byte) (asc + 27);
				name = name + (asc + 27) + "%";
			}
			return name;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
