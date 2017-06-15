package trivia;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;
import trivia.User;
import trivia.Game;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import static spark.Spark.*;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.Random;

/**
  * Clase Principal que administra el juego y sus subprocesos.
  * @author Maria, Santiago; Rivero, Matias.
  * @version 0.5
*/

public class App
{
    private static final String SESSION_NAME = "username";
    private static ArrayList<Game> games = new ArrayList<Game>();
    private static Map hostUser = new HashMap();
    private static Map hosts = new HashMap();
    
    public static void main( String[] args )
    {
        /* 
        before((request, response) -> {
          boolean authenticated;
          // ... check if authenticated
          if (!authenticated) {
            halt(401, "You are not welcome here");
          }
        });


        after((request, response) -> {
          response.header("foo", "set by after filter");
        });
        */

      //Se selecciona la carpeta en la cual se guardaran los archivos estaticos, como css o json.
      staticFileLocation("/public");
      //se reinicia el servidor con los datos actualizados
      init();

        //HashMap con los valores del perfil del usuario de la sesion iniciada.
        Map profile = new HashMap();
        //HashMap que permite mostrar mensajes en diferentes fases del juego.
        Map mensajes = new HashMap();
        //HashMap que almacena la cantidad de preguntas respondidas de manera correcta por cada usuario iniciado.-
        Map jugadorRespuesta = new HashMap();
        //HashMap donde se almacena una pregunta con sus respectivas opciones.
        final Map preguntas = new HashMap();
        //HashMap que almacena el ganador y perdedor de una partida.
        Map winnerLoser = new HashMap();

        //Se inicializa el contador de hosts
        hosts.put("cantidadHosts", 0);

        //Funcion anonima utilizada para mostrar el menu principal de la aplicacion.
        get("/", (req, res) -> {
          String username = req.session().attribute(SESSION_NAME);
          String category = req.session().attribute("category");

          if ((username != null) && (category != null)) {
              if (category.equals("user")) {
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
          if (req.session().attribute("gameIndex") != null) {
            games.remove((int) req.session().attribute("gameIndex"));
            closeHost((String) req.session().attribute(SESSION_NAME));
            req.session().removeAttribute("gameIndex");
          }

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
          profile.put("puntaje", ((User) req.session().attribute("user")).getInteger("puntaje"));
          return new ModelAndView(profile, "./views/profile.mustache");
        }, new MustacheTemplateEngine()
        );

        //Funcion anonima utilizada para mostrar las preguntas al usuario en modo Single Player.
        get("/play", (req, res) -> {
        String templateRoute = "./views/play.mustache";
        int indexOfGame = req.session().attribute("gameIndex");

        if (games.get(indexOfGame).getCantUsuarios() == 1) {
          preguntas.put("puntajeUsuario", (( (User) req.session().attribute("user"))).getPoints() );
          preguntas.put("hp", "");
          preguntas.put("redireccion", "/play");
        }
        else if (games.get(indexOfGame).getCantUsuarios() == 2) {
          preguntas.put("puntajeUsuario", "");
          preguntas.put("hp", ((User) req.session().attribute("user")).getHP());
          preguntas.put("redireccion", "/playTwoPlayers");
        }
        else {
          res.redirect("/login");
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

        //Funcion anonima que permite a un administrador administrar las preguntas existentes en la base de datos.
        //ACTUALMENTE NO DISPONIBLE
        get("/adminQuestions", (req, res) -> {
          return new ModelAndView(new HashMap(), "./views/commingSoon.mustache");
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
          closeHost(req.session().attribute(SESSION_NAME));

          return new ModelAndView(winnerLoser, "./views/winnerAlert.mustache");
        }, new MustacheTemplateEngine()
        );

        //Funcion anonima que muestra el menu multiplayer, permitiendo crear una partida o unirse a una ya creada.
        get("/menuHost", (req, res) -> {
          return new ModelAndView(new HashMap(), "./views/menuMultiplayer.mustache");
        }, new MustacheTemplateEngine()
        );

        //Funcion anonima utilizada para recolectar los datos de la partida que el usuario desea crear.
        get("/hostLAN", (req, res) -> {
          return new ModelAndView(mensajes, "./views/crearHost.mustache");
        }, new MustacheTemplateEngine()
        );

        //Funcion anonima que muestra una tabla con las partidas creadas por otros usuarios, permitiendo al usuario conectarse a alguna.
        //Crea un script que carga la tabla del tamaño necesario para mostrar todas las partidas creadas.
        get("/listarHosts", (req, res) -> {
          String script = "";
          String crearFila = "";
          if ((int) hosts.get("cantidadHosts") > 0) {            
            script = "function tableCreate() { var body = document.getElementsByTagName('div')[0]; var tbl = document.createElement('table'); tbl.style.width = '100%'; tbl.setAttribute('border', '1'); var tbdy = document.createElement('tbody'); ";
            for (int i = 0; i < (int) hosts.get("cantidadHosts"); i++) {
              crearFila = crearFila+"var tr = document.createElement('tr'); ";
              for (int j = 0; j < 2; j++) {
                if (j == 0) {
                  crearFila = crearFila+"var td = document.createElement('td'); var form = document.createElement('form'); form.action = \"/selectHost\"; form.method = \"POST\"; var hidden = document.createElement('input'); hidden.type = \"hidden\"; hidden.name = \"hostName\"; hidden.value = \""+hosts.get("Host "+(i+1))+"\"; var btn = document.createElement('input'); btn.type = \"submit\"; btn.className = \"btn\"; btn.value = \""+hosts.get("Host "+(i+1))+"\"; form.appendChild(hidden); form.appendChild(btn); td.appendChild(form); tr.appendChild(td); ";  
                }
                else if (j == 1) {
                  crearFila = crearFila+"var td = document.createElement('td'); td.appendChild(document.createTextNode('"+((User) hostUser.get((String) hosts.get("Host "+(i+1)))).getUsername()+"')); tr.appendChild(td); tbdy.appendChild(tr); tbl.appendChild(tbdy); body.appendChild(tbl);";
                }
              }
            }
          }
          crearFila = crearFila+" } document.getElementById(\"hosts\").innerHTML = tableCreate();";
          script = script+crearFila;
          hosts.put("script", script);

          return new ModelAndView(hosts, "./views/listarHosts.mustache");
        }, new MustacheTemplateEngine()
        );

        //Funcion anonima tipo POST que inicializa un juego entre el usuario y el creador de la partida que ha elegido.
        post("/selectHost", (request, response) -> {
            String hostName = request.queryParams("hostName");

            Game newGame = new Game();

            Game.initGame(newGame, (int) hosts.get(hostName), (User) hostUser.get(hostName), (User) request.session().attribute("user"));

            games.add(newGame);
            request.session().attribute("gameIndex", games.size()-1);

            jugadorRespuesta.put( ((User) request.session().attribute("user")).getString("username"), 0);
            jugadorRespuesta.put( ((User) hostUser.get(hostName)).getString("username"), 0);
            response.redirect("/playTwoPlayers");
            return null;
        });

        //Funcion anonima tipo POST que controla los datos ingresados por el usuario que desea crear una partida multi-jugador.
        //Si los datos tienen el formato correcto, se procede a la espera de otro jugador que se conecte a la partida.
        post("/host", (request, response) -> {
          String hostName = request.queryParams("hostName");
          int cantPreguntas = 0;
          try {
            cantPreguntas = Integer.parseInt(request.queryParams("cantPreguntas"));
          } catch (NumberFormatException e) {
            System.out.println("Error en el formato del numero.");
            mensajes.put("estadoHost", "Debe ingresar una cantidad de preguntas");
            response.redirect("/hostLAN");
            return null;
          }
          

          if (!existeHost(hostName)) {


          openDB();
          List<Question> questions = Question.where("active = 1 and creador != '"+((User) request.session().attribute("user")).getUsername()+"' and ('"+((User) request.session().attribute("user")).getInteger("id")+"', id) not in (SELECT * from respondidas) ");

          if (questions.isEmpty()) {
            mensajes.put("estadoHost", "Lo siento, no tiene preguntas disponibles suficientes.");
            response.redirect("/hostLAN");
            return null;
          }
          closeDB();

            mensajes.put("estadoHost", "");            

            hostUser.put(hostName, (User) request.session().attribute("user"));
            hostUser.put(request.session().attribute(SESSION_NAME), "Host "+hosts.size());
            hosts.put("Host "+ (((int) hosts.get("cantidadHosts"))+1), hostName);
            hosts.put("cantidadHosts",( (int) hosts.get("cantidadHosts"))+1);
            hosts.put(hostName, cantPreguntas);

            response.redirect("/waiting");
          }
          else {
            mensajes.put("estadoHost", "Lo siento, el nombre de host ya existe o ya existe un host creado por este usuario.");
            response.redirect("/hostLAN");
          }
          return null;
        });

        //Funcion anonima tipo post que se ejecuta iterativamente hasta que dos jugadores estan conectados. Entonces crea un juego multiplayer
        post("/waitForPlayers", (request, response) -> {
      int actualMax = games.size()-1;
      Game aux;
          for (int i = actualMax; i >= 0; i--) {
            aux = games.get(i);
            if ((aux.getPlayer2() != null) && (aux.getPlayer1() == (User) request.session().attribute("user"))) {
          request.session().attribute("gameIndex", i);
              break;
            }
          }
          if (request.session().attribute("gameIndex") != null) {
            response.redirect("/playTwoPlayers");
          }
          else {
            response.redirect("/waiting");  
          }
          return null;
        });    


        //Funcion anonima tipo post que administra la obtencion de una pregunta que sera respondida por el usuario que corresponda en modo multiplayer.
        //Ademas controla que la respuesta dada por el usuario en la pregunta anterior sea correcta y realiza las acciones necesarias en caso que esto suceda.
        post("/playTwoPlayers", (request, response) -> {
          User usuarioActual = request.session().attribute("user");
            if (usuarioActual == null) {
              response.redirect("/login");
            }
            else {
              int indexOfGame = (int) request.session().attribute("gameIndex");
              if (games.get(indexOfGame).isClosed()) {
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
                      User actual = request.session().attribute("user");
                      jugadorRespuesta.put( actual.getString("username"), (Integer) jugadorRespuesta.get(actual.getString("username"))+1);
                      int correctasSeguidas = (Integer) jugadorRespuesta.get(((User) request.session().attribute("user")).getString("username"));
                      games.get((int) request.session().attribute("gameIndex")).respondioCorrectamente(actual, correctasSeguidas);
                    }
                }             
              }

                Map preguntaObtenida = new HashMap();
                preguntaObtenida = games.get(indexOfGame).obtenerPregunta(usuarioActual);

                if ( (preguntaObtenida.get("pregunta").equals("")) || (games.get(indexOfGame).getPlayer1().getHP() == 0) || (games.get(indexOfGame).getPlayer2().getHP() == 0)) {
                  if (preguntaObtenida.get("pregunta").equals("")) {
                    mensajes.put("cantAnswer", "Lo siento, uno de los jugadores no tiene mas preguntas disponibles para responder."); 
                  }                 

                  HashMap aux = games.get(indexOfGame).closeGame();
                  winnerLoser.put("ganador", aux.get("ganador"));
                  winnerLoser.put("perdedor", aux.get("perdedor"));

                  request.session().removeAttribute("gameIndex");

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
        });
        
        //Funcion anonima tipo POST que inicializa un juego single player y procede a redirigir al juego.
        post("/singlePlayerGame", (request, response) -> {
          Game aux = new Game();

          Game.initGame(aux, request.session().attribute("user"));

          games.add(aux);
          request.session().attribute("gameIndex", games.size()-1);

          response.redirect("/play");
          return null;
          
        });


        //Funcion anonima tipo post que administra la obtencion de preguntas que responde el usuario en modo Single Player
        post("/play", (request, response) -> {
          User usuarioActual = request.session().attribute("user");
          
            if (usuarioActual == null) {
          response.redirect("/login");
            }
            else {
              if (request.session().attribute("gameIndex") == null) {
                Game aux = new Game();
                Game.initGame(aux, (User) request.session().attribute("user"));
                games.add(aux);

                request.session().attribute("gameIndex", games.size()-1);
              }
              else if (games.get((int) request.session().attribute("gameIndex")).isClosed()) {
                games.remove(request.session().attribute("gameIndex"));
                closeHost(request.session().attribute(SESSION_NAME));
                Game aux = new Game();
                Game.initGame(aux, (User) request.session().attribute("user"));
                games.add(aux);

                request.session().attribute("gameIndex", games.size()-1);
              }
              int indexOfGame = request.session().attribute("gameIndex");
            String respuestaDada = request.queryParams("answer");
              if (respuestaDada != null) {
                openDB();
                String respuestaCorrecta = (Question.where("id = "+preguntas.get("ID"))).get(0).getString("respuestaCorrecta");
                closeDB();

                if ((respuestaDada != null) && (respuestaDada.equals(respuestaCorrecta))) {
                    games.get(indexOfGame).respondioCorrectamente(usuarioActual, 0);
                }
              }
              Map preguntaObtenida = new HashMap();
              preguntaObtenida = games.get(indexOfGame).obtenerPregunta(usuarioActual);

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

            User creador = request.session().attribute("user");
            pregunta.set("creador", creador.getString("username"));  

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

        usuario = logIn(usuario);

          if ( (sessionUsername != null) && (usuario != null) ) {

            request.session().attribute("user", usuario);

            request.session().attribute("category", (usuario instanceof Admin)?"admin":"user");

            mensajes.put("estadoLogin", "");
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
    private static User logIn(User usuario) {
      openDB();

      String userN = usuario.getUsername();
      String userP = usuario.getPassword();

      //Controlando administradores.
      List<Admin> listAdmins = Admin.where("username = '"+userN+"' and password = '"+userP+"'");

      //Controlando usuarios estandar.
      List<User> listUsers = User.where("username = '"+userN+"' and password = '"+userP+"'");

      if ((listAdmins.isEmpty()) && (listUsers.isEmpty())) {
        closeDB();
        return null;
      }
      else {
        if (listAdmins.isEmpty()) {
          usuario = listUsers.get(0);
          usuario.setPoints(usuario.getInteger("puntaje"));
            usuario.setUsername(usuario.getString("username"));
        }
        else {
          usuario = listAdmins.get(0);
          usuario.setPoints(usuario.getInteger("puntaje"));
          usuario.setUsername(usuario.getString("username"));
        }
        closeDB();
        return usuario;
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

    /**
      * Metodo estatico booleano que retorna true si el hostname pasado como parametro ya esta siendo usado por otro usuario.
      * @author Maria, Santiago; Rivero, Matias.
      * @param hostName nombre de la partida que se desea crear, la cual es controlada para que no existan dos partidas con el mismo nombre.
    */
    public static boolean existeHost(String hostName) {
      if (hostUser.get(hostName) != null) {
        return true;
      }
      else {
        return false;
      }
    }

    /**
      * Metodo estatico que cierra la partida que tiene por creador al usuario cuyo nombre es pasado como parametro.
      * @author Maria, Santiago; Rivero, Matias.
      * @param usuarioCreador nombre de usuario del creador de la partida que se desea cerrar.
    */
    public static void closeHost(String usuarioCreador) {

      if (hostUser.get(usuarioCreador) != null) {
        String hostNumber = (String) hostUser.get(usuarioCreador);
        String hostName = (String) hosts.get(hostNumber);
        hosts.remove(hostNumber);
        hosts.put("cantidadHosts", ( (int) hosts.get("cantidadHosts") ) - 1);
        hostUser.remove(usuarioCreador);
        hostUser.remove(hostName);  
      }
    }
}
