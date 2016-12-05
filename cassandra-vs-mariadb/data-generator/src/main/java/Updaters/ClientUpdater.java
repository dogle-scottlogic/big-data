package Updaters;

import entities.Client;
import generators.DataGenerator;

import java.lang.reflect.Field;
import java.util.ArrayList;
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

    public Client updateClient(Client originalClient) {
        ArrayList<Field> fields = removeIdFromFieldsList(originalClient.getClass().getDeclaredFields());
        int fieldIndex = random.nextInt(fields.size());
        // Update a random field in the client
        String fieldName = fields.get(fieldIndex).getName();

        if (fieldName.equals("name")) return updateClientName(originalClient);
        if (fieldName.equals("address")) return  updateClientAddress(originalClient);
        if (fieldName.equals("email")) return  updateClientEmail(originalClient);
        return originalClient;
    }

    public Client updateClientName(Client client) {
        client.setName(this.dataGenerator.getNames(1)[0]);
        return client;
    }

    public Client updateClientAddress(Client client) {
        client.setAddress(this.dataGenerator.getAddresses(1)[0]);
        return client;
    }

    public Client updateClientEmail(Client client) {
        String[] emailParts = client.getEmail().split("\\.");
        emailParts[0]+=(Integer.toString(this.random.nextInt(90) + 1));
        if(emailParts.length == 1) {
            client.setEmail(emailParts[0] + ".com");
        } else {
            client.setEmail(emailParts[0] + "." + emailParts[1]);
        }
        return client;
    }

    public ArrayList<Field> removeIdFromFieldsList(Field[] fields) {
        ArrayList<Field> idlessFields = new ArrayList<Field>();
        for (int i = 0; i < fields.length; i++) {
            if (!fields[i].getName().equals("id")) {
                idlessFields.add(fields[i]);
            }
        }
        return idlessFields;
    }
}
