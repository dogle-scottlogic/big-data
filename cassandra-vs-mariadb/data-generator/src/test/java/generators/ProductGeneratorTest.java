package generators;

import entities.products.Hat;
import enums.Enums;
import org.junit.*;

import java.util.ArrayList;
import java.util.Random;

import entities.Product;

import static org.junit.Assert.*;

/**
 * Created by dogle on 30/11/2016.
 */
public class ProductGeneratorTest {
    public ProductGeneratorTest() {
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
     * Test of generateProduct method, of class ProductGenerator.
     */
    @Test
    public void testGenerateProducts() {
        System.out.println("generateProducts - With random seed");
        Random random = new Random(1234);
        ProductGenerator pg = new ProductGenerator(random);
        ArrayList<Product> productList = pg.generateProducts(5, Enums.ProductType.HAT);
        assertEquals(productList.size(), 5);

        random = new Random(1234);
        pg = new ProductGenerator(random);
        ArrayList<Product> productList2 = pg.generateProducts(5, Enums.ProductType.HAT);
        for (int i = 0; i < productList.size(); i++) {
            assertEquals(productList.get(i).getName(), productList2.get(i).getName());
        }
    }

    /**
     * Test of generateProduct method, of class ProductGenerator - no items.
     */
    @Test
    public void testGenerateProductsNoItems() {
        System.out.println("generateProducts - With Random Seed - Empty List");
        Random random = new Random(1234);
        ProductGenerator pg = new ProductGenerator(random);
        ArrayList<Product> productList = pg.generateProducts(0, Enums.ProductType.HAT);
        assertEquals(productList.size(), 0);
    }

    /**
     * Test of generateProducts method, of class ProductGenerator.
     */
    @Test
    public void testGenerateProductHat() {
        System.out.println("generateProduct - With random seed - HAT");
        Random random = new Random(1234);
        ProductGenerator pg = new ProductGenerator(random);
        Hat testHat = (Hat)pg.generateProduct(Enums.ProductType.HAT);

        assertNotNull(testHat.getName());
        assertNotNull(testHat.getColour());
        assertNotNull(testHat.getId());
        assertNotNull(testHat.getPrice());
        assertNotNull(testHat.getSize());
        assertNotNull(testHat.getWeight());
    }
}