package dataGenerator.Updaters;

import dataGenerator.entities.LineItem;
import dataGenerator.entities.products.Hat;
import dataGenerator.enums.Enums;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * Created by dogle on 02/12/2016.
 */
public class HatLineItemUpdaterTest {

    private Random random;

    @Before
    public void setUp() throws Exception {
        this.random = new Random(1234);
    }

    @Test
    public void updateRandomLineItemField() throws Exception {
        LineItemUpdater lu = new LineItemUpdater(this.random);

        Hat testHat = new Hat(UUID.randomUUID(), Enums.ProductType.HAT, "testHat", "Red", "L", 12.0, 12.0);
        LineItem testLineItem = new LineItem(UUID.randomUUID(), testHat, 12);

        LineItem newLineItem = lu.updateRandomLineItemField(testLineItem);
        boolean changed = false;
        if(newLineItem.getQuantity() != 12) changed = true;

        assertTrue(changed);
    }

    @Test
    public void getUpdatableFields() throws Exception {
        LineItemUpdater lu = new LineItemUpdater(this.random);

        Hat testHat = new Hat(UUID.randomUUID(), Enums.ProductType.HAT, "testHat", "Red", "L", 12.0, 12.0);
        LineItem testLineItem = new LineItem(UUID.randomUUID(), testHat, 12);

        ArrayList<Field> fields = lu.getUpdatableFields(testLineItem.getClass().getDeclaredFields());
        boolean valid = true;
        for (Field field: fields) {
            if(field.getName().equals("product") || field.getName().equals("id") || field.getName().equals("linePrice")) {
                valid = false;
            }
        }
        assertTrue(valid);
    }
}