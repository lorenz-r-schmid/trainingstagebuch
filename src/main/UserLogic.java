package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.IntStream;

import database.DatabaseManager;
import database.ExerciseDTO;
import database.SetDTO;
import tools.Menu;
import tools.ScannerManager;
import tools.Table;
import tools.TableColumn;

///
///	Florian Schmid
///
public class UserLogic {
	private static Scanner sc = ScannerManager.getScanner();
	
	public static void printAllExercises() {
		List<ExerciseDTO> exercises = DatabaseManager.getAllExercises();
		
		List<TableColumn> cols = new ArrayList<TableColumn>();
		cols.add(new TableColumn("Name"));
		
		// wrap 10 exercises per column
		for (int i = 0; i < exercises.size(); i++) {
			if (i % 10 == 0 && i != 0) {
				cols.add(new TableColumn(""));
			}
			cols.get(i / 10).addRow(exercises.get(i).getTitle());
		}
		
		Table t = new Table(cols);
		t.cell_size = 25;
		t.printTable();
	}
	
	public static void addSet() {
		int pageCount = (int) Math.ceil(DatabaseManager.getExerciseCount() / 10) + 1;
		
		System.out.println("Wähle eine Übung: ");
		List<Menu> pages = new ArrayList<Menu>();
		
		for(int i = 0; i < pageCount; i++) {
			Menu page = new Menu("Seite " + (i + 1));
			
			if (i == 0 && pageCount >= 2) {
				page.addEntry("Seite 2");
			} 
			else if (i == pageCount - 1) {
				page.addEntry("Seite " + i);
			} 
			else {
				page.addEntry("Seite " + i);
				page.addEntry("Seite " + (i + 2));
			}
			
			pages.add(page);
		}
		
		List<ExerciseDTO> exercises = DatabaseManager.getAllExercises();
		
		// wrap 10 exercises per page
		for (int i = 0; i < exercises.size(); i++) {
			pages.get(i / 10).addEntry(exercises.get(i).getTitle());
		}
		
		String choice = "Seite 1";
		// loop while no exercise is set (a page is called)
		while (choice.startsWith("Seite")) {
			// parse the selected page (Seite >1<- this is parsed to int)
			int page = Integer.parseInt("" + choice.charAt(choice.length() - 1));
			// call the page and set the selected string to choice
			choice = pages.get(page - 1).getSelection();
		}
		
		// the choosen exercise
		ExerciseDTO exercise = DatabaseManager.getExerciseByTitle(choice);
		
		System.out.println("\nWie viele Wiederholungen hast du gemacht?");
		int reps = -1;
		while (reps <= 0) {
			try { 
				reps = sc.nextInt();
				if (reps < 0)
					System.out.println("Die Anzahl der Wiederholungen darf nicht negativ sein!");
			} 
			catch (Exception e) {
				System.out.println("Dies ist keine gültige Zahl!");
			}
		}
		
		System.out.println("\nWieviele Gewicht wurde verwendet?");
		int weight = -1;
		while (weight < 0) {
			try { 
				weight = sc.nextInt();
				if (weight < 0)
					System.out.println("Die Anzahl des Gewichts darf nicht negativ sein!");
			} 
			catch (Exception e) {
				System.out.println("Dies ist keine gültige Zahl!");
			}
		}
		
		SetDTO set = new SetDTO(reps, weight, WelcomePage.getCurrentUserId(), exercise.getId());
		DatabaseManager.insertSet(set);
	}
	
	public static void viewAllSets() {
		viewAllSets(WelcomePage.getCurrentUserId());
	}
	
	public static void viewAllSets(int userId) {
		String query = "select sets.date, sets.reps, sets.weight, exercises.title from sets INNER JOIN exercises ON sets.exerciseid=exercises.exerciseid WHERE sets.userid = ? ORDER BY date DESC LIMIT 100;";
		
		// map dates to map of exercises and reps x weight
		Map<String, Map<String, String>> data = new LinkedHashMap<String, Map<String, String>>();
		
		try (Connection con = DatabaseManager.getConnection();
				PreparedStatement ps = con.prepareStatement(query)) {
			
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				String date = rs.getString("date");
				int reps = rs.getInt("reps");
				int weight = rs.getInt("weight");
				String title = rs.getString("title");
				
				// only get the five last days
				if (data.size() >= 5 && !data.containsKey(date)) {
					break;
				}
				// if date is not in map yet add new hashmap
				if (!data.containsKey(date)) {
					data.put(date, new HashMap<String, String>());
				}
				
				data.get(date).put(title, weight > 0 ? String.format("%sx%skg", reps, weight) : reps + "x");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fehler beim Abrufen der Sets.");
			return;
		}
		
		List<String> dates = new ArrayList<String>();
		dates.addAll(data.keySet());
		
		// match exercise to table col
		Map<String, TableColumn> cols = new LinkedHashMap<String, TableColumn>();
		
		// add all exercises to cols
		for (Map<String, String> m : data.values()) {
			for (String exercise : m.keySet()) {
				if (!cols.containsKey(exercise)) {
					cols.put(exercise, new TableColumn(exercise));
				}
			}
		}
		
		List<TableColumn> allCols = new ArrayList<TableColumn>();
		allCols.addAll(cols.values());
		
		// iterate all days
		// iterate through all exercercises if exercise was done on this day add it to the table col else add empty string
		for (String date : dates) {
			for (String exercise : cols.keySet()) {
				if (data.get(date).containsKey(exercise)) {
					cols.get(exercise).addRow(data.get(date).get(exercise));
				} else {
					cols.get(exercise).addRow(null);
				}
			}
		}
		
		allCols.addFirst(new TableColumn("Datum", dates));
		new Table(allCols).printTable();
	}
}
