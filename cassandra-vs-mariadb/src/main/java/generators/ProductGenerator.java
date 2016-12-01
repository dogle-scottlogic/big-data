/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generators;

import entities.Hat;
import entities.Product;

import java.util.*;

import enums.Enums.ProductType;

/**
 *
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

        switch(productType) {
            case HAT:
                HashMap<String, ArrayList<String>> hatData = dg.getHatData();
                String name = hatData.get("name").get(0);
                double price = dg.generatePriceWeight(1, 100);
                double weight = dg.generatePriceWeight(1, 1000);
                ArrayList<String> availableColours = hatData.get("colours");
                ArrayList<String> availableSizes = hatData.get("sizes");
                product = new Hat(id, ProductType.HAT, name, availableColours, availableSizes, price, weight);
                break;
        }
        return product;
    }
}
