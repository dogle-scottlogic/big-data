import entities.Event;
import entities.Order;
import enums.Enums.EventType;
import generators.ClientGenerator;
import entities.Client;
import generators.LineItemGenerator;
import generators.OrderGenerator;
import generators.ProductGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by dogle on 30/11/2016.
 */
public class Main {

    private static final int seed = 123435;
    private static Random random = new Random(seed);

    private static ArrayList<Client> clients = new ArrayList<Client>();
    private static final ClientGenerator clientGen = new ClientGenerator(random);
    private static final int numClients = 20;


    private static HashMap<String, Order> orderList = new HashMap<String, Order>();

    public static void main(String[] args) {

        // Create a list of clients
        clients = clientGen.getClients(numClients);

        /*
        Raise a create event
        Select a random client
        */
        Client client = clients.get(random.nextInt(clients.size()));

        //raise an order
        ProductGenerator pg = new ProductGenerator(random);
        LineItemGenerator lig = new LineItemGenerator(random, pg, random.nextInt(20) + 1);
        OrderGenerator og = new OrderGenerator(random, lig, client);
        Order order = og.generateOrder();

        // Add to list of raised orders
        orderList.put(order.getId(), order);
        // Create Event
        Event createEvent = new Event<Order>(EventType.CREATE, order);
        System.out.println("Created event: ");
        System.out.println("Type: " + createEvent.getType());
        System.out.println("data: " + createEvent.getData().toString());
    }
}
