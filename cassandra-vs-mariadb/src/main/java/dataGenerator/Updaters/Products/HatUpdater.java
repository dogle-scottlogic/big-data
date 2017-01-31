package dataGenerator.Updaters.Products;

import dataGenerator.Updaters.ProductUpdater;
import dataGenerator.entities.Product;
import dataGenerator.entities.products.Hat;
import dataGenerator.generators.DataGenerator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by dogle on 05/12/2016.
 */
public class HatUpdater extends ProductUpdater {

    private DataGenerator dataGenerator;
    private HashMap<String, String> productData;

    public HatUpdater(Random random) {
        super(random);
        this.random = random;
        this.dataGenerator = new DataGenerator(random);
        this.productData = dataGenerator.getHatData();
    }
    public Product updateRandomProduct(Product originalProduct) {
        ArrayList<Field> fields = getUpdatableFields(originalProduct.getClass().getDeclaredFields());
        int fieldIndex = random.nextInt(fields.size());
        // Update a random field in the hat
        String fieldName = fields.get(fieldIndex).getName();
        if (fieldName.equals("name")) return updateProductName(originalProduct);
        if (fieldName.equals("weight")) return  updateProductWeight(originalProduct);
        if (fieldName.equals("price")) return  updateProductPrice(originalProduct);
        if (fieldName.equals("colour")) return updateProductColour((Hat)originalProduct);
        if (fieldName.equals("size")) return updateProductSize((Hat)originalProduct);
        return originalProduct;
    }

    public Product updateProductPrice(Product originalProduct) {
        double price = dataGenerator.generatePriceWeight(1, 100);
        originalProduct.setPrice(price);
        return originalProduct;
    }

    public Product updateProductWeight(Product originalProduct) {
        double weight = dataGenerator.generatePriceWeight(1, 1000);
        originalProduct.setPrice(weight);
        return originalProduct;
    }

    public Product updateProductName(Product originalProduct) {
        String name = productData.get("name");
        originalProduct.setName(name);
        return originalProduct;
    }

    private Product updateProductColour(Hat originalProduct) {
        String colour = productData.get("colour");
        originalProduct.setColour(colour);
        return originalProduct;
    }

    private Product updateProductSize(Hat originalProduct) {
        String size = productData.get("size");
        originalProduct.setSize(size);
        return originalProduct;
    }

    public ArrayList<Field> getUpdatableFields(Field[] declaredFields) {
        ArrayList<Field> trimmedFields = new ArrayList<>();
        for (Field field : declaredFields) {
            if (!field.getName().equals("productType") && !field.getName().equals("id")) {
                trimmedFields.add(field);
            }
        }
        return trimmedFields;
    }
}
