package dataGenerator.Updaters.Products;

import dataGenerator.entities.products.Hat;
import dataGenerator.enums.Enums;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertNotEquals;

/**
 * Created by dogle on 05/12/2016.
 */
public class HatUpdaterTest {

    private Hat hat = new Hat(UUID.randomUUID(), Enums.ProductType.HAT, "TestHat", "Red", "L", 20.22, 43.44);
    private HatUpdater hatUpdater;

    @Before
    public void setUp() throws Exception {
        Random random = new Random(1234);
        this.hatUpdater = new HatUpdater(random);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void updateProductPrice() throws Exception {
        double originalPrice = hat.getPrice();
        hatUpdater.updateProductPrice(hat);
        assertNotEquals(originalPrice, hat.getPrice());
    }

    @Test
    public void updateProductWeight() throws Exception {
        double originalWeight = hat.getWeight();
        hatUpdater.updateProductWeight(hat);
        assertNotEquals(originalWeight, hat.getPrice());
    }

    @Test
    public void updateProductName() throws Exception {
        String originalName = hat.getName();
        hatUpdater.updateProductName(hat);
        assertNotEquals(originalName, hat.getName());
    }

    @Test
    public void getUpdatableFields() throws Exception {

    }

}