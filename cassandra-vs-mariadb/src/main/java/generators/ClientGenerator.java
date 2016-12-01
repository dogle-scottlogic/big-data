/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generators;

import entities.Client;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author lcollingwood
 */
public class ClientGenerator {

    private final Random random;
    private final DataGenerator dataGenerator;

    public ClientGenerator(Random random) {
        this.random = random;
        this.dataGenerator = new DataGenerator(random);;
    }

    public ArrayList<Client> getClients(int number) {
        ArrayList<Client> clients = new ArrayList();
        for(int i = 0; i < number; i++) {
            clients.add(generateClient());
        }
        return clients;
    }
    
    public Client generateClient() {
        UUID clientUUID = UUID.randomUUID();
        String name = dataGenerator.getNames(1)[0];
        String address = dataGenerator.getAddresses(1)[0];
        String email = dataGenerator.getEmail(name);
        Client newClient = new Client(clientUUID, name, address, email);
        return newClient;
    }
}
