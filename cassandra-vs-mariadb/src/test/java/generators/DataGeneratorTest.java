package generators;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
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

    /**
     * Should generate 0 random names in a list
     * @throws Exception
     */
    @Test
    public void getNamesZero() throws Exception {
        DataGenerator dg = new DataGenerator(this.random);
        String[] names = dg.getNames(0);
        assertEquals(0, names.length);
    }


    /**
     * Should generate 10 random addresses in a list
     * @throws Exception
     */
    @Test
    public void getAddresses() throws Exception {
        DataGenerator dg = new DataGenerator(this.random);
        String[] addresses = dg.getAddresses(10);
        assertEquals(10, addresses.length);
    }

    /**
     * Should generate 0 random addresses in a list
     * @throws Exception
     */
    @Test
    public void getAddressesZero() throws Exception {
        DataGenerator dg = new DataGenerator(this.random);
        String[] addresses = dg.getAddresses(0);
        assertEquals(0, addresses.length);
    }

    @Test
    public void getEmail() throws Exception {
        DataGenerator dg = new DataGenerator(this.random);
        String email = dg.getEmail("bob");
        assertEquals("bob@fakemail.com", email);
    }

    @Test
    public void getHatData() throws Exception {
        DataGenerator dg = new DataGenerator(this.random);
        HashMap<String, ArrayList<String>> hatData = dg.getHatData();

        assertNotNull(hatData);

        assertTrue(hatData.containsKey("name"));
        assertNotNull(hatData.get("name"));
        assertEquals(1, hatData.get("name").size());


        assertTrue(hatData.containsKey("colours"));
        assertNotNull(hatData.get("colours"));
        assertTrue(hatData.get("colours").size() > 1);

        assertTrue(hatData.containsKey("sizes"));
        assertNotNull(hatData.get("sizes"));
        assertTrue(hatData.get("sizes").size() > 1);
    }

    @Test
    public void generatePrice() throws Exception {
        DataGenerator dg = new DataGenerator(this.random);
        double price = dg.generatePriceWeight(1, 5);
        assertTrue(price > 1);
        assertTrue(price < 5);
    }
}