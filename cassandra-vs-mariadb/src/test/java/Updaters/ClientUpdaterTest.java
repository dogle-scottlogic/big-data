package Updaters;

import entities.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    public void updateClientUnknownField() throws Exception {
        Client testClient = new Client(UUID.randomUUID(), "bob", "bob_land", "bob@bob");
        ClientUpdater cu = new ClientUpdater(random);
        Client returnValue = cu.updateClient("not_a_field", testClient);
        assertNotNull(returnValue);
        assertEquals(testClient.getName(), returnValue.getName());
        assertEquals(testClient.getId(), returnValue.getId());
        assertEquals(testClient.getAddress(), returnValue.getAddress());
        assertEquals(testClient.getEmail(), returnValue.getEmail());
    }

    @Test
    public void updateClient_Name() throws Exception {
        Client testClient = new Client(UUID.randomUUID(), "bob", "bob_land", "bob@bob");
        ClientUpdater cu = new ClientUpdater(random);
        Client newClient = cu.updateClient("name", testClient);
        assertNotNull(newClient);
        assertNotEquals(newClient.getName(), "bob");
    }

    @Test
    public void updateClient_Address() throws Exception {
        Client testClient = new Client(UUID.randomUUID(), "bob", "bob_land", "bob@bob");
        ClientUpdater cu = new ClientUpdater(random);
        Client newClient = cu.updateClient("address", testClient);
        assertNotNull(newClient);
        assertNotEquals(newClient.getName(), "bob_land");
    }

    @Test
    public void updateClientEmail() throws Exception {
        Client testClient = new Client(UUID.randomUUID(), "bob", "bob_land", "bob@bob.com");
        ClientUpdater cu = new ClientUpdater(random);
        String oldEmail = testClient.getEmail();
        Client newClient = cu.updateClientEmail(testClient);
        assertNotNull(newClient);
        assertNotEquals(newClient.getEmail(), oldEmail);
    }

}