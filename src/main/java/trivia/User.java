package trivia;

import org.javalite.activejdbc.Model;

public class User extends Model {
	private String username;
	private String password;
	private int points;
	private double life;

	public User() {
		super();
	}

	public User(String name, String pass) {
		super();
		username = name;
		password = pass;
		points = 0;
		life = 100;
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

	public void quitarVida() {
		life = life*(0.9);
	}

	public void initializeToPlay() {
		username = getString("username");
		points = getInteger("puntaje");
		life = 100;
	}

	public double getHP() {
		return life;
	}

  	static{
    	validatePresenceOf("username").message("Please, provide your username");
  	}
}
