package generators;

import entities.Event;
import entities.Order;
import enums.Enums.EventType;

/**
 * Created by dogle on 30/11/2016.
 */
public class EventGenerator {

    public Event generateCreateEvent(Order order) {
        Event event = new Event(EventType.CREATE, order);
        return event;
    }
}
