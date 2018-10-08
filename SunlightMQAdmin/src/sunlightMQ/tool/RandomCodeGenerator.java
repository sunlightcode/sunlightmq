package sunlightMQ.tool;

import java.util.Random;

public class RandomCodeGenerator {
	private static char codeSequence[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q',
			'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9' };

	public static String generateCode(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < codeSequence.length && i < length; ++i) {
			sb.append(codeSequence[random.nextInt(codeSequence.length)]);
		}

		return sb.toString();
	}
}
