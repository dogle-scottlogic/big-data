import entities.Order;
import generators.ClientGenerator;
import entities.Client;
import generators.LineItemGenerator;
import generators.OrderGenerator;
import generators.ProductGenerator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dogle on 30/11/2016.
 */
public class Main {

    public static void main(String[] args) {

        OrderGenerator og;
        ArrayList<Order> orders = new ArrayList<Order>();

        int seed = 12345;
        int numProducts = 11;
        int numClients = 11;
        int numOrders = 11;

        Random rand = new Random(seed);
        LineItemGenerator lig = new LineItemGenerator(rand, new ProductGenerator(rand), numProducts);
        ClientGenerator cg = new ClientGenerator(rand);

        ArrayList<Client> clients = cg.getClients(numClients);

        // Add orders to the list for each client
        for (Client client:clients) {
             og = new OrderGenerator(rand, lig, client);
            orders.addAll(og.generateOrders(numOrders));
        }

        for (Order order:orders) {
            System.out.println(order.getId());
        }
    }
}
