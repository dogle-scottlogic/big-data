import generators.*;
import entities.Client;
import transmission.Emitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dogle on 30/11/2016.
 */
public class Main {

    private static Thread thread = null;
    private static final int seed = 123435;
    private static Random random = new Random(seed);

    private static ArrayList<Client> clients = new ArrayList<Client>();
    private static final ClientGenerator clientGen = new ClientGenerator(random);
    private static final int numClients = 20;

    public static void main(String[] args) throws IOException {

        // Create a list of clients
        clients = clientGen.getClients(numClients);

        EventGenerator eg = new EventGenerator(clients, random);
        thread = new Thread(eg);
        Emitter.initialize();
        thread.start();
        System.in.read();
        thread.interrupt();
        Emitter.end();
    }
}
