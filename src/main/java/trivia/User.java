package trivia;

import org.javalite.activejdbc.Model;

public class User extends Model {
	private String username;
	private String password;
	private int points;

	public User() {
		super();
	}


	public User(String name, String pass) {
		username = name;
		password = pass;
		points = 0;
	}

	public void setUsername(String newUsername) {
		username = newUsername;
	}

	public String getUsername(){
		return username;
	}

	public String getPassword(){
		return password;
	}

	public int getPoints() {
		return points;
	}

	public void incPoints() {
		points+= 5;
	}

	public void setPoints(int puntaje) {
		points = puntaje;
	}


  	static{
    	validatePresenceOf("username").message("Please, provide your username");
  	}
}
