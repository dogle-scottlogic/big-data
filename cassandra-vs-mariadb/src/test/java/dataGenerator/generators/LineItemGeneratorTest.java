package dataGenerator.generators;

import dataGenerator.entities.LineItem;
import dataGenerator.enums.Enums;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by dogle on 30/11/2016.
 */
public class LineItemGeneratorTest {
    private final static Logger LOG = Logger.getLogger(LineItemGeneratorTest.class);

    private Enums.ProductType[] productList = {Enums.ProductType.HAT};

    /**
     * Test of generateLineItems method, of class LineItemGenerator.
     */
    @Test
    public void testGenerateLineItems() {
        LOG.info("generateLineItems - With Random Seed");
        Random random = new Random(1234);
        ProductGenerator pg = new ProductGenerator(random);
        LineItemGenerator lg = new LineItemGenerator(random, pg, this.productList);
        ArrayList<LineItem> liList = lg.generateLineItems(5);
        assertEquals(liList.size(), 5);

        random = new Random(1234);
        pg = new ProductGenerator(random);
        lg = new LineItemGenerator(random, pg, this.productList);
        ArrayList<LineItem> liList2 = lg.generateLineItems(5);
        for(int i = 0; i < liList.size(); i++) {
            assertEquals(liList.get(i).getQuantity(), liList2.get(i).getQuantity());
        }
    }

    /**
     * Test of generateLineItems method, of class LineItemGenrator - no items.
     */
    @Test
    public void testGenerateLineItemsNoItem() {
        LOG.info("generateLineItems - With Random Seed - Empty List");
        Random random = new Random(1234);
        ProductGenerator pg = new ProductGenerator(random);
        LineItemGenerator lg = new LineItemGenerator(random, pg, this.productList);
        ArrayList<LineItem> liList = lg.generateLineItems(0);
        assertEquals(liList.size(), 0);
    }

    /**
     * Test of generateLineItem method, of class LineItemGenerator.
     */
    @Test
    public void testGenerateLineItemHat() {
        LOG.info("generateLineItem -With Random Seed-Hat");
        Random random = new Random(1234);
        ProductGenerator pg = new ProductGenerator(random);
        LineItemGenerator cg = new LineItemGenerator(random, pg, this.productList);
        LineItem testLI = cg.generateLineItem(Enums.ProductType.HAT);

        assertNotNull(testLI.getProduct());
        assertNotNull(testLI.getQuantity());
        assertNotNull(testLI.getId());
    }
}