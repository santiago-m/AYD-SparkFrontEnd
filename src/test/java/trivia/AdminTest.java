package trivia;

import trivia.Admin;

import org.javalite.activejdbc.Base;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
/**
	* Clase Admin, extiende de User para poder logearse y por tanto tambien de Model.
*/

public class AdminTest {
	@Before
    public void before(){
        Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/sparkTest_test", "root", "root");
        System.out.println("AdminTest setup");
        Base.openTransaction();
    }

    @After
    public void after(){
        System.out.println("UserTest tearDown");
        Base.rollbackTransaction();
        Base.close();
    }

    // @Test
    // public void validateUniquenessOfUsernames(){
    //     User user = new User();
    //     user.set("username", "anakin");
    //     user.saveIt();

    //     User user2 = new User();
    //     user.set("username", "anakin");

    //     assertEquals(user2.isValid(), false);
    // }

    @Test
    public void validateUniquenessOfUsernames(){
        User user = new User();
        user.set("username", "");

        assertEquals(user.isValid(), false);
    }
}
