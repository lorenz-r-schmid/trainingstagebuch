package tools;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

///
/// Florian Schmid
///
public class Table {
	public int cell_size = 15;
	
	private List<TableColumn> columns;
	private PrintStream o = System.out;
	
	public Table() {
		this.columns = new ArrayList<TableColumn>();
	}
	public Table(List<TableColumn> a) {
		this.columns = a;
	}
	
	public void printTable() {
		// if not colums: abort
		if (this.columns.size() == 0)
			return;
		
		// print titles
		printDivider();
		for (TableColumn c : this.columns) {
			o.printf("| %s ", fitText(c.getTitle()));
		}
		o.printf("|%n");
		printDivider("=");
		
		for(int i = 0; i < this.columns.get(0).getSize(); i++) {
			printRow(i);
		}
	}
	
	private void printRow(int index) {
		for (TableColumn c : this.columns) {
			o.printf("| %s ", fitText(c.getRow(index)));
		}
		o.printf("|%n");
		printDivider();
	}
	
	// prints a row divider
	private void printDivider() {
		printDivider("-");
	}
	
	private void printDivider(String symbol) {
		String cell = "+" + symbol.repeat(cell_size + 2);
		o.println(cell.repeat(this.columns.size()) + "+");
	}
	
	// makes the string s fit in a cell with MAX_CELL_SIZE size
	private String fitText(String s) {
		if (s == null) 
			return " ".repeat(cell_size);
		int len = s.length();
		
		if (len > cell_size) {
			return s.substring(0, cell_size - 2) + "..";
		}
		
		return 	" ".repeat((int)Math.ceil((cell_size - len) / 2)) 
				+ s 
				// if len is uneven: add one more space than usual
				+ " ".repeat((int)Math.ceil((cell_size - len) / 2) + (len % 2 == 0 ? 1 : 0)); 
	}
}
