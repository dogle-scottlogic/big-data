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
    private final String[] names;
    private final String[] addresses;

    public ClientGenerator(Random random) {
        this.random = random;
        this.names = getFirstNames();
        this.addresses = getAddresses();
    }

    public ArrayList<Client> getClients(int number) {
        ArrayList<Client> clients = new ArrayList<>();
        for(int i = 0; i < number; i++) {
            clients.add(generateClient());
        }
        return clients;
    }
    
    public Client generateClient() {
        UUID clientUUID = UUID.randomUUID();
        String name = this.names[random.nextInt(this.names.length)];
        String address = this.addresses[random.nextInt(this.addresses.length)];
        String email = getEmail(name);
        Client newClient = new Client(clientUUID, name, address, email);
        return newClient;
    }

    private String[] getFirstNames() {
        String[] mynames
                = {
                    "Helen Erickson",
                    "Alexis Dean",
                    "Georgia Webb",
                    "George Lloyd",
                    "Rick Richardson",
                    "Toni Reid",
                    "Brenda Pearson",
                    "Melba Patrick",
                    "Sylvia Cortez",
                    "Marlene Olson",
                    "Joan Marsh",
                    "Lamar Mckenzie",
                    "Ralph Sparks",
                    "Henry Nguyen",
                    "Gary Hunter"
                };
        return mynames;
    }

    private String[] getAddresses() {
        String[] myAdd = {
            "1/3 Peffermill Rd, Edinburgh EH16 5LE, UK",
            "11C Niddrie Marischal Gardens, Edinburgh EH16 4LX, UK",
            "60 Priestfield Rd, Edinburgh EH16 5JA, UK",
            "33 Old Dalkeith Rd, Edinburgh EH16 4TE, UK",
            "1 Craigmillar Park, Edinburgh EH16 5PG, UK",
            "12-20 A701, Edinburgh EH16, UK",
            "12-4 Burnhead Cres, Edinburgh EH16 6EA, UK",
            "21 Hay Gardens, Edinburgh EH16 4QY, UK",
            "4 Captain's Dr, Edinburgh EH16 6QE, UK",
            "5 Savile Terrace, Edinburgh EH9, UK",
            "Steedman Row, Edinburgh EH16, UK"
        };
        return myAdd;
    }

    private String getEmail(String name) {
        return name.replaceAll("\\s+","") + "@fakemail.com";
    }
}
