package generators;

import Updaters.OrderUpdater;
import entities.Client;
import entities.Event;
import entities.Order;
import enums.Enums;
import enums.Enums.ProductType;
import enums.Enums.EventType;
import transmission.Emitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by dogle on 30/11/2016.
 */
public class EventGenerator implements Runnable {

    private final EventType[] eventList = {EventType.CREATE, EventType.UPDATE}; //TODO put back in Enums.EventType.values();
    private HashMap<String, Order> orderList = new HashMap<String, Order>();
    private ArrayList<Client> clientList;
    private Random random;
    private final int totalStoredOrders = 20; //TODO config file

    public EventGenerator(ArrayList<Client> clientList, Random random) {
        this.clientList = clientList;
        this.random = random;
    }

    public void run() {
        boolean interrupted = false;
        while (!interrupted) {
            Event newEvent = null;
            // Get an event type
            EventType type = getEventType();
            switch (type) {
                case CREATE:
                    newEvent = generateCreateEvent();
                    break;
                case UPDATE:
                    newEvent = generateUpdateEvent();
                    break;
                case DELETE:
                    newEvent = generarateDeleteEvent();
                    break;
            }
            try {
                // Emit event
                Emitter.emitEvent(newEvent);
                addOrderToList((Order) newEvent.getData());
                Thread.sleep(2000); //TODO move to config file
            } catch (InterruptedException e) {
                interrupted = true;
                System.out.println("Stopping: ");
                System.out.println(e.getMessage());
            }

        }
    }

    public Event generateCreateEvent() {
        /*
        Raise a create event
        Select a random client
        */
        Client client = this.clientList.get(random.nextInt(clientList.size()));

        //raise an order
        ProductGenerator pg = new ProductGenerator(random);
        ProductType[] productList = {ProductType.HAT};
        LineItemGenerator lig = new LineItemGenerator(random, pg, productList);
        OrderGenerator og = new OrderGenerator(random, lig, client);
        Order order = og.generateOrder();
        // Create Event
        Event createEvent = new Event<Order>(EventType.CREATE, order);
        System.out.println("Event raised: ");
        System.out.println("Type: " + createEvent.getType());
        Event event = new Event(EventType.CREATE, order);
        return event;
    }

    public Event generateUpdateEvent() {
        /*
        Raise an update event
        Select a random order
        */
        Event event;

        if (this.orderList.size() >= 1) {
            String randomOrderId = this.orderList.keySet().toArray()[this.random.nextInt(this.orderList.keySet().toArray().length)].toString();
            Order updateOrder = this.orderList.get(randomOrderId);
            OrderUpdater orderUpdater = new OrderUpdater(this.random);
            // Modify the order in some way
            updateOrder = orderUpdater.updateOrder(updateOrder);
            event = new Event(EventType.UPDATE, updateOrder);
        } else { // If there have been no create events yet, raise a create event instead
            event = generateCreateEvent();
        }
        return event;
    }

    public Event generarateDeleteEvent() {
        
    }

    public HashMap<String, Order> getOrderList() {
        return this.orderList;
    }

    public Order getOrderById(String id) {
        return this.orderList.get(id);
    }

    public int getTotalStoredOrders() {
        return this.totalStoredOrders;
    }

    public void addOrderToList(Order order) {
        if (this.orderList.size() >= this.totalStoredOrders) {
            String toRemove = (this.orderList.keySet()).toArray()[0].toString();
            orderList.remove(toRemove);
        }
        // Add to list of raised orders
        orderList.put(order.getId(), order);
    }

    private EventType getEventType() {
        return eventList[random.nextInt(eventList.length)];
    }
}
