package sunlightMQ;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import SunlightFrame.web.FrameKeys;

/**
 * 验证码图片生成工具
 */
public class ImageGenerator extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 验证图片的宽度
	private int width = 80;

	// 验证图片的高度
	private int height = 24;

	// 验证码字符个数
	private int codeCount = 4;

	private int x = 16;

	// 字体高度
	private int fontHeight = 18;

	private int codeY = 18;

	char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T',
			'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9' };

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 定义图像buffer
		BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffImg.createGraphics();
		// 创建一个随机数生成器类
		Random random = new Random();
		// 将图像填充为白色
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		// 创建字体，字体的大小应该根据图片的高度来定
		Font font = new Font("Fixedsys", Font.BOLD, fontHeight);

		// 设置字体
		g.setFont(font);

		// 画边框
		g.setColor(Color.black);
		g.drawRect(0, 0, width - 1, height - 1);
		// 随机产生20条干扰线，使图像中的认证码不易被其他程序探测到
		g.setColor(Color.black);
		for (int i = 0; i < 20; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int x1 = random.nextInt(12);
			int y1 = random.nextInt(12);
			g.drawLine(x, y, x + x1, y + y1);

		}
		// randomCode用于保存随机产生的验证码，以便用户登录后进行验证
		StringBuffer randomCode = new StringBuffer();

		// 随机产生codeCount数字的验证码
		for (int i = 0; i < codeCount; i++) {
			// 得到随机产生的验证码数字
			String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
			// 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同
			int red = random.nextInt(255);
			int green = random.nextInt(255);
			int blue = random.nextInt(255);

			// 用随机产生的颜色将验证码绘制到图像中
			g.setColor(new Color(red, green, blue));
			g.drawString(strRand, (int) ((i + 0.5) * x), codeY);
			// 将产生的四个随机数组合在一起。
			randomCode.append(strRand);
		}

		// 将四位数字的验证码保存到session中
		request.getSession().setAttribute(FrameKeys.RANDOM_STRING, randomCode.toString());

		// 禁止图像缓存
		response.setHeader("Paragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		response.setContentType("image/jpeg");
		// 将图像输出到servlet输出流中
		ServletOutputStream sos = response.getOutputStream();
		ImageIO.write(buffImg, "jpeg", sos);
		sos.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
