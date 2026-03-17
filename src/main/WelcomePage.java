package main;

import database.DatabaseManager;
import database.UserDTO;
import tools.Menu;
import tools.ScannerManager;

///
///	Beide
///
public class WelcomePage {
	// null if no one is logged in
	private static UserDTO currentUser = null; 
	
	private static Menu startMenu = null;
	private static Menu trainerMenu = null;
	private static Menu userMenu = null;
	
	public static void main(String[] args) {
		// FOR DEBUG ONLY
		// DatabaseManager.deleteAllTables();
		// DatabaseManager.insertDummySets();
		// FOR DEBUG ONLY
		
		DatabaseManager.firstSetup();
		
		// create all instances of the menus
		createMenus();
		
		while (true) {
			if (currentUser == null) {
				startMenu.launch();
				continue;
			}
			
			if (currentUser.isTrainer()) {
				trainerMenu.launch();
				continue;
			}
			
			userMenu.launch();
		}
	}
	
	public static void setUser(UserDTO u) {
		currentUser = u;
	}
	
	public static void createMenus() {
		AccountCreation newAcc = new AccountCreation();
		startMenu = new Menu("Willkommen beim Trainigstagebuch!");
		startMenu.addEntry("Einloggen", LoginManager::startLogin);
		startMenu.addEntry("Neuen Account erstellen", newAcc::startAccountCreation);
		startMenu.addEntry("Beenden", WelcomePage::endProgram);
		
		trainerMenu = new Menu("Trainer Menü");
		trainerMenu.addEntry("Gesamtübersicht", TrainerLogic::viewAllUsers);
		trainerMenu.addEntry("Detailansicht eines Sportlers", TrainerLogic::viewSingleUser);
        trainerMenu.addEntry("Sportlerdaten einsehen", TrainerLogic::viewUserData);
        trainerMenu.addEntry("Sportlerdaten ergänzen", TrainerLogic::updateUserData);
        trainerMenu.addEntry("Sportler löschen", TrainerLogic::deleteProcess);
        trainerMenu.addEntry("Übungen hinzufügen", TrainerLogic::addExercise);
        trainerMenu.addEntry("Ausloggen", WelcomePage::logout);

        userMenu = new Menu("Nutzer Menü");
		userMenu.addEntry("Set Übersicht", UserLogic::viewAllSets);
		userMenu.addEntry("Set eintragen", UserLogic::addSet);
		userMenu.addEntry("Übungen einsehen", UserLogic::printAllExercises);
		userMenu.addEntry("Ausloggen", WelcomePage::logout);
	}
	
	public static int getCurrentUserId() {
		return currentUser.getId();
	}
	
	private static void endProgram() {
		ScannerManager.closeScanner();
		System.exit(0);
	}
	
	private static void logout() {
		currentUser = null;
	}
}
