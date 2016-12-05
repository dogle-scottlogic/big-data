import data_handlers.Settings;
import generators.*;
import entities.Client;
import transmission.Emitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.System.*;

/**
 * Created by dogle on 30/11/2016.
 */
public class Main {

    private static Thread thread = null;
    private static final int seed = Settings.getIntSetting("SEED");
    private static Random random = new Random(seed);

    private static ArrayList<Client> clients = new ArrayList<Client>();
    private static final ClientGenerator clientGen = new ClientGenerator(random);
    private static final int numClients = Settings.getIntSetting("NUM_CLIENTS");
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {

        // Create a list of clients
        run();
    }

    public static void run() throws IOException {
        String input = "";
        clients = clientGen.getClients(numClients);
        EventGenerator eg = new EventGenerator(clients, random);
        thread = new Thread(eg);

        out.println("Data Generator 1.0");
        while(!input.equals("exit")) {
            System.out.print(">");
            input = br.readLine();
            if (input.equals("run")) {
                Emitter.initialize();
                thread.start();
            }
            if (input.equals("stop")) {
                thread.interrupt();
                Emitter.end();
                thread = new Thread(eg);
            }
        }
    }
}
