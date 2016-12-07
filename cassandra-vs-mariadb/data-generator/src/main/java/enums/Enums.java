package enums;

/*
 * Created by dogle on 30/11/2016.
 */
public class Enums {

    public enum EventType {
        CREATE, READ, UPDATE, UPDATE_STATUS, DELETE
    }

    public enum ProductType {
        HAT
    }

    public enum OrderStatus {
        ORDERED, PROCESSING, DISPATCHED, DELIVERED
    }

    public static String[] getEventTypes(){
        EventType[] eventTypes = EventType.values();
        String[] eventNames = new String[eventTypes.length];

        for(int i = 0; i< eventTypes.length; i++) {
            eventNames[i] = eventTypes[i].name();
        }

        return eventNames;
    }
}
