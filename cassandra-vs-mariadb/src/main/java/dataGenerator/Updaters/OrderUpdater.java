package dataGenerator.Updaters;

import dataGenerator.Updaters.Products.HatUpdater;
import dataGenerator.entities.Client;
import dataGenerator.entities.LineItem;
import dataGenerator.entities.Order;
import dataGenerator.enums.Enums;

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

        // Update the line item
        order.setLineItems(updateLineItems(order.getLineItems()));
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
            ProductUpdater productUpdater = null;
            switch (lineItemToAmend.getProduct().getProductType()) {
                case HAT:
                    productUpdater = new HatUpdater(this.random);
                    break;
            }
            lineItemToAmend.setProduct(productUpdater.updateRandomProduct(lineItemToAmend.getProduct()));
        } else {
            // Update a random line item field
            LineItemUpdater lineItemUpdater = new LineItemUpdater(this.random);
            lineItems.set(lineItemToAmendIndex, lineItemUpdater.updateRandomLineItemField(lineItemToAmend));
        }
        return lineItems;
    }

    public Order updateOrderStatus(Order updateOrder) {
        updateOrder.setStatus(Enums.OrderStatus.values()[this.random.nextInt(Enums.OrderStatus.values().length)]);
        return updateOrder;
    }
}
