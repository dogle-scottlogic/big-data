package storers.storers.maria.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SQLQueryTest {

    @Test
    public void parametersAreSubstitutedWhenProvided() {
        String actual = SQLQuery.CONNECTION_STRING.getQuery("1.2.3.4");
        assertEquals("jdbc:mariadb://1.2.3.4:3306/?user=root&password=myfirstpassword", actual);
    }

    @Test
    public void parametersAreNotSubstitutedIfNotProvided() {
        String actual = SQLQuery.CONNECTION_STRING.getQuery();
        assertEquals("jdbc:mariadb://${0}:3306/?user=root&password=myfirstpassword", actual);
    }
}