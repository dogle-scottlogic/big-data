package dataGenerator.generators;

import dataGenerator.entities.Client;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by dogle on 30/11/2016.
 */
public class ClientGeneratorTest {
    private final static Logger LOG = Logger.getLogger(ClientGeneratorTest.class);

    /**
     * Test of getClients method, of class ClientGenerator.
     */
    @Test
    public void testGetClients() {
        LOG.info("getClients - With Random Seed");
        Random random = new Random(1234);
        ClientGenerator cg = new ClientGenerator(random);
        ArrayList<Client> clientList = cg.getClients(5);
        assertEquals(clientList.size(), 5);

        random = new Random(1234);
        cg = new ClientGenerator(random);
        ArrayList<Client> clientList2 = cg.getClients(5);
        for(int i = 0; i < clientList.size(); i++) {
            assertEquals(clientList.get(i).getName(), clientList2.get(i).getName());
        }
    }

    /**
     * Test of getClients method, of class ClientGenerator - no items.
     */
    @Test
    public void testGetClientsZeroNumber() {
        LOG.info("getClients - With Random Seed - Empty List");
        Random random = new Random(1234);
        ClientGenerator cg = new ClientGenerator(random);
        ArrayList<Client> clientList = cg.getClients(0);
        assertEquals(clientList.size(), 0);
    }

    /**
     * Test of generateClient method, of class ClientGenerator.
     * Should generate a new client object with filled fields
     */
    @Test
    public void testGenerateClientRandomSeed() {
        LOG.info("generateClient - With Random Seed");
        Random random = new Random(1234);
        ClientGenerator cg = new ClientGenerator(random);
        Client testClient = cg.generateClient();

        assertNotNull(testClient.getName());
        assertNotNull(testClient.getAddress());
        assertNotNull(testClient.getEmail());
        assertNotNull(testClient.getId());
    }

}