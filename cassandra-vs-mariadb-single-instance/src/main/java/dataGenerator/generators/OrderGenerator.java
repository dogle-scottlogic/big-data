package dataGenerator.generators;

import dataGenerator.data_handlers.Settings;
import dataGenerator.entities.Client;
import dataGenerator.entities.LineItem;
import dataGenerator.entities.Order;
import dataGenerator.enums.Enums.OrderStatus;

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
    private final int maxLineItems;

    public OrderGenerator(
        Random random, 
        LineItemGenerator lineItemGenerator,
        Client client
    ) {
        this.random = random;
        this.lineItemGenerator = lineItemGenerator;
        this.client = client;
        this.maxLineItems = Settings.getIntSetting("MAX_LINE_ITEMS");
    }
    
    public Order generateOrder() {
        int nLineItems = this.random.nextInt(this.maxLineItems) + 1;
        UUID id = UUID.randomUUID();
        ArrayList<LineItem> lineItems = this.lineItemGenerator.generateLineItems(nLineItems);
        OrderStatus status = OrderStatus.values()[this.random.nextInt(OrderStatus.values().length)];

        return new Order(id, lineItems, this.client, status);
    }
    
    public ArrayList<Order> generateOrders(int n) {
        ArrayList<Order> orders = new ArrayList();
        for (int i = 0; i < n; i++) {
            orders.add(this.generateOrder());
        }
        return orders;
    }
}
