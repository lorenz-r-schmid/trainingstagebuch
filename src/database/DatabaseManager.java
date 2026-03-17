package database;

import java.sql.*;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import main.AccountCreation;
import tools.Helper;

public class DatabaseManager {
	//Stellt Verbindung zur Datenbank auf
	
	private static String SQL_URL;
	private static String SQL_USERNAME;
	private static String SQL_PW;
	
	static {
	    try {
	        Properties props = new Properties();
	        props.load(new FileInputStream("config.properties"));
	        SQL_URL = props.getProperty("db.url");
	        SQL_USERNAME = props.getProperty("db.username");
	        SQL_PW = props.getProperty("db.password");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	// Lorenz Schmid
	public static Connection getConnection() throws SQLException{
		return DriverManager.getConnection(SQL_URL, SQL_USERNAME, SQL_PW);
    }
	
	// Lorenz Schmid
	public static void firstSetup() {
		databaseSetup();
		
		try(Connection con = DatabaseManager.getConnection();
				Statement s = con.createStatement()){
			String query = "SELECT COUNT(id) FROM userdata;";
			ResultSet rs = s.executeQuery(query);
			rs.next();
			int result = rs.getInt(1);
			// if a user exists -> not first launch off the app
			if(result > 0) 
				return;
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		
		insertExercises();
		
		System.out.println("Danke, dass Sie sich für unseren Service entschieden haben!");
		AccountCreation newAcc = new AccountCreation();
		newAcc.startAccountCreation();
		//Keine WHERE clause weil bei FirstSetup nur dieser Nutzer dann besteht
		String query = "UPDATE userdata SET is_trainer = true;";
		try(Connection con = DatabaseManager.getConnection();
				Statement s = con.createStatement()){
			s.executeUpdate(query);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
							
	}
	
	// Lorenz Schmid
	public static void databaseSetup() {
		try(Connection con = DatabaseManager.getConnection();
				Statement s = con.createStatement()){
			String query = "CREATE TABLE IF NOT EXISTS public.userdata(\r\n"
					+ "id SERIAL PRIMARY KEY, \r\n"
					+ "firstname VARCHAR(50),\r\n"
					+ "lastname VARCHAR(50),\r\n"
					+ "loginname VARCHAR(50) UNIQUE NOT NULL,\r\n"
					+ "password VARCHAR(255),\r\n"
					+ "age INT,\r\n"
					+ "sex CHAR,\r\n"
					+ "is_trainer BOOLEAN DEFAULT FALSE,\r\n"
					+ "fitness_level VARCHAR(50)\r\n"
					+ ");\r\n"
					+ "\r\n"
					+ "CREATE TABLE IF NOT EXISTS public.exercises(\r\n"
					+ "exerciseid SERIAL PRIMARY KEY,\r\n"
					+ "title VARCHAR(50),\r\n"
					+ "description TEXT\r\n"
					+ ");\r\n"
					+ "\r\n"
					+ "CREATE TABLE IF NOT EXISTS public.sets(\r\n"
					+ "setid SERIAL PRIMARY KEY,\r\n"
					+ "date VARCHAR(10),\r\n"
					+ "reps INT,\r\n"
					+ "weight DECIMAL,\r\n"
					+ "userid INT,\r\n"
					+ "exerciseid INT,\r\n"
					+ "FOREIGN KEY (userid) REFERENCES userdata(id),\r\n"
					+ "FOREIGN KEY (exerciseid) REFERENCES exercises(exerciseid)\r\n"
					+ ");";
			s.execute(query);
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Florian Schmid
	private static void insertExercises() {
		String[] exercises = {"Abduktor", "Adduktor", "Rückenstrecker", "Bankdrücken", "Langhantel Rudern","Bizeps Curls","Cabel Cross","Cable Twist","Wadenheben","Chest Dip","Butterfly","Chin Up","Crunch","Kreuzheben","Dip / Barrenstütz","Frontheben","Hammer curls","Handstand push up","Hängendes Beinheben","Hip Thrust","Iso lateral row","Hampelmänner","Seil springen","Latzug","Seitheben","Beinstrecker","Beinbeuger","Muscle-Up","Plank/ Unterarmstütz","Klimmzüge","Liegestützen","Butterfly","Rudern","Kurzhantel Shroug","Seitlicher Unterarmstütz","Kniebeugen","Trizepsdrücken"};
		String query = "INSERT INTO exercises (title) VALUES (?);";
		
		// add all exercises to the query
		for (String s : exercises) {
			query += String.format("('%s'),", s);
		}
		query = query.substring(0, query.length() - 1) + ";";
		
		try(Connection con = DatabaseManager.getConnection();
				Statement st = con.createStatement()){
			st.executeUpdate(query);
		}catch(Exception e) {}
	}
	
	// Lorenz Schmid
	public static void insertExercisesManually(String title){
		String query = "INSERT INTO exercises(title) VALUES ?;";
		
		try(Connection con = DatabaseManager.getConnection();
			PreparedStatement ps = con.prepareStatement(query)){
				ps.setString(1, title);
				ps.executeUpdate();	
		}
		catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim Hinzufügen!");
		}
	}
	
	// Florian Schmid
	public static void deleteAllTables() {
		try(Connection con = DatabaseManager.getConnection();
				Statement s = con.createStatement()){
			String query = "DROP TABLE IF EXISTS public.sets;"
					+ "DROP TABLE IF EXISTS public.exercises;"
					+ "DROP TABLE IF EXISTS public.userdata;";
			s.execute(query);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Florian Schmid
	public static List<ExerciseDTO> getAllExercises() {
        String query = "SELECT * FROM exercises ORDER BY title ASC;";
        
        List<ExerciseDTO> exercises = new ArrayList<ExerciseDTO>();
        
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
        	
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("exerciseid");
                String title = rs.getString("title");
                String description = rs.getString("description");
                
                exercises.add(new ExerciseDTO(id, title, description));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Fehler beim Abrufen der Übungen.");
            return null;
        }
        
        return exercises;
    }
	
	// Florian Schmid
	public static ExerciseDTO getExerciseById(int id) {
		String query = "SELECT * FROM exercises WHERE exerciseid = ?;";
		
		try (Connection con = DatabaseManager.getConnection();
				PreparedStatement ps = con.prepareStatement(query)) {
			
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				int exerciseid = rs.getInt("exerciseid");
				String title = rs.getString("title");
				String description = rs.getString("description");
				
				return new ExerciseDTO(exerciseid, title, description);
			}
			
			// no exercise found
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim Abrufen der Übung.");
			return null;
		}
	}
	// Florian Schmid
	public static ExerciseDTO getExerciseByTitle(String title) {
		String query = "SELECT * FROM exercises WHERE title = ?;";
		
		try (Connection con = DatabaseManager.getConnection();
				PreparedStatement ps = con.prepareStatement(query)) {
			
			ps.setString(1, title);
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				int exerciseid = rs.getInt("exerciseid");
				String title1 = rs.getString("title");
				String description = rs.getString("description");
				
				return new ExerciseDTO(exerciseid, title1, description);
			}
			
			// no exercise found
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim Abrufen der Übung.");
			return null;
		}
	}
	
	// Florian Schmid
	public static void insertSet(SetDTO set) {
		// reject duplicate exercise from same user on same day
		String searchQuery = "SELECT * FROM sets WHERE date=? AND userid=? AND exerciseid=?;";
		try (Connection con = DatabaseManager.getConnection();
				PreparedStatement ps = con.prepareStatement(searchQuery)) { 
			ps.setString(1, set.getDate());
			ps.setInt(2, set.getUserId());
			ps.setInt(3, set.getExerciseId());
			
			ResultSet rs = ps.executeQuery();
			// there is already a entry with this exercise on this day from this user
			if (rs.next()) {
				// mby add message
				return;
			}
		} catch (Exception e) { e.printStackTrace(); }
		
		String query = "INSERT INTO sets (date, reps, weight, userid, exerciseid) VALUES (?, ?, ?, ?, ?)";
		
		try (Connection con = DatabaseManager.getConnection();
				PreparedStatement ps = con.prepareStatement(query)) {
			
			ps.setString(1, set.getDate());
			ps.setInt(2, set.getRepetitions());
			ps.setInt(3, set.getWeight());
			ps.setInt(4, set.getUserId());
			ps.setInt(5, set.getExerciseId());
			
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Florian Schmid
	public static void insertDummySets() {
		int base = Helper.getRandomInt(0, Integer.MAX_VALUE);
		for(int i = 0; i < 20; i++) {
			SetDTO s = new SetDTO(100 + i, 
					String.format("2024-10-1%d", i/4), 
					Helper.getRandomInt(10, 25),
					Helper.getRandomInt(0, 5) * 10,
					1, 
					Helper.getRandomInt(1, 5));
			insertSet(s);
		}
	}
	
	// Lorenz Schmid
	public static UserDTO getUserData(String loginname) {
        if (loginname.isBlank()) {
            String query = "SELECT * FROM userdata;";
            try (Connection con = DatabaseManager.getConnection();
                 Statement st = con.createStatement()) {
                ResultSet rs = st.executeQuery(query);
                if (rs.next()) {
                    return new UserDTO(rs.getInt("id"), 
                    		rs.getString("firstname"), 
                    		rs.getString("lastname"), 
                    		rs.getString("loginname"),
                            rs.getInt("age"), 
                            rs.getString("sex"),
                            rs.getString("fitness_level"),
                            rs.getBoolean("is_trainer"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            String query = "SELECT * FROM userdata WHERE loginname = ?";
            try (Connection con = DatabaseManager.getConnection();
                 PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, loginname);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return new UserDTO(rs.getInt("id"), 
                    		rs.getString("firstname"), 
                    		rs.getString("lastname"), 
                    		rs.getString("loginname"),
                            rs.getInt("age"), 
                            rs.getString("sex"), 
                            rs.getString("fitness_level"),
                            rs.getBoolean("is_trainer"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
	}

	// Florian Schmid
	public static int getExerciseCount() {
		String query = "SELECT COUNT(*) from exercises;";
		
		try (Connection con = DatabaseManager.getConnection();
				PreparedStatement ps = con.prepareStatement(query)) {
			
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {	
				return rs.getInt("count");
			}
			
			// no exercise found
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim Abrufen der Anzahl der Übungen.");
			return 0;
		}
	}
}