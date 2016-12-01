package generators;

import entities.Client;
import entities.Event;
import entities.Order;
import enums.Enums.EventType;
import transmission.Emitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by dogle on 30/11/2016.
 */
public class EventGenerator implements Runnable {

    private final EventType[] eventList = {EventType.CREATE};
    private static HashMap<String, Order> orderList = new HashMap<String, Order>();
    private ArrayList<Client> clientList;
    private Random random;

    public EventGenerator(ArrayList<Client> clientList, Random random) {
        this.clientList = clientList;
        this.random = random;
    }

    public void run() {
        boolean interrupted = false;
         while(!interrupted) {
            Event newEvent = null;
            // Get an event type
            EventType type = getEventType();
            switch (type) {
                case CREATE:
                    newEvent = generateCreateEvent();
                    break;
            }
            // Emit event
            Emitter.emitEvent(newEvent);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                interrupted = true;
                System.out.println("Stopping: ");
                System.out.println(e.getMessage());
            }
        }
    }

    private EventType getEventType() {
        return eventList[random.nextInt(eventList.length)];
    }

    public Event generateCreateEvent() {
        /*
        Raise a create event
        Select a random client
        */
        Client client = this.clientList.get(random.nextInt(clientList.size()));

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

        Event event = new Event(EventType.CREATE, order);

        return event;
    }
}
