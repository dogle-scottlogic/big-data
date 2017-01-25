package dataGenerator.enums;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by dogle on 01/12/2016.
 */
public class EnumsTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getEventTypes() throws Exception {
        String[] enumList = Enums.getEventTypes();
        assertEquals(5, enumList.length);
        assertEquals("CREATE", enumList[0]);
    }

}