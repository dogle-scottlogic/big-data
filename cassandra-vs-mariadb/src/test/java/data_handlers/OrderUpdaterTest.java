package data_handlers;

import Updaters.OrderUpdater;
import entities.Order;
import enums.Enums;
import generators.ClientGenerator;
import generators.LineItemGenerator;
import generators.OrderGenerator;
import generators.ProductGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

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
    public void updateOrder() throws Exception {

    }

    @Test
    public void removeIdFromFieldsList() throws Exception {
        OrderUpdater ou = new OrderUpdater(this.random);
        ClientGenerator cg = new ClientGenerator(this.random);
        ProductGenerator pg = new ProductGenerator(this.random);
        LineItemGenerator lig = new LineItemGenerator(this.random, pg, Enums.ProductType.values());
        OrderGenerator og = new OrderGenerator(this.random, lig, cg.generateClient());

        Order testOrder = og.generateOrder();
        Field[] fieldsWithId = Order.class.getDeclaredFields();
        boolean containsId = false;
        for (Field field:fieldsWithId) {
            if(field.getName().equals("id")) {
                containsId = true;
            }
        }
        assertTrue(containsId);
        containsId = false;
        ArrayList<Field> fieldsWithoutId = ou.removeIdFromFieldsList(fieldsWithId);
        for (Field field:fieldsWithoutId) {
            if(field.getName().equals("id")) {
                containsId = true;
            }
        }
        assertFalse(containsId);
    }

    @Test
    public void updateClient() {
        OrderUpdater ou = new OrderUpdater(this.random);
        ClientGenerator cg = new ClientGenerator(this.random);
        ProductGenerator pg = new ProductGenerator(this.random);
        LineItemGenerator lig = new LineItemGenerator(this.random, pg, Enums.ProductType.values());
        OrderGenerator og = new OrderGenerator(this.random, lig, cg.generateClient());
        ou.updateOrder(og.generateOrder());
    }
}