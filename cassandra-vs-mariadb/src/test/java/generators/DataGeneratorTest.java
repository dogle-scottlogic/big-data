package generators;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by dogle on 01/12/2016.
 */
public class DataGeneratorTest {

    private Random random;

    @Before
    public void setUp() throws Exception {
        int seed = 1234;
        this.random = new Random(seed);
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Should generate 10 random names in a list
     * @throws Exception
     */
    @Test
    public void getNames() throws Exception {
        DataGenerator dg = new DataGenerator(this.random);
        String[] names = dg.getNames(10);
        assertEquals(10, names.length);
    }

}