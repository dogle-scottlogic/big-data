package entities;

import enums.Enums.EventType;

/**
 * Created by dogle on 30/11/2016.
 */
public class Event<T> {
    private EventType type;
    private T data;

    public Event(EventType type, T data){
        this.type = type;
        this.data = data;
    }

    public EventType getType() {
        return this.type;
    }

    public T getData() {
        return data;
    }

}
