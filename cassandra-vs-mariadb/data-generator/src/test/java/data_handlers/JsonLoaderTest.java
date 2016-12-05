package data_handlers;

import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dogle on 01/12/2016.
 */
public class JsonLoaderTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Test of loadJson method, of class JsonLoader.
     */
    @Test
    public void testLoadJson_Success() {
        JsonLoader jl = new JsonLoader();
        JSONObject file = jl.loadJson();
        assertNotNull(file);
    }

    /**
     * Test of loadJson method, of class JsonLoader.
     */
    @Test
    public void testLoadJson_Fail() {
        JsonLoader jl = new JsonLoader("not_a_file");
        JSONObject file = jl.loadJson();
        assertNull(file);
    }

    /**
     * Test of getJsonField method, of class JsonLoader
     */
    @Test
    public void testGetJsonField_Success() {
        JsonLoader jl = new JsonLoader();
        JSONObject field = jl.getJsonField("Client");
        assertNotNull(field);
    }

    /**
     * Test of getJsonField method, of class JsonLoader
     */
    @Test
    public void testGetJsonField_Fail() {
        JsonLoader jl = new JsonLoader();
        JSONObject field = jl.getJsonField("Bob");
        assertNull(field);
    }
}