package main;

import java.sql.*;
import java.util.Scanner;
import java.util.regex.Pattern;

import database.DatabaseManager;
import tools.Helper;
import tools.ScannerManager;

import java.util.regex.Matcher;

///
/// Lorenz Schmid
///
public class AccountCreation {
	
	private static final Scanner scanner = ScannerManager.getScanner();
	
	public void startAccountCreation() {
		System.out.println("Willkommen bei Ihrem persönlichen Trainingstagebuch! Legen Sie nun Ihren neuen Account an.");
		
		String firstname = "";
		do {
			System.out.println("Bitte geben Sie Ihren Vornamen ein.");
			firstname = scanner.nextLine();
		} while (firstname.length() <= 2);
		
		
		String lastname = "";
		do {
			System.out.println("Geben Sie nun Ihren Nachnamen ein. (Min. 3 Zeichen)");
			lastname = scanner.nextLine();
		} while (lastname.length() <= 2);
		
		System.out.println("Tragen Sie schließlich noch Ihr Alter ein.");
		int age = 0;
		do {
			try {
				age = Integer.parseInt(scanner.nextLine());
			} catch (Exception e) {
				System.out.println("Dies ist kein Alter!");
			}
		} while (age < 1 || age > 100);
		
		
		insertSqlUser(firstname, lastname, age);
	}

	public void insertSqlUser(String firstname, String lastname, int age){
		int id = Helper.getRandomInt(1000, 9999);
		
		String query = "INSERT INTO userdata (firstname, lastname, age, loginname) VALUES (?,?,?,?);";
		String generatedLoginname = generateLoginname(firstname, lastname, id);

		try(Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(query)){
				
				ps.setString(1, firstname);
				ps.setString(2, lastname);
				ps.setInt(3, age);
				ps.setString(4, generatedLoginname);
				ps.executeUpdate();
				
				System.out.printf("%nIhr automatisch generierter Loginname lautet: %n%s%n", generatedLoginname);

		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		
		getPasswordFromUser(generatedLoginname);
	}
	
	public String generateLoginname(String firstname, String lastname, Integer id) {
		return 	lastname.toLowerCase().substring(0, 2) 
				+ firstname.toLowerCase().substring(0, 2) 
				+ id;
	}
	
	// TODO: hash password
	public void getPasswordFromUser(String loginname) {
		boolean passwordSet = false;
		
		while(!passwordSet) {
			System.out.println("Nun können Sie Ihr Passwort festlegen");
			System.out.println("Anforderungen: Min. 6 Zeichen; Min. 1 Groß- und min. 1 Kleinbuchstabe; Min. 1 Ziffer. \n");
			
			String input1 = scanner.nextLine();
			
			if(!isPasswordValid(input1)) {
				System.out.println();
				System.out.println("Das Passwort muss mindestens einen Großbuchstaben, einen Kleinbuchstaben und eine Ziffer enthalten und 6 Zeichen lang sein.");	
				continue;
			}
			
			System.out.println("Geben Sie das Passwort erneut ein.");
			String input2 = scanner.nextLine();
		
			if(!input1.equals(input2)) {
				System.out.println("Die beiden Passwörter stimmen nicht überein! Bitte versuchen Sie es erneut.");
				continue;
			}
			
			// set password in db
			String query = "UPDATE userdata SET password = ? WHERE loginname = ?";
			try(Connection con = DatabaseManager.getConnection();
				PreparedStatement ps = con.prepareStatement(query))
			{
					ps.setString(1, input1);
					ps.setString(2, loginname);
					ps.executeUpdate();
					
					System.out.println("Password festgelegt.");	
					// exit loop
					passwordSet = true;
			}
			catch(SQLException e ) {
				e.printStackTrace();
				System.out.println("!! Fehler bei der Verbindung mit der Datenbank.\nEs wird empfohlen das Programm neu zu starten!\n");
			}
		}
	}

	// false = invalid, true = valid
	public boolean isPasswordValid(String password) {
		String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";
		Pattern pattern = Pattern.compile(passwordPattern);
		Matcher matcher = pattern.matcher(password);
		
		if(password.length() < 6 || !matcher.matches())
			return false;
		
		return true;
	}
}
