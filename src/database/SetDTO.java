package database;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

///
/// Florian Schmid
///
public class SetDTO {
	private int setId;
	private String date;
	private int repetitions;
	private int weight;
	private int userId;
	private int exerciseId;
	
	public int getSetId() {
		return setId;
	}
	public String getDate() {
		return date;
	}
	public int getRepetitions() {
		return repetitions;
	}
	public int getWeight() {
		return weight;
	}
	public int getUserId() {
		return userId;
	}
	public int getExerciseId() {
		return exerciseId;
	}
	
	// used when inserting
	public SetDTO(int repetitions, int weight, int userId, int exerciseId) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); // Quoted "Z" to indicate UTC, no timezone offset
		df.setTimeZone(tz);

		this.date = df.format(new Date());;
		this.repetitions = repetitions;
		this.weight = weight;
		this.userId = userId;
		this.exerciseId = exerciseId;
	}
	
	// used by database manager
	public SetDTO(int setId, String date, int repetitions, int weight, int userId, int exerciseId) {
		this.setId = setId;
		this.date = date;
		this.repetitions = repetitions;
		this.weight = weight;
		this.userId = userId;
		this.exerciseId = exerciseId;
	}
}
