package dataGenerator.data_handlers;

import dataGenerator.Updaters.OrderUpdater;
import dataGenerator.entities.Order;
import dataGenerator.enums.Enums;
import dataGenerator.generators.ClientGenerator;
import dataGenerator.generators.LineItemGenerator;
import dataGenerator.generators.OrderGenerator;
import dataGenerator.generators.ProductGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Created by dogle on 01/12/2016.
 */
public class OrderUpdaterTest {

    private Random random;

    @Before
    public void setUp() throws Exception {
        this.random = new Random(1234);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void updateOrderStatus() throws Exception {
        OrderUpdater ou = new OrderUpdater(this.random);
        ClientGenerator cg = new ClientGenerator(this.random);
        ProductGenerator pg = new ProductGenerator(this.random);
        LineItemGenerator lig = new LineItemGenerator(this.random, pg, Enums.ProductType.values());
        OrderGenerator og = new OrderGenerator(this.random, lig, cg.generateClient());
        Order order = ou.updateOrder(og.generateOrder());
        Enums.OrderStatus status = order.getStatus();
        order = ou.updateOrderStatus(order);
        assertNotEquals(order.getStatus(), status);
    }
}