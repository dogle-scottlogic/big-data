package dataGenerator.Updaters;

import dataGenerator.entities.Client;
import dataGenerator.entities.Order;
import dataGenerator.enums.Enums;
import dataGenerator.generators.ClientGenerator;
import dataGenerator.generators.LineItemGenerator;
import dataGenerator.generators.OrderGenerator;
import dataGenerator.generators.ProductGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by dogle on 02/12/2016.
 */
public class ClientUpdaterTest {

    private Random random;

    @Before
    public void setUp() throws Exception {
        this.random = new Random(1234);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void updateClient() throws Exception {
        Client testClient = new Client(UUID.randomUUID(), "bob", "bob_land", "bob@bob");
        ClientUpdater cu = new ClientUpdater(random);
        Client returnValue = cu.updateClient(testClient);
        assertNotNull(returnValue);
        assertEquals(testClient.getName(), returnValue.getName());
        assertEquals(testClient.getId(), returnValue.getId());
        assertEquals(testClient.getAddress(), returnValue.getAddress());
        assertEquals(testClient.getEmail(), returnValue.getEmail());
    }

    @Test
    public void updateClientName() throws Exception {
        Client testClient = new Client(UUID.randomUUID(), "bob", "bob_land", "bob@bob");
        ClientUpdater cu = new ClientUpdater(random);
        Client newClient = cu.updateClientName(testClient);
        assertNotNull(newClient);
        assertNotEquals(newClient.getName(), "bob");
    }

    @Test
    public void updateClientAddress() throws Exception {
        Client testClient = new Client(UUID.randomUUID(), "bob", "bob_land", "bob@bob");
        ClientUpdater cu = new ClientUpdater(random);
        Client newClient = cu.updateClientAddress(testClient);
        assertNotNull(newClient);
        assertNotEquals(newClient.getName(), "bob_land");
    }

    @Test
    public void updateClientEmail1() throws Exception {
        Client testClient = new Client(UUID.randomUUID(), "bob", "bob_land", "bob@bob.com");
        ClientUpdater cu = new ClientUpdater(random);
        String oldEmail = testClient.getEmail();
        Client newClient = cu.updateClientEmail(testClient);
        assertNotNull(newClient);
        assertNotEquals(newClient.getEmail(), oldEmail);
    }

    @Test
    public void removeIdFromFieldsList() throws Exception {
        ClientUpdater cu = new ClientUpdater(this.random);
        ClientGenerator cg = new ClientGenerator(this.random);
        ProductGenerator pg = new ProductGenerator(this.random);
        LineItemGenerator lig = new LineItemGenerator(this.random, pg, Enums.ProductType.values());

        Field[] fieldsWithId = Order.class.getDeclaredFields();
        boolean containsId = false;
        for (Field field:fieldsWithId) {
            if(field.getName().equals("id")) {
                containsId = true;
            }
        }
        assertTrue(containsId);
        containsId = false;
        ArrayList<Field> fieldsWithoutId = cu.removeIdFromFieldsList(fieldsWithId);
        for (Field field:fieldsWithoutId) {
            if(field.getName().equals("id")) {
                containsId = true;
            }
        }
        assertFalse(containsId);
    }
}