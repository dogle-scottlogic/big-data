package generators;

import entities.Client;
import entities.LineItem;
import entities.Order;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author lcollingwood
 */
public class OrderGenerator {
    private Random random;
    private LineItemGenerator lineItemGenerator;
    private final Client client;

    public OrderGenerator(
        Random random, 
        LineItemGenerator lineItemGenerator,
        Client client
    ) {
        this.random = random;
        this.lineItemGenerator = lineItemGenerator;
        this.client = client;
    }
    
    public Order generateOrder() {
        int nLineItems = this.random.nextInt(10) + 1;
        UUID id = UUID.randomUUID();
        ArrayList<LineItem> lineItems = this.lineItemGenerator.generateLineItems(nLineItems);

        return new Order(id, lineItems, this.client);
    }
    
    public ArrayList<Order> generateOrders(int n) {
        ArrayList<Order> orders = new ArrayList();
        for (int i = 0; i < n; i++) {
            orders.add(this.generateOrder());
        }
        return orders;
    }
}