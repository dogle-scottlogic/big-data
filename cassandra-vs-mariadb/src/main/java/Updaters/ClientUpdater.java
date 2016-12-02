package Updaters;

import entities.Client;
import generators.DataGenerator;

import java.util.Random;

/**
 * Created by dogle on 02/12/2016.
 */
public class ClientUpdater {

    private Random random;
    private DataGenerator dataGenerator;

    public ClientUpdater(Random random) {
        this.random = random;
        this.dataGenerator = new DataGenerator(random);
    }

    public Client updateClient(String fieldName, Client originalClient) {

        if (fieldName.equals("name")) return updateClientName(originalClient);
        if (fieldName.equals("address")) return  updateClientAddress(originalClient);
        if (fieldName.equals("email")) return  updateClientEmail(originalClient);
        return originalClient;
    }

    private Client updateClientName(Client client) {
        client.setName(this.dataGenerator.getNames(1)[0]);
        return client;
    }

    private Client updateClientAddress(Client client) {
        client.setAddress(this.dataGenerator.getAddresses(1)[0]);
        return client;
    }

    public Client updateClientEmail(Client client) {
        String[] emailParts = client.getEmail().split("\\.");
        emailParts[0]+=(Integer.toString(this.random.nextInt(90) + 1));
        client.setEmail(emailParts[0] + emailParts[1]);
        return client;
    }

}
