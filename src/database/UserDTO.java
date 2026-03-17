package database;

///
/// Lorenz Schmid
///
public class UserDTO {
    private int id;
    private String firstname;
    private String lastname;
    private String loginname;
    private int age;
    private String sex;
    private String fitnessLevel;
    private boolean isTrainer;

    public UserDTO(int id, String firstname, String lastname, String loginname, int age, String sex, String fitnessLevel, boolean 					isTrainer) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.loginname = loginname;
        this.age = age;
        this.sex = sex;

        this.fitnessLevel = fitnessLevel;
        this.isTrainer = isTrainer;
    }

	public int getId() { return id; }
	public String getFirstname() { return firstname; }
	public String getLastname() { return lastname; }
	public String getLoginname() {return loginname; }
	public int getAge() { return age; }
	public String getSex() { return sex; }
	public String getFitnessLevel() { return fitnessLevel; }
	public boolean isTrainer() { return this.isTrainer; }
	
}
