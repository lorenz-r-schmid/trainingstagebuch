package database;

///
/// Lorenz Schmid
///
public class ExerciseDTO {
	private int id;
	private String title;
	private String description;
	
	public int getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public String getDescription() {
		return description;
	}
	
	public ExerciseDTO(int id, String title, String description) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
	}
}
