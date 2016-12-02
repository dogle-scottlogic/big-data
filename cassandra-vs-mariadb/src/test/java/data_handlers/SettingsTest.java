package data_handlers;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dogle on 02/12/2016.
 */
public class SettingsTest {


    @Test
    public void getIntSetting() throws Exception {
        int test = Settings.getIntSetting("SANITY");
        assertEquals(123, test);
    }

    @Test
    public void getStringSetting() throws Exception {
        String test = Settings.getStringSetting("SANITY");
        assertEquals("123", test);
    }
}