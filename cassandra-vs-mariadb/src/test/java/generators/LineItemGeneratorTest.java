package generators;

import org.junit.*;

import java.util.ArrayList;
import java.util.Random;

import entities.LineItem;

import static org.junit.Assert.*;

/**
 * Created by dogle on 30/11/2016.
 */
public class LineItemGeneratorTest {
    public LineItemGeneratorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of generateLineItems method, of class LineItemGenerator.
     */
    @Test
    public void testGenerateLineItems() {
        System.out.println("generateLineItems - With Random Seed");
        Random random = new Random(1234);
        ProductGenerator pg = new ProductGenerator(random);
        LineItemGenerator lg = new LineItemGenerator(random, pg, 5);
        ArrayList<LineItem> liList = lg.generateLineItems(5);
        assertEquals(liList.size(), 5);

        random = new Random(1234);
        pg = new ProductGenerator(random);
        lg = new LineItemGenerator(random, pg,5);
        ArrayList<LineItem> liList2 = lg.generateLineItems(5);
        for(int i = 0; i < liList.size(); i++) {
            assertEquals(liList.get(i).getColor(), liList2.get(i).getColor());
        }
    }

    /**
     * Test of generateLineItems method, of class LineItemGenrator - no items.
     */
    @Test
    public void testGenerateLineItemsNoItem() {
        System.out.println("generateLineItems - With Random Seed - Empty List");
        Random random = new Random(1234);
        ProductGenerator pg = new ProductGenerator(random);
        LineItemGenerator lg = new LineItemGenerator(random, pg,5);
        ArrayList<LineItem> liList = lg.generateLineItems(0);
        assertEquals(liList.size(), 0);
    }

    /**
     * Test of generateLineItem method, of class LineItemGenerator.
     */
    @Test
    public void testGenerateLineItem() {
        System.out.println("generateLineItem -With Random Seed");
        Random random = new Random(1234);
        ProductGenerator pg = new ProductGenerator(random);
        LineItemGenerator cg = new LineItemGenerator(random, pg, 5);
        LineItem testLI = cg.generateLineItem();

        assertNotNull(testLI.getProduct());
        assertNotNull(testLI.getColor());
        assertNotNull(testLI.getQuantity());
        assertNotNull(testLI.getId());
        assertNotNull(testLI.getSize());
    }
}