package tools;

import java.util.ArrayList;
import java.util.List;

///
/// Florian Schmid
///
public class TableColumn {
	private String title;
	private List<String> content;
	
	public TableColumn(String title) {
		this.title = title;
		this.content = new ArrayList<String>();
	}
	
	public TableColumn(String title, List<String> content) {
		this.title = title;
		this.content = content;
	}
	
	public String getRow(int i) {
		if (i >= this.content.size())
			return null;
		return this.content.get(i);
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public int getSize() {
		return this.content.size();
	}
	
	public void addRow(String s) {
		this.content.add(s);
	}
}
