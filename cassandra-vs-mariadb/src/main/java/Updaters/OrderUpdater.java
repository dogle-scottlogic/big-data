package Updaters;

import entities.Client;
import entities.LineItem;
import entities.Order;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
        if(updateClient) {
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
        ArrayList<Field> fields = removeIdFromFieldsList(client.getClass().getDeclaredFields());
        int fieldIndex = random.nextInt(fields.size());
        // Update a random field in the client
        client = clientUpdater.updateClient(fields.get(fieldIndex).getName(), client);
        return client;
    }

    private ArrayList<LineItem> updateLineItems(ArrayList<LineItem> lineItems) {
        return lineItems;
    }

    public ArrayList<Field> removeIdFromFieldsList(Field[] fields) {
        ArrayList<Field> idlessFields = new ArrayList<Field>();
        for(int i = 0; i < fields.length; i++) {
            if(!fields[i].getName().equals("id")) {
                idlessFields.add(fields[i]);
            }
        }
        return idlessFields;
    }
}
