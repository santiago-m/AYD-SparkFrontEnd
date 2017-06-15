package trivia;

import org.javalite.activejdbc.Model;

/**
  * Clase User que representa un usuario en la base de datos.
  * @author Maria, Santiago Jose; Rivero, Matias.
*/
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
		if (puntaje >= 0) {
			points = puntaje;
		}
	}

	public void quitarVida(int cantPreguntas) {
		life = life*((1/cantPreguntas)*0.8);
	}

	public void recoverLife() {
		life = life + ((100-life)*0.3);
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
