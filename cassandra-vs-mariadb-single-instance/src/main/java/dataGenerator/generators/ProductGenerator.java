/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataGenerator.generators;

import dataGenerator.data_handlers.Settings;
import dataGenerator.entities.products.Hat;
import dataGenerator.entities.Product;

import java.util.*;

import dataGenerator.enums.Enums.ProductType;

/**
 * @author lcollingwood
 */
public class ProductGenerator {

    private Random random;
    private DataGenerator dg;

    public ProductGenerator(Random random) {
        this.random = random;
        this.dg = new DataGenerator(this.random);
    }

    public ArrayList<Product> generateProducts(int n, ProductType productType) {
        ArrayList<Product> generatedProducts = new ArrayList();
        for (int i = 0; i < n; i++) {
            generatedProducts.add(this.generateProduct(productType));
        }
        return generatedProducts;
    }

    public Product generateProduct(ProductType productType) {
        UUID id = UUID.randomUUID();
        Product product = null;

        switch (productType) {
            case HAT:
                HashMap<String, String> hatData = dg.getHatData();
                String name = hatData.get("name");
                double price = dg.generatePriceWeight(Settings.getIntHatSetting("MIN_PRICE"), Settings.getIntHatSetting("MAX_PRICE"));
                double weight = dg.generatePriceWeight(Settings.getIntHatSetting("MIN_WEIGHT"), Settings.getIntHatSetting("MAX_WEIGHT"));
                String colour = hatData.get("colour");
                String size = hatData.get("size");
                product = new Hat(id, ProductType.HAT, name, colour, size, price, weight);
                break;
        }
        return product;
    }
}
