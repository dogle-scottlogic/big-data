/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generators;

import entities.Client;
import java.util.ArrayList;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dogle
 */
public class ClientGeneratorTest {
    
    public ClientGeneratorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getClients method, of class ClientGenerator.
     */
    @Test
    public void testGetClients() {
        System.out.println("getClients - With Random Seed");
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
        System.out.println("getClients - With Random Seed - Empty List");
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
        System.out.println("generateClient - With Random Seed");
        Random random = new Random(1234);
        ClientGenerator cg = new ClientGenerator(random);
        Client testClient = cg.generateClient();
        
        assertNotNull(testClient.getName());
        assertNotNull(testClient.getAddress());
        assertNotNull(testClient.getEmail());
        assertNotNull(testClient.getId());
    }
    
}
