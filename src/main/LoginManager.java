package main;

import java.sql.*;
import java.util.Scanner;

import database.DatabaseManager;
import database.UserDTO;
import tools.ScannerManager;

///
/// Lorenz Schmid
///
public class LoginManager {
	
	private static final Scanner scanner = ScannerManager.getScanner();

	public static void startLogin() {
		System.out.printf("%nGeben Sie Ihren Loginnamen ein. %n");
		String loginname = scanner.nextLine();
		System.out.printf("%nGeben Sie jetzt Ihr Passwort ein. %n");
		String password = scanner.nextLine();
		
		String query = "SELECT * FROM userdata WHERE loginname=?;";
		
		try(Connection con = DatabaseManager.getConnection();
				PreparedStatement pr = con.prepareStatement(query))
		{
			pr.setString(1, loginname);
			ResultSet rs = pr.executeQuery();
			
			// check if no rows with this loginname
			if (!rs.next()) {
				System.out.printf("Falscher Loginname oder falsches Passwort! %n");
				return;
			}
			
			// check if pw is correct
			String pwFromDb = rs.getString("password");
			if (!password.equals(pwFromDb)) {
				System.out.printf("Falscher Loginname oder falsches Passwort! %n");
				return;
			}
			
			// sucessfull login
			int id = rs.getInt("id");
            String firstname = rs.getString("firstname");
            String lastname = rs.getString("lastname");
            int age = rs.getInt("age");
            String sex = rs.getString("sex");
            String fitnessLevel = rs.getString("fitness_level");
            boolean isTrainer = rs.getBoolean("is_trainer");
            
            UserDTO u = new UserDTO(id,firstname,lastname,loginname,age,sex,fitnessLevel,isTrainer);
            WelcomePage.setUser(u);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
