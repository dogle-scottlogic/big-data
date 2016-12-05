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
public class ProductUpdater {
    private Random random;
    private DataGenerator dataGenerator;
    private HashMap<String, ArrayList<String>> productData;

    public ProductUpdater(Random random) {
        this.random = random;
        this.dataGenerator = new DataGenerator(random);
        this.productData = null;
    }

    public Product updateRandomProduct(Product originalProduct) {
        ArrayList<Field> fields = getUpdatableFields(originalProduct.getClass().getDeclaredFields());
        int fieldIndex = random.nextInt(fields.size());
        // Update a random field in the product
        String fieldName = fields.get(fieldIndex).getName();

        switch (originalProduct.getProductType()){
            case HAT:
                this.productData = dataGenerator.getHatData();
                break;
        }

        if (fieldName.equals("name")) return updateProductName(originalProduct);
        if (fieldName.equals("weight")) return  updateProductWeight(originalProduct);
        if (fieldName.equals("price")) return  updateProductPrice(originalProduct);
        return originalProduct;
    }

    private Product updateProductPrice(Product originalProduct) {
        double price = dataGenerator.generatePriceWeight(1, 100);
        originalProduct.setPrice(price);
        return originalProduct;
    }

    private Product updateProductWeight(Product originalProduct) {
        double weight = dataGenerator.generatePriceWeight(1, 1000);
        originalProduct.setPrice(weight);
        return originalProduct;
    }

    private Product updateProductName(Product originalProduct) {
        String name = productData.get("name").get(0);
        originalProduct.setName(name);
        return originalProduct;
    }

    public ArrayList<Field> getUpdatableFields(Field[] declaredFields) {
        ArrayList<Field> trimmedFields = new ArrayList<Field>();
        for (Field field : declaredFields) {
            if (!field.getName().equals("productType") && !field.getName().equals("id")) {
                trimmedFields.add(field);
            }
        }
        return trimmedFields;
    }
}
