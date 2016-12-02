package Updaters;

import entities.products.Hat;
import entities.LineItem;
import enums.Enums;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

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
        HatLineItemUpdater hlu = new HatLineItemUpdater(this.random);

        ArrayList<String> availableColours = new ArrayList<String>();
        ArrayList<String> availableSizes = new ArrayList<String>();
        availableColours.add("Red");
        availableColours.add("Blue");
        availableColours.add("Yellow");
        availableSizes.add("L");
        availableSizes.add("S");
        availableSizes.add("M");

        Hat testHat = new Hat(UUID.randomUUID(), Enums.ProductType.HAT, "testHat", availableColours, availableSizes, 12.0, 12.0);
        LineItem testLineItem = new LineItem(UUID.randomUUID(), testHat, 12, "Red", "S");

        LineItem newLineItem = hlu.updateRandomLineItemField(testLineItem);
        boolean changed = false;
        if(!newLineItem.getColor().equals("Red")) changed = true;
        if(!newLineItem.getSize().equals("S")) changed = true;
        if(newLineItem.getQuantity() != 12) changed = true;

        assertTrue(changed);
    }

    @Test
    public void getUpdatableFields() throws Exception {
        HatLineItemUpdater hlu = new HatLineItemUpdater(this.random);

        ArrayList<String> availableColours = new ArrayList<String>();
        ArrayList<String> availableSizes = new ArrayList<String>();
        availableColours.add("Red");
        availableColours.add("Blue");
        availableSizes.add("S");
        availableSizes.add("L");

        Hat testHat = new Hat(UUID.randomUUID(), Enums.ProductType.HAT, "testHat", availableColours, availableSizes, 12.0, 12.0);
        LineItem testLineItem = new LineItem(UUID.randomUUID(), testHat, 12, "Red", "S");

        ArrayList<Field> fields = hlu.getUpdatableFields(testLineItem.getClass().getDeclaredFields());
        boolean valid = true;
        for (Field field: fields) {
            if(field.getName().equals("product") || field.getName().equals("id") || field.getName().equals("linePrice")) {
                valid = false;
            }
        }
        assertTrue(valid);
    }

}