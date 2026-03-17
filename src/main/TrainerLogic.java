package main;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import database.DatabaseManager;
import database.UserDTO;
import tools.Menu;
import tools.ScannerManager;
import tools.Table;
import tools.TableColumn;

import java.sql.*;


public class TrainerLogic {
    private static final Scanner scanner = ScannerManager.getScanner();

    // Lorenz Schmid
    public static void viewUserData() {
        String query = "SELECT loginname FROM userdata WHERE is_trainer = false;";
        
        try (Connection con = DatabaseManager.getConnection();
             Statement stmt = con.createStatement();            
             ResultSet rs = stmt.executeQuery(query)) {

            List<UserDTO> users = new ArrayList<>();
            
            while (rs.next()) {
                String loginname = rs.getString("loginname");
                UserDTO user = DatabaseManager.getUserData(loginname);  // Methode zum Abrufen von UserDTO
                if (user != null) {
                    users.add(user);
                }
            }

            List<TableColumn> columns = new ArrayList<>();
            columns.add(new TableColumn("ID", extractColumnData(users, "getId")));
            columns.add(new TableColumn("Vorname", extractColumnData(users, "getFirstname")));
            columns.add(new TableColumn("Nachname", extractColumnData(users, "getLastname")));
            columns.add(new TableColumn("Loginname", extractColumnData(users, "getLoginname")));
            columns.add(new TableColumn("Alter", extractColumnData(users, "getAge")));
            columns.add(new TableColumn("Geschlecht", extractColumnData(users, "getSex")));
            columns.add(new TableColumn("Plan vorhanden", extractColumnData(users, "isHasPlan")));
            columns.add(new TableColumn("Fitness-Level", extractColumnData(users, "getFitnessLevel")));

            Table userDataTable = new Table(columns);
            userDataTable.printTable();
            
            System.out.println("\nDrücken Sie die Eingabetaste, um ins Trainer-Menü zurückzukehren...");
            scanner.nextLine();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Lorenz Schmid
    private static List<String> extractColumnData(List<UserDTO> users, String methodName) {
        List<String> columnData = new ArrayList<>();
        
        try {
            for (UserDTO user : users) {
                Object value = UserDTO.class.getMethod(methodName).invoke(user);
                columnData.add(value != null ? value.toString() : "-");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return columnData;
    }

    // Lorenz Schmid
    public static void updateUserData() {
    	
        System.out.println("Geben Sie den Loginnamen des Sportlers ein, dessen Daten Sie ergänzen möchten:");
        String loginname = scanner.nextLine();
        UserDTO user = DatabaseManager.getUserData(loginname);

        if (user != null) {
        	  System.out.println("Geben Sie das biologische Geschlecht des Sportlers an:");
              String sex = scanner.nextLine();
              System.out.println("Ihr/sein aktuelles Fitness-Level:");
              String fitnessLevel = scanner.nextLine();

              String query = "UPDATE userdata SET sex = ?, has_plan = ?, fitness_level = ? WHERE loginname = ?";
              try (Connection con = DatabaseManager.getConnection();
                   PreparedStatement ps = con.prepareStatement(query)) {
                  ps.setString(1, sex);
                  ps.setBoolean(2, true);
                  ps.setString(3, fitnessLevel);
                  ps.setString(4, loginname);
                  ps.execute();
                  System.out.println("Daten erfolgreich aktualisiert.");
                  
              } catch (SQLException e) {
                  System.out.println("Fehler beim Aktualisieren der Daten.");
                  e.printStackTrace();
              }
        } else {
            System.out.println("Sportler nicht gefunden.");
        }
    }

    // Lorenz Schmid
    public static void deleteProcess() {
        System.out.println("Geben Sie den Loginnamen des Sportlers ein, den Sie löschen möchten:");
        String loginname = scanner.nextLine();
        UserDTO user = DatabaseManager.getUserData(loginname);
        
        if (user != null) {
            Menu deleteMenu = new Menu("Wählen Sie");
        	deleteMenu.addEntry("Löschen", () -> deleteUser(loginname));
        	deleteMenu.addEntry("Sportler ändern", TrainerLogic::deleteProcess);
        	deleteMenu.addEntry("Zurück zum Hauptmenü", () -> WelcomePage.main(null));
        	deleteMenu.launch();
        } else {
            Menu returnMenu = new Menu("Kein Sportler mit diesem Loginname gefunden");
        	returnMenu.addEntry("Anderen Loginnamen eingeben", TrainerLogic::deleteProcess);
        	returnMenu.addEntry("Zurück ins Hauptmenu", () -> WelcomePage.main(null));
        	returnMenu.launch();
        }   
    }
    
    // Lorenz Schmid
    public static void deleteUser(String loginname) {
        String query = "DELETE FROM userdata WHERE loginname = ? ;";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, loginname);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Sportler erfolgreich gelöscht.");
            } else {
                System.out.println("Fehler beim Löschen des Sportlers.\n");
            }
        } catch (SQLException e) {
            System.out.println("Fehler beim Löschen des Sportlers.");
            e.printStackTrace();
        }
    }

    // Florian Schmid
    public static void viewAllUsers() {
    	String query = "SELECT COUNT(*) as setcount, sets.date, (userdata.firstname || ' ' || userdata.lastname) as fullname from sets INNER JOIN userdata on sets.userid = userdata.id GROUP BY sets.date, fullname ORDER BY sets.date DESC;";
    	
    	// map dates to map of usernames and count of reps
		Map<String, Map<String, String>> data = new LinkedHashMap<String, Map<String, String>>();
		
		try (Connection con = DatabaseManager.getConnection();
				PreparedStatement ps = con.prepareStatement(query)) {
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				String date = rs.getString("date");
				int setcount = rs.getInt("setcount");
				String fullname = rs.getString("fullname");
				
				// only get the last five days
				if (data.size() >= 5 && !data.containsKey(date)) {
					break;
				}
				// if date is not in map yet add new hashmap
				if (!data.containsKey(date)) {
					data.put(date, new HashMap<String, String>());
				}
				
				// 1 Set or x Sets
				String setdisplay = setcount > 1 ? setcount + " Sets" : setcount + " Set";
				
				// get map of the date and add the string
				data.get(date).put(fullname, setdisplay);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim Abrufen der Sets.");
			return;
		}
		
		List<String> dates = new ArrayList<String>();
		dates.addAll(data.keySet());
		
		// match fullname of user to table col
		Map<String, TableColumn> cols = new LinkedHashMap<String, TableColumn>();
		
		// add all names to cols
		for (Map<String, String> m : data.values()) {
			for (String name : m.keySet()) {
				if (!cols.containsKey(name)) {
					cols.put(name, new TableColumn(name));
				}
			}
		}
		
		List<TableColumn> allCols = new ArrayList<TableColumn>();
		allCols.addAll(cols.values());
		
		// iterate all days
		// iterate through all names if user has done a set on that date add it to the table col else add empty string
		for (String date : dates) {
			for (String name : cols.keySet()) {
				if (data.get(date).containsKey(name)) {
					cols.get(name).addRow(data.get(date).get(name));
				} else {
					cols.get(name).addRow(null);
				}
			}
		}
		
		allCols.addFirst(new TableColumn("Datum", dates));
		new Table(allCols).printTable();
		
		System.out.println("\nDrücken Sie die Eingabetaste, um ins Trainer-Menü zurückzukehren...");
        scanner.nextLine();
    }
    
    // Florian Schmid
    public static void viewSingleUser() {
    	System.out.println("\nGeben Sie den Loginname des Nutzers ein, dessen Ansicht Sie ansehen möchten:");
        String loginname = scanner.nextLine();
        
        UserDTO u = DatabaseManager.getUserData(loginname);
        if (u == null) {
        	System.out.println("Kein Nutzer mit diesem Loginname gefunden!");
        	System.out.println("\nDrücken Sie die Eingabetaste, um ins Trainer-Menü zurückzukehren...");
            scanner.nextLine();
        	return;
        }
        
        UserLogic.viewAllSets(u.getId());
        
        System.out.println("\nDrücken Sie die Eingabetaste, um ins Trainer-Menü zurückzukehren...");
        scanner.nextLine();
    }
    
    // Lorenz Schmid
    public static void addExercise(){
	    System.out.println("Geben Sie den Namen der Übung ein:");
	    String input = scanner.nextLine();
        if (DatabaseManager.getExerciseByTitle(input) == null) {
        	DatabaseManager.insertExercisesManually(input);
        	System.out.printf("%nÜbung '%s' wurde erfolgreich hinzugefügt.%n%n", input);  
        }
        else {
        	System.out.println("Übung existiert bereits!\n");
        }
    }
}
