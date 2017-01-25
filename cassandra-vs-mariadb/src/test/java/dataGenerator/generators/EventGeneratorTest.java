package dataGenerator.generators;

import dataGenerator.entities.Event;
import dataGenerator.entities.Order;
import dataGenerator.enums.Enums;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * Created by dogle on 30/11/2016.
 */
public class EventGeneratorTest {

    private int seed = 1234;
    private Random random;

    @Before
    public void setUp() {
        this.random = new Random(this.seed);
    }

    @Test
    public void generateCreateEvent() throws Exception {
        EventGenerator eg = new EventGenerator(this.random);
        Event newEvent = eg.generateCreateEvent();

        Assert.assertNotNull(newEvent);
        Assert.assertNotNull(newEvent.getData());
        Assert.assertNotNull(newEvent.getType());

        assertEquals(Enums.EventType.CREATE, newEvent.getType());
        assertThat(newEvent.getData(), instanceOf(Order.class));
    }

    @Test
    public void addOrderToList() {
        EventGenerator eg = new EventGenerator(this.random);

        for(int i = 0; i < eg.getTotalStoredOrders(); i++) {
            Event newEvent = eg.generateCreateEvent();
            eg.addOrderToList((Order)newEvent.getData());
        }
        assertTrue(eg.getOrderList().size() == eg.getTotalStoredOrders());
        Event newEvent = eg.generateCreateEvent();
        eg.addOrderToList((Order)newEvent.getData());
        assertTrue(eg.getOrderList().size() == eg.getTotalStoredOrders());
        assertTrue(eg.getOrderList().containsKey(((Order) newEvent.getData()).getId()));
    }

    @Test
    public void generateUpdateEvent_NoCreatedOrders() throws Exception {
        EventGenerator eg = new EventGenerator(this.random);
        Event newEvent = eg.generateUpdateEvent(Enums.EventType.UPDATE);

        Assert.assertNotNull(newEvent);
        Assert.assertNotNull(newEvent.getData());
        Assert.assertNotNull(newEvent.getType());

        assertEquals(Enums.EventType.CREATE, newEvent.getType());
        assertThat(newEvent.getData(), instanceOf(Order.class));
    }

    @Test
    public void generateUpdateEvent_CreatedOrders() throws Exception {
        EventGenerator eg = new EventGenerator(this.random);
        Event newEvent = eg.generateUpdateEvent(Enums.EventType.UPDATE);

        Assert.assertNotNull(newEvent);
        Assert.assertNotNull(newEvent.getData());
        Assert.assertNotNull(newEvent.getType());

        assertEquals(Enums.EventType.CREATE, newEvent.getType());
        assertThat(newEvent.getData(), instanceOf(Order.class));

        eg.addOrderToList((Order)newEvent.getData());
        newEvent = eg.generateUpdateEvent(Enums.EventType.UPDATE);

        Assert.assertNotNull(newEvent);
        Assert.assertNotNull(newEvent.getData());
        Assert.assertNotNull(newEvent.getType());

        assertEquals(Enums.EventType.UPDATE, newEvent.getType());
        assertThat(newEvent.getData(), instanceOf(Order.class));
    }

}