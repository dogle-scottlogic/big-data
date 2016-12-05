package Updaters;

import entities.Product;
import generators.DataGenerator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by dogle on 02/12/2016.
 */
public abstract class ProductUpdater {

    private Random random;

    public ProductUpdater(Random random) {
        this.random = random;
    }

    public abstract Product updateRandomProduct(Product originalProduct);

    public abstract Product updateProductPrice(Product originalProduct);

    public abstract Product updateProductWeight(Product originalProduct);

    public abstract Product updateProductName(Product originalProduct);

    public abstract ArrayList<Field> getUpdatableFields(Field[] declaredFields);
}
