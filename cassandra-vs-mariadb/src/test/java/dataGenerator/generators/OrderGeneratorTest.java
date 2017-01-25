package dataGenerator.generators;

import dataGenerator.entities.Client;
import dataGenerator.entities.Order;
import dataGenerator.enums.Enums;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by dogle on 30/11/2016.
 */
public class OrderGeneratorTest {

    private final static Logger LOG = Logger.getLogger(OrderGeneratorTest.class);
    private int seed = 1234;
    private Random random;
    private OrderGenerator og;
    private ProductGenerator pg;
    private LineItemGenerator lig;
    private Client client;
    private Enums.ProductType[] productList = {Enums.ProductType.HAT};

    @Before
    public void setUp() {

        this.random = new Random(this.seed);
        this.pg = new ProductGenerator(this.random);
        this.lig = new LineItemGenerator(this.random, this.pg, this.productList);
        ClientGenerator cg = new ClientGenerator(this.random);
        this.client = cg.generateClient();
        this.og = new OrderGenerator(this.random, lig, this.client);
    }

    /**
     * Test of generateOrder method, of class OrderGenerator.
     */
    @Test
    public void testGenerateOrders() {
        LOG.info("generateOrders - With random seed");
        ArrayList<Order> orderList = og.generateOrders(5);
        assertEquals(orderList.size(), 5);

        this.random = new Random(this.seed);
        this.pg = new ProductGenerator(this.random);
        this.lig = new LineItemGenerator(this.random, this.pg, this.productList);
        ClientGenerator cg = new ClientGenerator(this.random);
        this.client = cg.generateClient();
        this.og = new OrderGenerator(this.random, this.lig, this.client);
        ArrayList<Order> orderList2 = og.generateOrders(5);
        for (int i = 0; i < orderList.size(); i++) {
            assertEquals(0, Double.doubleToLongBits(orderList.get(i).getSubTotal()), Double.doubleToLongBits(orderList2.get(i).getSubTotal()));
        }
    }

    /**
     * Test of generateOrder method, of class OrderGenerator - no items.
     */
    @Test
    public void testGenerateOrdersNoItems() {
        LOG.info("generateOrders - With Random Seed - Empty List");
        this.og = new OrderGenerator(this.random, this.lig, this.client);
        ArrayList<Order> orderList = og.generateOrders(0);
        assertEquals(orderList.size(), 0);
    }

    /**
     * Test of generateProducts method, of class ProductGenerator.
     */
    @Test
    public void testGenerateOrder() {
        LOG.info("generateOrder - With random seed");
        this.og = new OrderGenerator(this.random, this.lig, this.client);
        Order order = og.generateOrder();

        assertNotNull(order.getSubTotal());
        assertNotNull(order.getId());
        assertNotNull(order.getClient());
        assertNotNull(order.getLineItems());
    }
}