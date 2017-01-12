package dataGenerator.generators;

import dataGenerator.Updaters.OrderUpdater;
import dataGenerator.data_handlers.Settings;
import dataGenerator.entities.Client;
import dataGenerator.entities.Event;
import dataGenerator.entities.Order;
import dataGenerator.enums.Enums.ProductType;
import dataGenerator.enums.Enums.EventType;
import org.apache.commons.lang3.ArrayUtils;
import dataGenerator.transmission.Emitter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by dogle on 30/11/2016.
 */
public class EventGenerator implements Runnable {

    private HashMap<String, Order> orderList = new HashMap<String, Order>();
    private ArrayList<Client> clientList;
    private Random random;

    private int numClients = Settings.getIntSetting("NUM_CLIENTS");
    private final int totalStoredOrders = Settings.getIntSetting("ORDER_CACHE_SIZE");
    private String eventGenMode = Settings.getStringSetting("EVENT_GEN_MODE");
    private int numFixedEvents = Settings.getIntSetting("NUM_FIXED_EVENTS");

    private EventType[] events = {};
    private int eventCounter = 1;
    private int fixedEventCount = 0;
    private int fixedEventTypeCount = 0;

    public EventGenerator(Random random) {
        ClientGenerator clientGen = new dataGenerator.generators.ClientGenerator(random);
        this.clientList = clientGen.getClients(this.numClients);
        this.random = random;
    }

    public EventGenerator(Random random, EventType[] events) {
        ClientGenerator clientGen = new dataGenerator.generators.ClientGenerator(random);
        this.clientList = clientGen.getClients(this.numClients);
        this.random = random;
        this.events = events;
    }

    public void run() {
        EventType type = EventType.CREATE;
        boolean interrupted = false;
        while (!interrupted) {
            try {
                // Check the mode
                if (eventGenMode.equals("fixed")) {
                    int eventListLength =  EventType.values().length;
                    if (this.events.length > 0) eventListLength = this.events.length;
                    type = getFixedEventType(fixedEventTypeCount);
                    fixedEventCount++;
                    if (fixedEventCount == numFixedEvents) {
                        fixedEventTypeCount++;
                        fixedEventCount = 0;
                    }
                    if (fixedEventTypeCount == eventListLength) fixedEventTypeCount = 0;
                }
                if (eventGenMode.equals("random")) {
                    type = getRandomEventType();
                }
                Event newEvent = generateEvents(type);
                // Emit event
                System.out.println(eventCounter + ":" + Emitter.emitEvent(newEvent));
                eventCounter++;
                if (newEvent.getType() == EventType.CREATE) addOrderToList((Order) newEvent.getData());
                if (newEvent.getType() == EventType.DELETE) removeOrderFromList((String) newEvent.getData());
                Thread.sleep(Settings.getIntSetting("SLEEP"));
            } catch (InterruptedException e) {
                interrupted = true;
                System.out.println("Stopping: ");
                System.out.println(e.getMessage());
            }
        }
    }

    public Event getNextEvent() {
        EventType type = EventType.CREATE;
        // Check the mode
        if (eventGenMode.equals("fixed")) {
            int eventListLength = EventType.values().length;
            if (this.events.length > 0) eventListLength = this.events.length;
            type = getFixedEventType(fixedEventTypeCount);
            fixedEventCount++;
            if (fixedEventCount == numFixedEvents) {
                fixedEventTypeCount++;
                fixedEventCount = 0;
            }
            if (fixedEventTypeCount == eventListLength) fixedEventTypeCount = 0;
        }
        if (eventGenMode.equals("random")) {
            type = getRandomEventType();
        }
        Event newEvent = generateEvents(type);
        eventCounter++;
        if (newEvent.getType() == EventType.CREATE) addOrderToList((Order) newEvent.getData());
        if (newEvent.getType() == EventType.DELETE) removeOrderFromList((String) newEvent.getData());
        return newEvent;
    }

    private Event generateEvents(EventType eventType) {
        Event newEvent = null;

        switch (eventType) {
            case CREATE:
                newEvent = generateCreateEvent();
                break;
            case READ:
                newEvent = generateReadEvent();
                break;
            case UPDATE:
                newEvent = generateUpdateEvent(EventType.UPDATE);
                break;
            case UPDATE_STATUS:
                newEvent = generateUpdateEvent(EventType.UPDATE_STATUS);
                break;
            case DELETE:
                newEvent = generateDeleteEvent();
                break;
        }
        return newEvent;
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

    public Event generateReadEvent() {

        Event event;

        if (this.orderList.size() >= 1) {
            String randomOrderId = this.orderList.keySet().toArray()[this.random.nextInt(this.orderList.keySet().toArray().length)].toString();
            event = new Event(EventType.READ, randomOrderId);
        } else { // If there have been no create events yet, raise a create event instead
            event = generateCreateEvent();
        }
        return event;
    }

    public Event generateUpdateEvent(EventType type) {
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
            if (type == EventType.UPDATE) {
                updateOrder = orderUpdater.updateOrder(updateOrder);
            }
            if (type == EventType.UPDATE_STATUS) {
                updateOrder = orderUpdater.updateOrderStatus(updateOrder);
            }
            event = new Event(type, updateOrder);
        } else { // If there have been no create events yet, raise a create event instead
            event = generateCreateEvent();
        }
        return event;
    }

    public Event generateDeleteEvent() {

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
        for (Object key : keyList) {
            Order o = this.orderList.get(key.toString());
            if (o.getDate().before(oldestDate)) {
                oldestOrder = o.getId();
                oldestDate = o.getDate();
            }
        }
        return oldestOrder;
    }

    public void setEvents(EventType[] events) {
        this.events = events;
    }

    public void setClientNumber(int numClients) {
        this.numClients = numClients;
    }

    private EventType getFixedEventType(int fixedEventTypeCount) {
        EventType[] events =  EventType.values();
        if (this.events.length > 0) events = this.events;
        EventType type = events[fixedEventTypeCount];
        return type;
    }

    private EventType getRandomEventType() {
        // One in 5 chance of generating a delete event
        EventType[] events =  EventType.values();
        if (this.events.length > 0) events = this.events;
        int c = Settings.getIntSetting("DELETE_CHANCE");
        int weight = this.random.nextInt(c) + 1;
        if (weight == 1 && ArrayUtils.contains(events, EventType.DELETE)) return EventType.DELETE;
        EventType[] reducedList = ArrayUtils.removeElement(events, EventType.DELETE);
        return reducedList[random.nextInt(events.length - 1)];
    }
}
