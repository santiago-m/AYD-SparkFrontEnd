package trivia;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;
import trivia.User;
import trivia.Game;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.Random;
//import java.util.Scanner;

/**
  * Clase Principal que administra el juego y sus subprocesos.
  * @author Maria, Santiago; Rivero, Matias.
  * @version 0.5
*/

public class App
{
//    public static Scanner in = new Scanner(System.in);

    private static final String SESSION_NAME = "username";
    private static Map juego = new HashMap();
    
    public static void main( String[] args )
    {
        //Se inicializa la variable juego, para luego modificar sus datos segun el juego particular.
        openDB();
        Game game = new Game();
        closeDB();

        //Se inicializan los valores del HashMap que servirá para mantener los datos del juego abierto.-
        juego.put("user1", null);
        juego.put("user2", null);
        juego.put("cantIniciadas", 0);

        //Se selecciona la carpeta en la cual se guardaran los archivos estaticos, como css o json.
    	  staticFileLocation("/public");
        //se reinicia el servidor con los datos actualizados
    	  init();

        //HashMap con los valores del perfil del usuario de la sesion iniciada.
      	Map profile = new HashMap();
        //Hashmap que permite mostrar mensajes en diferentes fases del juego.
      	Map mensajes = new HashMap();
        //Hashmap que almacena la cantidad de preguntas respondidas de manera correcta por cada usuario iniciado.-
        Map jugadorRespuesta = new HashMap();
        //HashMap donde se almacena una pregunta con sus respectivas opciones.
      	final Map preguntas = new HashMap();
        //HashMap que almacena el ganador y perdedor de una partida.
        Map winnerLoser = new HashMap();

        //Funcion anonima utilizada para mostrar el menu principal de la aplicacion.
      	get("/", (req, res) -> {
        	String username = req.session().attribute(SESSION_NAME);

        	if (username != null) {
          		if (req.session().attribute("category").equals("user")) {
            		res.redirect("/gameMenu");
            		return null;
          		}
          		else {
            	
            	res.redirect("/adminMenu");
            	return null;
          		}
        	}
        	else {
          		return new ModelAndView(new HashMap(), "./views/mainpage.mustache");  
        	}
      	}, new MustacheTemplateEngine()
      );

        //Funcion anonima utilizada para mostrar el menu del jugador.
      	get("/gameMenu", (req, res) -> {
        	return new ModelAndView(mensajes, "./views/gameMenu.mustache");
	    }, new MustacheTemplateEngine()
    	);

        //Funcion anonima utilizada para mostrar el menu de logueo de la aplicacion.
      	get("/login", (req, res) -> {
        	return new ModelAndView(mensajes, "./views/login.mustache");
      	}, new MustacheTemplateEngine()
      	);

        //Funcion anonima utilizada para mostrar el menu de registro de usuario de la aplicacion.
      	get("/register", (req, res) -> {
        	return new ModelAndView(mensajes, "./views/register.mustache");
      	}, new MustacheTemplateEngine()
      	);

        //Funcion anonima utilizada para mostrar el perfil del usuario.
      	get("/profile", (req, res) -> {
          profile.put("username", req.session().attribute(SESSION_NAME));
          profile.put("puntaje", ((User) juego.get(req.session().attribute("user"))).getInteger("puntaje"));
      	  return new ModelAndView(profile, "./views/profile.mustache");
      	}, new MustacheTemplateEngine()
      	);

        //Funcion anonima utilizada para mostrar las preguntas al usuario en modo Single Player.
        get("/play", (req, res) -> {
        String templateRoute = "./views/play.mustache";

        openDB();
        preguntas.put("puntajeUsuario", (((User) juego.get(req.session().attribute("user"))).getPoints() ));
        preguntas.put("hp", ((User) juego.get(req.session().attribute("user"))).getHP());
        closeDB();

        if (jugadorRespuesta.isEmpty()) {
          preguntas.put("redireccion", "/play");
        }
        else {
          preguntas.put("redireccion", "/playTwoPlayers");
        }

      	if (preguntas.get("opcion 4").equals("")) {
        	if (preguntas.get("opcion 3").equals("")) {
        		templateRoute = "./views/1wrong.mustache";
        	}
        	else {
        		templateRoute = "./views/2wrong.mustache";
        	}
        }
        else {
        	templateRoute = "./views/3wrong.mustache";
        }

        return new ModelAndView(preguntas, templateRoute);
      }, new MustacheTemplateEngine()
      );

        //Funcion anonima utilizada para mostrar el menu de administracion al usuario con los privilegios necesarios.
        get("/adminMenu", (req, res) -> {
          return new ModelAndView(mensajes, "./views/adminMenu.mustache");
        }, new MustacheTemplateEngine()
        );

        //Funcion anonima que permite a un administrador cargar una pregunta desde una interfaz grafica.
        get("/createQuestion", (req, res) -> {
          return new ModelAndView(mensajes, "./views/createQuestion.mustache");
        }, new MustacheTemplateEngine()
        );            

        //Funcion anonima que crea un bucle hasta que se conecta un segundo jugador para crear una partida multiplayer.
        get("/waiting", (req, res) -> {
          return new ModelAndView(new HashMap(), "./views/WFP.mustache");
        }, new MustacheTemplateEngine()
        );   

        //Funcion anonima que permite a dos usuarios responder preguntas de manera simultanea.
        get("/playTwoPlayers", (request, response) -> {
          return new ModelAndView(new HashMap(), "./views/twoPlayersRedirect.mustache");
        }, new MustacheTemplateEngine()
        );

        //Funcion anonima muestra el ganador de una partida multiplayer.
        get("/informWinner", (req, res) -> {
          return new ModelAndView(winnerLoser, "./views/winnerAlert.mustache");
        }, new MustacheTemplateEngine()
        );

        //Funcion anonima tipo post que se ejecuta iterativamente hasta que dos jugadores estan conectados. Entonces crea un juego multiplayer
        post("/waitForPlayers", (request, response) -> {

          if (request.session().attribute("user") == "user2") {
            if (game.getPlayer1() == null) {
              Game.initGame(game, (User) juego.get("user2"));
            }
            else if (!(game.getPlayer1().equals((User) juego.get("user2")))) {
              Game.initGame(game, game.getPlayer1(), (User) juego.get("user2"));
            }
          }
          else if (request.session().attribute("user") == "user1") {
            if (game.getPlayer1() == null) {
              Game.initGame(game, (User) juego.get("user1"));
            }
            else if (!(game.getPlayer1().equals((User) juego.get("user1")))) {
              Game.initGame(game, game.getPlayer1(), (User) juego.get("user1"));
            } 
          }
        
          if (game.getCantUsuarios() == 2) {
            game.initializePlayers();

            jugadorRespuesta.put("user1", 0);
            jugadorRespuesta.put("user2", 0);
            response.redirect("/playTwoPlayers");
          }
          else {
            response.redirect("/waiting");
          }
          return null;
        });    

        //Funcion anonima tipo post que administra la obtencion de una pregunta que sera respondida por el usuario que corresponda en modo multiplayer.
        post("/playTwoPlayers", (request, response) -> {
          if (game.isClosed()) {
            response.redirect("/informWinner");
            return null;
          }
          else {
            String respuestaDada = request.queryParams("answer");
            if (respuestaDada != null) {
              openDB();
              String respuestaCorrecta = (Question.where("id = "+preguntas.get("ID"))).get(0).getString("respuestaCorrecta");
              closeDB();

              if ((respuestaDada != null) && (respuestaDada.equals(respuestaCorrecta))) {
                User actual = (User) juego.get(request.session().attribute("user"));
                jugadorRespuesta.put(request.session().attribute("user"), (Integer) jugadorRespuesta.get(request.session().attribute("user"))+1);
                int correctasSeguidas = (Integer) jugadorRespuesta.get(request.session().attribute("user"));
                game.respondioCorrectamente(actual, correctasSeguidas);
                if (correctasSeguidas == 5) {
                  jugadorRespuesta.put(request.session().attribute("user"), 0);
                }
              }
            }
            User usuario = (User) juego.get(request.session().attribute("user"));
            if (usuario == null) {
              response.redirect("/login");
            }
            else {
              Map preguntaObtenida = new HashMap();
              preguntaObtenida = game.obtenerPregunta(usuario);

              if (preguntaObtenida.get("pregunta").equals("")){
                mensajes.put("cantAnswer", "Lo siento, no tiene mas preguntas disponibles para responder.");

                HashMap aux = game.closeGame();
                winnerLoser.put("ganador", aux.get("ganador"));
                winnerLoser.put("perdedor", aux.get("perdedor"));
                response.redirect("/informWinner");
              }
              else {
                mensajes.put("cantAnswer", "");
                preguntas.put("pregunta", preguntaObtenida.get("pregunta"));
                preguntas.put("opcion 1", preguntaObtenida.get("opcion 1"));
                preguntas.put("opcion 2", preguntaObtenida.get("opcion 2"));
                preguntas.put("opcion 3", preguntaObtenida.get("opcion 3"));
                preguntas.put("opcion 4", preguntaObtenida.get("opcion 4"));
                preguntas.put("ID", preguntaObtenida.get("ID"));
                preguntas.put("cantPreguntasDisponibles", preguntaObtenida.get("cantPreguntasDisponibles"));

                response.redirect("/play");
              }
            }
            return null;
          }
        });
        
        //Funcion anonima tipo post que administra la obtencion de preguntas que responde el usuario en modo Single Player
        post("/play", (request, response) -> {
        
          String respuestaDada = request.queryParams("answer");
          if (respuestaDada != null) {
            openDB();
            String respuestaCorrecta = (Question.where("id = "+preguntas.get("ID"))).get(0).getString("respuestaCorrecta");
            closeDB();

            if ((respuestaDada != null) && (respuestaDada.equals(respuestaCorrecta))) {
              User actual = (User) juego.get(request.session().attribute("user"));
              game.respondioCorrectamente(actual, 0);
            }
          }

          User usuario = (User) juego.get(request.session().attribute("user"));
          
          if (usuario == null) {
    	 	   response.redirect("/login");
          }
          else {
            Game.initGame(game, usuario);
            Map preguntaObtenida = new HashMap();
            preguntaObtenida = game.obtenerPregunta(usuario);

            if (preguntaObtenida.get("pregunta").equals("")){
              mensajes.put("cantAnswer", "Lo siento, no tiene mas preguntas disponibles para responder.");
              response.redirect("/");
            }
            else {
              mensajes.put("cantAnswer", "");
              preguntas.put("pregunta", preguntaObtenida.get("pregunta"));
              preguntas.put("opcion 1", preguntaObtenida.get("opcion 1"));
              preguntas.put("opcion 2", preguntaObtenida.get("opcion 2"));
              preguntas.put("opcion 3", preguntaObtenida.get("opcion 3"));
              preguntas.put("opcion 4", preguntaObtenida.get("opcion 4"));
              preguntas.put("ID", preguntaObtenida.get("ID"));
              preguntas.put("cantPreguntasDisponibles", preguntaObtenida.get("cantPreguntasDisponibles"));

              response.redirect("/play");
            }
          }
          return null;
        });

        //Funcion anonima tipo post que obtiene los datos ingresados por el usuario e intenta registrar al usuario en la base de datos.
        post("/register", (request, response) -> {
          
          openDB();
          User usuario = new User(request.queryParams("txt_username"), request.queryParams("txt_password"));     
          closeDB();

          if (registrar(usuario)) {
            mensajes.put("estadoRegistro", "");
            response.redirect("/");
            return null;
          } else {
            mensajes.put("estadoRegistro", "El usuario ingresado ya existe, pruebe con otro.-");
            response.redirect("/register");
            return null;
          }
        });

        //Funcion anonima tipo post que obtiene los datos de la pregunta creada por el administrador e intenta guardarla en la base de datos.
        post("/submitQuestion", (request, response) -> {
          openDB();

          Question pregunta = new Question();

          String preguntaString, correctAnswer, incorrectAnswer;

          preguntaString = request.queryParams("txt_question");
          correctAnswer = request.queryParams("txt_correct");
          incorrectAnswer = request.queryParams("txt_incorrect1");

          if ((preguntaString.equals("")) || (correctAnswer.equals("")) || (incorrectAnswer.equals(""))) {

            mensajes.put("estadoPregunta", "Las preguntas deben tener al menos una respuesta correcta y una incorrecta");
            closeDB();
            response.redirect("/createQuestion");
          }
          else {

            pregunta.set("pregunta", preguntaString);
            pregunta.set("respuestaCorrecta", correctAnswer);
            pregunta.set("wrong1", incorrectAnswer);
        
            if (request.queryParams("wrong1") != null) {
              pregunta.set("wrong2", request.queryParams("txt_incorrect2"));
            } else {
              pregunta.set("wrong2", null);
            }
        
            if (request.queryParams("wrong2") != null) {
              pregunta.set("wrong3", request.queryParams("txt_incorrect3"));
            } else {
              pregunta.set("wrong3", null);
            }

            String userString = request.session().attribute("user");
            User creador = (User) juego.get(userString);
            pregunta.set("creador", creador.getString("username"));  

            //pregunta.set("leido", 0);
            pregunta.set("active", 0);
            pregunta.saveIt();

            closeDB();
            mensajes.put("estadoPregunta", "");
            response.redirect("/adminMenu");
          }
          return null;
        });    
      
        //Funcion anonima tipo post que intenta iniciar sesion con los datos ingresados por el usuario en el menu de logueo.
        post("/login", (request, response) -> {
          
          openDB();
          User usuario = new User (request.queryParams("txt_username"), request.queryParams("txt_password"));
          closeDB();
        
          request.session().attribute(SESSION_NAME, usuario.getUsername());

          String sessionUsername = request.session().attribute(SESSION_NAME);
          int log = -1;

          if ((Integer) juego.get("cantIniciadas") <= 1) {
      		  log = logIn(usuario);
          }
          else {
            response.redirect("/");
          }

          if ( (sessionUsername != null) && (log > 0)) {

            request.session().attribute("user", (String) juego.get("tempSession"));
            juego.put("tempSession", null);

            if (log == 2) {
              request.session().attribute("category", "admin");
    	      }
            else {
              request.session().attribute("category", "user");
              mensajes.put("estadoLogin", "");
            }
            response.redirect("/");
    	      return null;
          }
          else {
            mensajes.put("estadoLogin", "Usuario o contraseña incorrecto.-");
            response.redirect("/login");
            return null;
          }      
        });

        //Funcion anonima tipo post que cierra una sesion abierta.
        post ("/logout", (request, response) -> {
          String actualUser = request.session().attribute("user");

          if(actualUser.equals("user1")) {
            juego.put(actualUser, juego.get("user2"));
            juego.put("user2", null);
          }
          else {
            juego.put(actualUser, null);
          }
          request.session().attribute(SESSION_NAME, null);
          request.session().attribute("user", null);
          mensajes.put("cantAnswer", "");
          response.redirect("/");
          return null;
        });

        //Funcion anonima tipo post que permite volver al menu anterior al actual. Segun sea administrador o usuario.
        post ("/goBack", (request, response) -> {
          if (request.session().attribute("category").equals("user")) {
            response.redirect("/gameMenu");  
          }
          else {
            response.redirect("/adminMenu");   
          }
        
          return null;
        });

    }

    /**
      * Metodo que intenta registrar en un usuario en la base de datos.- Devuelve True o False segun pueda o no hacerlo respectivamente.
      * @author Maria, Santiago; Rivero, Matias.
      * @param usuario Usuario que contiene los datos necesarios para su registro ya cargados.
    */
    private static boolean registrar (User usuario) {
      openDB();
      String userN = usuario.getUsername();

      List<User> list = User.where("username = '"+userN+"'");

      if (!list.isEmpty()) {
        closeDB();
        return false;
      }
      else {
        usuario.set("username", userN);
        usuario.set("password", usuario.getPassword());
        usuario.set("puntaje", 0);
        usuario.saveIt();
        closeDB();
        return true;
      }
    }

    /**
      * Metodo que intenta loguear en un usuario ya registrado en la base de datos.- Devuelve True o False segun pueda o no hacerlo respectivamente.
      * @author Maria, Santiago; Rivero, Matias.
      * @param usuario Usuario que contiene los datos necesarios para verificar si el usuario existe o no en la base de datos.
    */
    private static int logIn(User usuario) {
      openDB();

      String userN = usuario.getUsername();
      String userP = usuario.getPassword();

      //Controlando administradores.
      List<Admin> listAdmins = Admin.where("username = '"+userN+"' and password = '"+userP+"'");

      //Controlando usuarios estandar.
      List<User> listUsers = User.where("username = '"+userN+"' and password = '"+userP+"'");

      if ((listAdmins.isEmpty()) && (listUsers.isEmpty())) {
        closeDB();
        return 0;
      }
      else {
        if (listAdmins.isEmpty()) {
          usuario = listUsers.get(0);

          if ((Integer) juego.get("cantIniciadas") == 0) {
            juego.put("user1", usuario);
            juego.put("cantIniciadas", 1);
            juego.put("tempSession", "user1");
          }
          else {
            juego.put("user2", usuario);
            juego.put("cantIniciadas", 2);
            juego.put("tempSession", "user2");  
          }
          closeDB();
          return 1;
        }
        else {
          usuario = listAdmins.get(0);
          usuario.setPoints(usuario.getInteger("puntaje"));
          usuario.setUsername(usuario.getString("username"));

          if ((Integer) juego.get("cantIniciadas") == 0) {
            juego.put("user1", usuario);
            juego.put("cantIniciadas", 1);
            juego.put("tempSession", "user1");
          }
          else {
            juego.put("user2", usuario);
            juego.put("cantIniciadas", 2);
            juego.put("tempSession", "user2");
          }
          closeDB();
          return 2;
        }
      }
    }

    /**
      * Returns a pseudo-random number between min and max, inclusive.
      * The difference between min and max can be at most
      * <code>Integer.MAX_VALUE - 1</code>.
      *
      * @param min Minimum value
      * @param max Maximum value.  Must be greater than min.
      * @return Integer between min and max, inclusive.
      * @see java.util.Random#nextInt(int)
    */
    public static int randInt(int min, int max) {

      if (min == max) {
        return min;
      }
      else {
        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
      }
    }

    /**
      * Metodo que intenta abrir una conexion a la base de datos.
      * @author Maria, Santiago; Rivero, Matias.
    */
    public static void openDB() {
      try {
        Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/sparkTest", "root", "root");  
      } catch(DBException e) {
        System.out.println("Existe una conexion abierta a la base de datos.");
      }  
    }

    /**
      * Metodo que intenta cerrar la conexion a la base de datos.
      * @author Maria, Santiago; Rivero, Matias.
    */
    public static void closeDB() {
      try {
        Base.close();
      } catch(Exception e) {
        System.out.println("No hay ninguna conexion abierta para cerrar");
      }
    }
}
