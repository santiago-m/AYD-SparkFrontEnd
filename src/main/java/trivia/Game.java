package trivia;
import org.javalite.activejdbc.Model;
import trivia.Respondida;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
  * Clase que permite administrar una partida del juego.
  * @author Maria, Santiago; Rivero, Matias.
  * @version 0.3
*/
public class Game extends Model{

  static{
      validatePresenceOf("jugador1").message("Please, provide your username");
      validatePresenceOf("jugador2").message("Please, provide your username");
      validatePresenceOf("ganador").message("It must be a winner");
    }
	private User player1, player2;
	private int cantPreguntas;
 	private int cantJugadoresConectados;
 	private boolean activo;
 	private HashMap cantRespondidas = new HashMap();

    /**
      * Constructor basico de la clase.-
      * @author Maria, Santiago; Rivero, Matias.
    */
  public Game() {
    super();
    activo = false;
  }

  /**
    * Metodo que devuelve True si el juego esta cerrado o inactivo, y False en caso contrario.
    * @author Maria, Santiago; Rivero, Matias.
  */
  public boolean isClosed() {
    return !activo;
  }

/**
  * Metodo que activa un juego.
  * @author Maria, Santiago Jose; Rivero, Matias.
*/

  private void activateGame() {
  	activo = true;
  }

/**
  * Metodo que inicializa los datos del primer jugador del juego (Player1).
  * @author Maria, Santiago Jose; Rivero, Matias.
*/

  private void initRespondidaOnePlayer() {
    cantRespondidas.put(player1.getUsername(), 0);
  }

/**
  * Metodo que inicializa los datos del segundo jugador del juego (Player2).
  * @author Maria, Santiago Jose; Rivero, Matias.
*/

  private void initializeRespondidas() {
  	cantRespondidas.put(player1.getUsername(), 0);
  	cantRespondidas.put(player2.getUsername(), 0);
  }

  /**
    * Metodo que cierra el juego actual y devuelve un map con los datos del ganador y perdedor de la partida.
    * @author Maria, Santiago; Rivero, Matias.
  */
  public HashMap closeGame() {
    activo = false;
    App.openDB();
    
    HashMap winnerLoser = new HashMap();

    if (player1.getHP() > player2.getHP()) {
      set("ganador", player1.getInteger("id"));
      winnerLoser.put("ganador", player1.getString("username"));
      winnerLoser.put("perdedor", player2.getString("username"));
    }
    else if (player1.getHP() < player2.getHP()) {
      set("ganador", player2.getInteger("id"));
      winnerLoser.put("ganador", player2.getString("username"));
      winnerLoser.put("perdedor", player1.getString("username"));
    }
    else {
      set("ganador", -1);
      winnerLoser.put("ganador", "Empate");
      winnerLoser.put("perdedor", "Empate");
    }
    set("estado", "finalizado");
    player1 = null;
    player2 = null;
    cantJugadoresConectados = 0;
    saveIt();

    App.closeDB();
    return winnerLoser;
  }

  /**
    * Constructor de un juego Single Player.
    * @author Maria, Santiago; Rivero, Matias.
    * @param unicoUsuario Usuario que desea abrir una partida single player.
  */
  public Game (User unicoUsuario) {
    super();
    player1 = unicoUsuario;
    player1.setUsername(unicoUsuario.getString("username"));
    player1.setPoints(unicoUsuario.getInteger("puntaje"));
    player2 = null;
    cantJugadoresConectados = 1;
    activo = true;
  }

  /**
    * Constructor de un juego Multi-Player.
    * @author Maria, Santiago; Rivero, Matias.
    * @param user1 Usuario que creo el juego multi-player.
    * @param user2 Usuario que se unio al juego multi-player.
  */
  public Game (User user1, User user2) {
    super();
    player1 = user1;
    player1.setUsername(user1.getString("username"));
    player1.setPoints(user1.getInteger("puntaje"));

    player2 = user2;
    player1.setUsername(user2.getString("username"));
    player1.setPoints(user2.getInteger("puntaje"));

    cantJugadoresConectados = 2;
    activo = true;
  }

  /**
    * Metodo estatico que inicializa un juego multi-player con el primer jugador. Lo pone en cola hasta que se una el segundo jugador.
    * @author Maria, Santiago; Rivero, Matias.
    * @param game Juego que se desea inicializar.
    * @param user Usuario que busca jugar Multi-Player.
  */
  public static void initGame(Game game, User user) {
    App.openDB();
    game.set("jugador1", user.getInteger("id"));
    game.set("jugador2", -1);
    game.set("ganador", -1);
    game.set("estado", "activo");
    game.saveIt();

    game.setPlayer1(user);
    game.setPlayer2(null);
    game.setCantUsuarios(1);
    App.closeDB();
    game.initRespondidaOnePlayer();
    game.activateGame();
  }

  /**
    * Metodo estatico que inicializa un juego multi-player con ambos jugadores necesarios.- El estado del juego pasa a ser activo.
    * @author Maria, Santiago; Rivero, Matias.
    * @param game Juego que se inicializa.
    * @param user1 Jugador 1 de la partida.
    * @param user2 Jugador 2 de la partida.
  */
  public static void initGame(Game game, int cantPreguntas, User user1, User user2) {
    App.openDB();
    game.set("jugador1", user1.getInteger("id"));
    game.set("jugador2", user2.getInteger("id"));
    game.set("ganador", -1);
    game.set("estado", "activo");
    game.saveIt();

    game.setPlayer1(user1);
    game.setPlayer2(user2);
    game.setCantUsuarios(2);
    game.initializePlayers();
    game.setCantPreguntas(cantPreguntas);

    App.closeDB();
    game.initializeRespondidas();

    game.activateGame();
  }

  /**
    * Metodo que devuelve una pregunta que puede ser respondida por un usuario con sus respectivas opciones.
    * @author Maria, Santiago; Rivero, Matias.
    * @param player Jugador que debe responder la pregunta.
  */
	public HashMap obtenerPregunta(User player) {
		App.openDB();

		Map preguntas = new HashMap();
        preguntas.put("ID", -1);
        preguntas.put("pregunta", "");
      	preguntas.put("opcion 1", "");
      	preguntas.put("opcion 2", "");
      	preguntas.put("opcion 3", "");
      	preguntas.put("opcion 4", "");

    	String respuestasEnOrden[];
      	List<Question> questions = Question.where("active = 1 and creador != '"+player.getString("username")+"' and ('"+player.getInteger("id")+"', id) not in (SELECT * from respondidas) ");
      	Question pregunta;
      	int preguntaActual;
      	int cantPreguntas = questions.size();


      	if (!questions.isEmpty() && ( ((int) cantRespondidas.get(player.getUsername()) ) < cantPreguntas) ) {
          preguntaActual = App.randInt(1, cantPreguntas);
          pregunta = questions.get(preguntaActual-1);
        
        preguntas.put("ID", pregunta.getInteger("id"));

        pregunta.calcularOpciones();
        respuestasEnOrden = new String[pregunta.getCantOpciones()];
          
        int posicionRespCorrecta = App.randInt(1, pregunta.getCantOpciones());
        
        for (int i = 1; i <= pregunta.getCantOpciones(); i++) {

          if (i > posicionRespCorrecta) {
            preguntas.put("opcion "+String.valueOf(i), pregunta.getString("wrong"+String.valueOf(i-1)));
          }
          else if (i < posicionRespCorrecta) {
            preguntas.put("opcion "+String.valueOf(i), pregunta.getString("wrong"+String.valueOf(i)));
          }
          else {
            preguntas.put("opcion "+String.valueOf(i), (String) pregunta.get("respuestaCorrecta"));
          }
        }
        preguntas.put("pregunta", pregunta.get("pregunta"));
        preguntas.put("cantPreguntasDisponibles", questions.size());
        
        Respondida preguntaRespondida = new Respondida();
        preguntaRespondida.set("usuario", player.getInteger("id"));
        preguntaRespondida.set("pregunta", pregunta.getInteger("id"));
        preguntaRespondida.saveIt();

        cantRespondidas.put(player.getUsername(), ((int) cantRespondidas.get(player.getUsername()) ) + 1);

      }
      App.closeDB();
      return (HashMap) preguntas;
  }

  /**
    * Metodo que actualiza los datos del juego cada vez que un usuario responde correctamente.
    * @author Maria, Santiago; Rivero, Matias.
    * @param jugador Jugador que respondio correctamente.
    * @param vecesCorrectas Cantidad de respuestas respondidas correctamente de manera consecutiva.
  */
  public void respondioCorrectamente(User jugador, int vecesCorrectas) {
    App.openDB();
      jugador.incPoints();
      jugador.set("puntaje", jugador.getPoints());
      jugador.saveIt();
    App.closeDB();

    if(cantJugadoresConectados == 2) {
      if (jugador.equals(player1)) {
        if (vecesCorrectas % 3 == 0) {
          player1.recoverLife();
        }
        player2.quitarVida(cantPreguntas);
      }
      else {
        if (vecesCorrectas % 3 == 0) {
          player2.recoverLife();
        }
        player1.quitarVida(cantPreguntas);
      }  
    }
  }

  /**
    * Metodo que inicializa los atributos de los jugadores antes de iniciar el juego.
    * @author Maria, Santiago; Rivero, Matias.
  */
  public void initializePlayers() {
    player1.initializeToPlay();
    player2.initializeToPlay();
  }


  //Metodos get y set de los atributos

  public int getCantUsuarios() {
    return cantJugadoresConectados;
  }

  public User getPlayer1() {
    return player1;
  }

  public User getPlayer2() {
    return player2;
  }

  public void setPlayer1(User player) {
    player1 = player;
  }

  public void setPlayer2(User player) {
    player2 = player;
  }

  public void setCantUsuarios(int cantidad) {
    cantJugadoresConectados = cantidad;
  }

  private void setCantPreguntas(int cantPreguntas) {
    this.cantPreguntas = cantPreguntas;
  }

}