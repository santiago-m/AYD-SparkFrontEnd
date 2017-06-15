package trivia;

import trivia.User;

import org.javalite.activejdbc.Base;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest{
    @Before
    public void before(){
        Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia_test", "root", "root");
        System.out.println("UserTest setup");
        Base.openTransaction();
    }

    @After
    public void after(){
        System.out.println("UserTest tearDown");
        Base.rollbackTransaction();
        Base.close();
    }

/*    @Test
    public void validateUniquenessOfUsernames(){
        User user = new User("anakin", "");
//        user.set("username", "anakin");
        user.saveIt();

        User user2 = new User("anakin", "");
//        user.set("username", "anakin");

        assertEquals(user2.isValid(), false);
    }*/

    @Test
    public void validateUniquenessOfUsernames2(){
        User user = new User("", "");
//        user.set("username", "");

        assertEquals(user.isValid(), false);
    }

    @Test
    public void nonNegativePoints() {
        User user = new User("asd", "dsa");

        user.setPoints(-1);

        assertEquals(user.getPoints()>=0 , true);
    }

    @Test
    public void morePoints() {
        User user1 = new User ("user1", "prueba");
        User user2 = new User ("user2", "prueba");

        user1.incPoints();

        assertEquals(user1.getPoints(), (user2.getPoints()+5) );
    }

    @Test
    public void morePoints2() {
        User user1 = new User ("user1", "prueba");
        User user2 = new User ("user2", "prueba");

        user1.incPoints();
        user2.setPoints(5);

        assertEquals(user1.getPoints(), user2.getPoints());

    }

}