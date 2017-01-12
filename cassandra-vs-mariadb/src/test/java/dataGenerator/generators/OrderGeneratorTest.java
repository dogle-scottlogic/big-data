package dataGenerator.generators;

import dataGenerator.entities.Client;
import dataGenerator.entities.Order;
import dataGenerator.enums.Enums;
import org.junit.*;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by dogle on 30/11/2016.
 */
public class OrderGeneratorTest {

    int seed = 1234;
    Random random;
    OrderGenerator og;
    ProductGenerator pg;
    LineItemGenerator lig;
    Client client;
    Enums.ProductType[] productList = {Enums.ProductType.HAT};

    public OrderGeneratorTest() {

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
        this.lig = new LineItemGenerator(this.random, this.pg, this.productList);
        ClientGenerator cg = new ClientGenerator(this.random);
            this.client = cg.generateClient();
            this.og = new OrderGenerator(this.random, lig, this.client);
        }

        @After
        public void tearDown() {
        }

        /**
         * Test of generateOrder method, of class OrderGenerator.
         */
        @Test
        public void testGenerateOrders() {
            System.out.println("generateOrders - With random seed");
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
        System.out.println("generateOrders - With Random Seed - Empty List");
        this.og = new OrderGenerator(this.random, this.lig, this.client);
        ArrayList<Order> orderList = og.generateOrders(0);
        assertEquals(orderList.size(), 0);
    }

    /**
     * Test of generateProducts method, of class ProductGenerator.
     */
    @Test
    public void testGenerateOrder() {
        System.out.println("generateOrder - With random seed");
        this.og = new OrderGenerator(this.random, this.lig, this.client);
        Order order = og.generateOrder();

        assertNotNull(order.getSubTotal());
        assertNotNull(order.getId());
        assertNotNull(order.getClient());
        assertNotNull(order.getLineItems());
    }
}