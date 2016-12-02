package generators;

import Updaters.OrderUpdater;
import data_handlers.Settings;
import entities.Client;
import entities.Event;
import entities.Order;
import enums.Enums.ProductType;
import enums.Enums.EventType;
import transmission.Emitter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by dogle on 30/11/2016.
 */
public class EventGenerator implements Runnable {

    private final EventType[] eventList = EventType.values();
    private HashMap<String, Order> orderList = new HashMap<String, Order>();
    private ArrayList<Client> clientList;
    private Random random;
    private final int totalStoredOrders = Settings.getIntSetting("ORDER_CACHE_SIZE");

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
                    newEvent = generateDeleteEvent();
                    break;
            }
            try {
                // Emit event
                Emitter.emitEvent(newEvent);
                if(newEvent.getType() == EventType.CREATE) addOrderToList((Order) newEvent.getData());
                if(newEvent.getType() == EventType.DELETE) removeOrderFromList((String) newEvent.getData());
                Thread.sleep(Settings.getIntSetting("SLEEP"));
            } catch (InterruptedException e) {
                interrupted = true;
                System.out.println("Stopping: ");
                System.out.println(e.getMessage());
            }
        }
    }

    private void removeOrderFromList(String id) {
        this.orderList.remove(id);
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

    public Event generateDeleteEvent() {
        /*
        Raise an update event
        Select a random order
        */
        Event event;

        if (this.orderList.size() >= 1) {
            String randomOrderId = this.orderList.keySet().toArray()[this.random.nextInt(this.orderList.keySet().toArray().length)].toString();
            event = new Event(EventType.DELETE, randomOrderId);
        } else { // If there have been no create events yet, raise a create event instead
            event = generateCreateEvent();
        }
        return event;
    }

    public HashMap<String, Order> getOrderList() {
        return this.orderList;
    }

    public int getTotalStoredOrders() {
        return this.totalStoredOrders;
    }

    public void addOrderToList(Order order) {
        if (this.orderList.size() >= this.totalStoredOrders) {
            String toRemove = findLastUpdate();
            orderList.remove(toRemove);
        }
        // Add to list of raised orders
        orderList.put(order.getId(), order);
    }

    private String findLastUpdate() {
        String oldestOrder = "";
        Date oldestDate = new Date();

        Object[] keyList = this.orderList.keySet().toArray();
        for (Object key: keyList) {
            Order o = this.orderList.get(key.toString());
            if(o.getDate().before(oldestDate)) {
                oldestOrder = o.getId();
                oldestDate = o.getDate();
            }
        }
        return oldestOrder;
    }

    private EventType getEventType() {
        return eventList[random.nextInt(eventList.length)];
    }
}
