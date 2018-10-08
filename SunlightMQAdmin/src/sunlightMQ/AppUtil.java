package sunlightMQ;

public class AppUtil {
	/***
	 * 获取图片地址
	 * 
	 * @param src
	 *            保存在数据库中的图片文件名
	 * @param type
	 *            图片的前缀 (如 type = 200 那么则获取的图片是 200_XXX的图片)
	 * @param dirName
	 *            图片保存的文件夹名 如 (league)
	 * @return
	 */
	public static String getImageURL(String dirName, String src, int type) {
		String image = "/images/" + "none.jpg";
		if (src != null && !src.equals("")) {
			if (type != 0) {
				image = "/uploadFile/" + dirName + "/" + type + "_" + src;
			} else {
				image = "/uploadFile/" + dirName + "/" + src;
			}
		}

		return image;
	}
}
