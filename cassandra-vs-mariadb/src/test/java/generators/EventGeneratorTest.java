package generators;

import entities.Client;
import entities.Order;
import entities.Event;
import enums.Enums;
import org.junit.*;
import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by dogle on 30/11/2016.
 */
public class EventGeneratorTest {

    int seed = 1234;
    Random random;
    OrderGenerator og;
    ProductGenerator pg;
    LineItemGenerator lig;
    Client client;
    ClientGenerator cg;

    public EventGeneratorTest() {

    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        this.random = new Random(this.seed);
        this.pg = new ProductGenerator(this.random);
        Enums.ProductType[] productList = {Enums.ProductType.HAT};
        this.lig = new LineItemGenerator(this.random, this.pg, productList);
        cg = new ClientGenerator(this.random);
        this.client = cg.generateClient();
        this.og = new OrderGenerator(this.random, lig, this.client);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void generateCreateEvent() throws Exception {
        EventGenerator eg = new EventGenerator(cg.getClients(5), this.random);
        Event newEvent = eg.generateCreateEvent();

        Assert.assertNotNull(newEvent);
        Assert.assertNotNull(newEvent.getData());
        Assert.assertNotNull(newEvent.getType());

        assertEquals(Enums.EventType.CREATE, newEvent.getType());
        assertThat(newEvent.getData(), instanceOf(Order.class));
    }

    @Test
    public void addOrderToList() {
        EventGenerator eg = new EventGenerator(cg.getClients(5), this.random);

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
        EventGenerator eg = new EventGenerator(cg.getClients(5), this.random);
        Event newEvent = eg.generateUpdateEvent();

        Assert.assertNotNull(newEvent);
        Assert.assertNotNull(newEvent.getData());
        Assert.assertNotNull(newEvent.getType());

        assertEquals(Enums.EventType.CREATE, newEvent.getType());
        assertThat(newEvent.getData(), instanceOf(Order.class));
    }

    @Test
    public void generateUpdateEvent_CreatedOrders() throws Exception {
        EventGenerator eg = new EventGenerator(cg.getClients(5), this.random);
        Event newEvent = eg.generateUpdateEvent();

        Assert.assertNotNull(newEvent);
        Assert.assertNotNull(newEvent.getData());
        Assert.assertNotNull(newEvent.getType());

        assertEquals(Enums.EventType.CREATE, newEvent.getType());
        assertThat(newEvent.getData(), instanceOf(Order.class));

        eg.addOrderToList((Order)newEvent.getData());
        newEvent = eg.generateUpdateEvent();

        Assert.assertNotNull(newEvent);
        Assert.assertNotNull(newEvent.getData());
        Assert.assertNotNull(newEvent.getType());

        assertEquals(Enums.EventType.UPDATE, newEvent.getType());
        assertThat(newEvent.getData(), instanceOf(Order.class));
    }

}