package Updaters;

import entities.Client;
import entities.LineItem;
import entities.Order;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dogle on 01/12/2016.
 */
public class OrderUpdater {

    private Random random;

    public OrderUpdater(Random random) {
        this.random = random;
    }

    public Order updateOrder(Order order) {

        boolean updateClient = random.nextBoolean();
        if (updateClient) {
            try {
                order.setClient(updateClient(order.getClient()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            // Update the line item
            order.setLineItems(updateLineItems(order.getLineItems()));
        }
        return order;
    }

    private Client updateClient(Client client) throws IllegalAccessException {
        ClientUpdater clientUpdater = new ClientUpdater(this.random);
        client = clientUpdater.updateClient(client);
        return client;
    }

    private ArrayList<LineItem> updateLineItems(ArrayList<LineItem> lineItems) {
        //Select a random line item to update
        int lineItemToAmendIndex = random.nextInt(lineItems.size());
        LineItem lineItemToAmend = lineItems.get(lineItemToAmendIndex);
        boolean updateProduct = random.nextBoolean();
        if (updateProduct) {
            // Update a random product field
            ProductUpdater productUpdater = new ProductUpdater(this.random);
            lineItemToAmend.setProduct(productUpdater.updateRandomProduct(lineItemToAmend.getProduct()));
        } else {
            // Update a random line item field
            LineItemUpdater lineItemUpdater = null;
            switch (lineItemToAmend.getProduct().getProductType()) {
                case HAT:
                    lineItemUpdater = new HatLineItemUpdater(this.random);
                    break;
            }
            lineItems.set(lineItemToAmendIndex, lineItemUpdater.updateRandomLineItemField(lineItemToAmend));
        }
        return lineItems;
    }
}
