package storers.maria;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import storers.maria.enums.SQLQuery;

import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

/**
 * Created by lcollingwood on 07/12/2016.
 */
public class MariaDBStorerTest {

    private MariaDBStorer mariaDBStorer;
    private JSONParser parser;

    @Before
    public void setUp() throws Exception {
        this.mariaDBStorer = new MariaDBStorer();
        this.parser = new JSONParser();
    }

    @After
    public void tearDown() throws Exception {
        this.mariaDBStorer.end();
    }

    @Test
    public void messageHandler() throws Exception {

    }
}