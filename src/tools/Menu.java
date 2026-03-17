package tools;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

///
/// Florian Schmid
///
public class Menu {
	private Map<String, Runnable> entries;
	private String title;
	private PrintStream o = System.out;
	private Scanner sc;
	
	public Menu() {
		this.entries = new LinkedHashMap<String, Runnable>();
		this.sc = ScannerManager.getScanner();
	}
	
	public Menu(String title) {
		this();
		this.title = title;
	}
	
	// add entry with no action
	public void addEntry(String s) { this.entries.put(s, () -> {}); }
	// add entry with ref to action
	public void addEntry(String s, Runnable action) { this.entries.put(s, action); }
	// add entry that opens a submenu
	public void addEntry(String s, Menu m) { this.entries.put(s, m::launch); }
	
	public void removeEntry(String s) { this.entries.remove(s); }
	public void clearEntries() { this.entries.clear(); }
	
	// start default menu behavior
	public void launch() {
		this.print();
		String choice = this.getInput();
		int index = -1;
		// validate input
		try {  
			index = Integer.parseInt(choice) - 1;
		} catch(NumberFormatException e){  
			o.printf("Ungültige Eingabe!%n%n");
			this.launch();
			return;
		}  
		
		// check if index is in bounds
		if (!isIndexInBounds(index)) {
			o.printf("Diese Zahl ist außerhalb der Optionen%n%n");
			this.launch();
			return;
		}
		
		// get key from map
		String[] keys = new String[entries.size()];
		keys = this.entries.keySet().toArray(keys);
		
		// run method
		this.entries.get(keys[index]).run();
	}
	
	// returns the selected key
	public String getSelection() {
		this.print();
		String choice = this.getInput();
		int index = -1;
		
		// validate input
		try {  
			index = Integer.parseInt(choice) - 1;
		} catch(NumberFormatException e){  
			o.printf("Ungültige Eingabe!%n%n");
			return this.getSelection();
		}  
		
		// check if index is in bounds
		if (!isIndexInBounds(index)) {
			o.printf("Diese Zahl ist außerhalb der Optionen%n%n");
			this.launch();
			return this.getSelection();
		}
		
		// get key from map
		String[] keys = new String[]{};
		keys = this.entries.keySet().toArray(keys);
		
		// return the selected key from the map, that the user selected
		return keys[index];
	}
	
	private void print() {
		o.printf("----- %s -----%n", title);
		
		// get all keys as List from map
		List<String> items = new ArrayList<String>();
		items.addAll(this.entries.keySet());
		
		// print each entry with bar infront and index
		for (int i = 0; i < items.size(); i++) {
			o.printf("| (%d) %s%n", i + 1, items.get(i));
		}
		
		// after this the user will write
		o.printf("--> ");
	}
	
	private String getInput() {
		return sc.nextLine();
	}
	
	// check if index is in bounds
	private boolean isIndexInBounds(int n) {
		return n >= 0 && n < this.entries.size();
	}
	
}
