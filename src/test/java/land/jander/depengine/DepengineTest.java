package land.jander.depengine;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.HashMap;

public class DepengineTest
{
    // Initial Keys
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME  = "last_name";
    private static final String ID         = "id";

    // Product Keys
    private static final String FULL_NAME  = "full_name";
    private static final String NAMED_ID   = "named_id";

    private static Map<String, String> getInitials()
    {
        Map<String, String> initials = new HashMap<>();
        initials.put( FIRST_NAME , "jon"      );
        initials.put( LAST_NAME  , "anderson" );
        initials.put( ID         , "1234"     );
        return initials;
    }


    /*
     * Generators
     */

    private static String generateFullName(Map<String, String>
                                           dependencies)
    {
        String firstName = dependencies.get(FIRST_NAME);
        String lastName = dependencies.get(LAST_NAME);
        return firstName + lastName;
    }

    private static String generateNamedId(Map<String, String>
                                          dependencies)
    {
        String id = dependencies.get(ID);
        String fullName = dependencies.get(FULL_NAME);
        return id + fullName;
    }


    /*
     * Tests
     */

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Tests the generation of products which only depend on
     * initials.
     */
    @Test
    public void dependOnInitials() throws DependencyException
    {
        Map<String, String> initials = getInitials();

        Depengine<String, String> engine
            = new Depengine<>("engine", initials);

        engine.addGenerator(
            FULL_NAME,
            DepengineTest::generateFullName,
            engine.getDependency(FIRST_NAME),
            engine.getDependency(LAST_NAME)
        );

        Map<String, String> products = engine.getProducts();

        String fullName = products.get(FULL_NAME);
        String firstName = initials.get(FIRST_NAME);
        String lastName = initials.get(LAST_NAME);

        assertEquals(fullName, firstName + lastName);
    }

    /**
     * Tests the generation of products which depend on other
     * products.
     */
    @Test
    public void dependOnProducts() throws DependencyException
    {
        Map<String, String> initials = getInitials();

        Depengine<String, String> engine
            = new Depengine<>("engine", initials);

        engine.addGenerator(
            FULL_NAME,
            DepengineTest::generateFullName,
            engine.getDependency(FIRST_NAME),
            engine.getDependency(LAST_NAME)
        );

        engine.addGenerator(
            NAMED_ID,
            DepengineTest::generateNamedId,
            engine.getDependency(FULL_NAME),
            engine.getDependency(ID)
        );

        Map<String, String> products = engine.getProducts();

        String namedId = products.get(NAMED_ID);
        String firstName = initials.get(FIRST_NAME);
        String lastName = initials.get(LAST_NAME);
        String id = initials.get(ID);

        assertEquals(namedId, id + firstName + lastName);
    }

    /**
     * Tests the generation of products which depend on products
     * found in another engine.
     */
    @Test
    public void dependOnOtherEngine() throws DependencyException
    {
        Map<String, String> initials = getInitials();

        Depengine<String, String> fullNameEngine
            = new Depengine<>("fullNameEngine", initials);

        Depengine<String, String> namedIdEngine
            = new Depengine<>("namedIdEngine");

        fullNameEngine.addGenerator(
            FULL_NAME,
            DepengineTest::generateFullName,
            fullNameEngine.getDependency(FIRST_NAME),
            fullNameEngine.getDependency(LAST_NAME)
        );

        namedIdEngine.addGenerator(
            NAMED_ID,
            DepengineTest::generateNamedId,
            fullNameEngine.getDependency(FULL_NAME),
            fullNameEngine.getDependency(ID)
        );

        Map<String, String> products = namedIdEngine.getProducts();

        String namedId = products.get(NAMED_ID);
        String firstName = initials.get(FIRST_NAME);
        String lastName = initials.get(LAST_NAME);
        String id = initials.get(ID);

        assertEquals(namedId, id + firstName + lastName);
    }

    /**
     * Test that an exception will be thrown because a dependency
     * is missing.
     */
    @Test
    public void missingDependency()
    {
        Depengine<String, String> fullNameEngine
            = new Depengine<>("fullNameEngine");

        Depengine<String, String> namedIdEngine
            = new Depengine<>("namedIdEngine");

        namedIdEngine.addGenerator(
            NAMED_ID,
            DepengineTest::generateNamedId,
            fullNameEngine.getDependency(FULL_NAME),
            fullNameEngine.getDependency(ID)
        );

        try
        {
            namedIdEngine.getProducts();

            // Getting the class's name from the class
            // object creates a compile-time dependency
            // to the class's name, meaning that if the
            // class name every changes I will be notified
            // via a build error.
            fail("Failed to throw "
                    + DependencyException
                        .class.getSimpleName());
        }
        catch(DependencyException ex)
        {
            // NOTE: We don't check the "key" of the exception
            // because either FULL_NAME or ID could have failed.
            // The dependencies are not guaranteed to be
            // resolved in a specific order.
            assertEquals(ex.source, "fullNameEngine");
            assertEquals(ex.destination, "namedIdEngine");
        }
    }
}
