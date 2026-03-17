package tools;

import java.util.Scanner;

///
/// Lorenz Schmid
///
public class ScannerManager {
	private static final Scanner scanner = new Scanner(System.in);
	
	public static Scanner getScanner() {
		return scanner;
	}
	public static void closeScanner() {
		if(scanner != null) {
			scanner.close();
		}
	}
}
