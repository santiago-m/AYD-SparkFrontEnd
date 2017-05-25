package trivia;
import org.javalite.activejdbc.Base;
import trivia.User;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.Random;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App
{
    public static Scanner in = new Scanner(System.in);

    private static final String SESSION_NAME = "username";
    private static User usuario1, usuario2;

      public static void main( String[] args )
    {
    	staticFileLocation("/public");
    	init();

      Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/sparkTest", "root", "root");

      Map profile = new HashMap();

      Map mensajes = new HashMap();

      Map preguntas = new HashMap();


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
          openDB();
          uncheckAllQuestions();
          closeDB();
          return new ModelAndView(new HashMap(), "./views/mainpage.mustache");  
        }
      }, new MustacheTemplateEngine()
      );

      get("/gameMenu", (req, res) -> {
        return new ModelAndView(mensajes, "./views/gameMenu.mustache");
      }, new MustacheTemplateEngine()
      );

      get("/login", (req, res) -> {
        return new ModelAndView(mensajes, "./views/login.mustache");
      }, new MustacheTemplateEngine()
      );

      get("/register", (req, res) -> {
        return new ModelAndView(mensajes, "./views/register.mustache");
      }, new MustacheTemplateEngine()
      );

      get("/profile", (req, res) -> {
        return new ModelAndView(profile, "./views/profile.mustache");
      }, new MustacheTemplateEngine()
      );

      get("/play", (req, res) -> {
      	String templateRoute = "./views/play.mustache";

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

      get("/adminMenu", (req, res) -> {
        return new ModelAndView(mensajes, "./views/adminMenu.mustache");
      }, new MustacheTemplateEngine()
      );

      get("/createQuestion", (req, res) -> {
        return new ModelAndView(mensajes, "./views/createQuestion.mustache");
      }, new MustacheTemplateEngine()
      );            


    post("/play", (request, response) -> {
      openDB();

      preguntas.put("opcion 1", "");
      preguntas.put("opcion 2", "");
      preguntas.put("opcion 3", "");
      preguntas.put("opcion 4", "");

      String respuestasEnOrden[];
      List<Question> questions = Question.where("active = 1 and creador != '"+usuario1.getString("username")+"'");
      Question pregunta;
      int preguntaActual;
      int cantPreguntas = questions.size();
      int cantidadRespondidas = (usuario1.getPoints()/5);

      if (cantidadRespondidas < cantPreguntas) {


        do {
          preguntaActual = randInt(1, cantPreguntas);

          questions = Question.where("id = ?", preguntaActual);
          pregunta = questions.get(0);

          System.out.println(pregunta.getString("creador"));
          System.out.println(usuario1.getString("username"));
          System.out.println(usuario1.getUser);
          System.out.println(pregunta.getString("creador").equals(usuario1.getUsername()));

        } while (pregunta.checked() || (pregunta.getString("creador").equals(usuario1.getString("username"))));
        
        pregunta.calcularOpciones();
        respuestasEnOrden = new String[pregunta.getCantOpciones()];
          
        int posicionRespCorrecta = randInt(1, pregunta.getCantOpciones());
        
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
        pregunta.check();
        cantidadRespondidas++;
        Base.close();
        response.redirect("/play");
      }
      else {
        mensajes.put("cantAnswer", "Lo siento, no tiene preguntas disponibles para responder.");
        closeDB();
        response.redirect("/");
      }
      return null;
    });

    post("/register", (request, response) -> {
      Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/sparkTest", "root", "root");

      usuario1 = new User(request.queryParams("txt_username"), request.queryParams("txt_password"));     

      if (registrar(usuario1)) {
        Base.close();
        mensajes.put("estadoRegistro", "");
        response.redirect("/");
        return null;
        } else {
          Base.close();
          mensajes.put("estadoRegistro", "El usuario ingresado ya existe, pruebe con otro.-");
          response.redirect("/register");
          return null;
      }
    });

    post("/submitQuestion", (request, response) -> {
      Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/sparkTest", "root", "root");

      Question pregunta = new Question();

      String preguntaString, correctAnswer, incorrectAnswer;

      preguntaString = request.queryParams("txt_question");
      correctAnswer = request.queryParams("txt_correct");
      incorrectAnswer = request.queryParams("txt_incorrect1");

      System.out.println(preguntaString);
      System.out.println(correctAnswer);
      System.out.println(incorrectAnswer);

      
      if ((preguntaString.equals("")) || (correctAnswer.equals("")) || (incorrectAnswer.equals(""))) {

        mensajes.put("estadoPregunta", "Las preguntas deben tener al menos una respuesta correcta y una incorrecta");
        Base.close();
        response.redirect("/createQuestion");
      }
      else {

        pregunta.set("pregunta", preguntaString);
        pregunta.set("respuestaCorrecta", correctAnswer);
        pregunta.set("wrong1", incorrectAnswer);

        System.out.println(request.queryParams("wrong1"));
        System.out.println(request.queryParams("wrong2"));

        
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

        pregunta.set("creador", usuario1.get("username"));
        pregunta.set("leido", 0);
        pregunta.set("active", 0);
        pregunta.saveIt();

        Base.close();
        mensajes.put("estadoPregunta", "");
        response.redirect("/adminMenu");
      }
      return null;
    });

    
      
    post("/login", (request, response) -> {
      openDB();

      usuario1 = new User (request.queryParams("txt_username"), request.queryParams("txt_password"));
      
      request.session().attribute(SESSION_NAME, usuario1.getUsername());

      String sessionUsername = request.session().attribute(SESSION_NAME);

      int log = logIn(usuario1);

      if ((sessionUsername != null) && (log > 0)) {


        profile.put("username", usuario1.getUsername());
        profile.put("puntaje", usuario1.getPoints());

        if (log == 2) {
          Base.close();
          request.session().attribute("category", "admin");
          response.redirect("/adminMenu");
        }
        else {
          request.session().attribute("category", "user");
          Base.close();
          mensajes.put("estadoLogin", "");
          response.redirect("/gameMenu");
        }
        return null;
      }

      else {
        Base.close();
        mensajes.put("estadoLogin", "Usuario o contraseña incorrecto.-");
        response.redirect("/login");
        return null;
      }      
    });

      post ("/logout", (request, response) -> {
        usuario1 = null;
        request.session().attribute(SESSION_NAME, null);
        mensajes.put("cantAnswer", "");
        response.redirect("/");
        return null;
      });

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

    private static boolean registrar (User usuario) {
      String userN = usuario.getUsername();

      List<User> list = User.where("username = '"+userN+"'");

      if (!list.isEmpty()) {
        return false;
      }
      else {
        usuario.set("username", userN);
        usuario.set("password", usuario.getPassword());
        usuario.set("puntaje", 0);
        usuario.saveIt();
        return true;
      }
    }

    private static int logIn(User usuario) {
      String userN = usuario.getUsername();
      String userP = usuario.getPassword();

      //Controlando administradores.
      List<Admin> listAdmins = Admin.where("username = '"+userN+"' and password = '"+userP+"'");

      //Controlando usuarios estandar.
      List<User> listUsers = User.where("username = '"+userN+"' and password = '"+userP+"'");

      if ((listAdmins.isEmpty()) && (listUsers.isEmpty())) {
        return 0;
      }
      else {
        if (listAdmins.isEmpty()) {
          usuario = listUsers.get(0);
          usuario.setPoints(usuario.getInteger("puntaje"));
          usuario.setUsername(usuario.getString("username"));

          System.out.println(usuario.getUsername());

          return 1;
        }
        else {
          usuario = listAdmins.get(0);
          usuario.setPoints(usuario.getInteger("puntaje"));
          usuario.setUsername(usuario.getString("username"));

          System.out.println(usuario.getUsername());

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
  private static int randInt(int min, int max) {

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

  public static void uncheckAllQuestions() {
    List<Question> allQuestions = Question.findAll();

    for(Question q: allQuestions) {
        q.unCheck();
    }
  }

  public static void openDB() {
    Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/sparkTest", "root", "root");
  }

  public static void closeDB() {
    Base.close();
  }
}
