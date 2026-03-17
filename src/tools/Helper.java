package tools;

import java.util.Random;

///
/// Florian Schmid
///
public class Helper {
	private static Random r = new Random();
	
	public static int getRandomInt(int from, int to) {
		return r.nextInt(from, to);
	}
}
