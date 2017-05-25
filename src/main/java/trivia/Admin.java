package trivia;

/**
	* Clase Admin, extiende de User para poder logearse y por tanto tambien de Model.
*/

public class Admin extends User {
  static{
    validatePresenceOf("username").message("Please, provide your username");
  }
}
