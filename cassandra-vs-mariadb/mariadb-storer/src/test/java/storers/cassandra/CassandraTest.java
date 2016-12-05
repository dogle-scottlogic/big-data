package storers.cassandra;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dogle on 05/12/2016.
 */
public class CassandraTest {

    private Cassandra cassandra = null;

    @Before
    public void setUp() throws Exception {
        cassandra = new Cassandra("127.0.0.1");
    }

    @After
    public void tearDown() throws Exception {
        this.cassandra.close();
    }

    @Test
    public void connect() throws Exception {
        boolean test = this.cassandra.connect();
        assertTrue(test);
    }

    @Test
    public void createDropKeySpace() throws Exception {
        this.cassandra.connect();
        boolean test = this.cassandra.createKeySpace("Test");
        assertTrue(test);
        test = this.cassandra.dropKeySpace("Test");
        assertTrue(test);
    }
}