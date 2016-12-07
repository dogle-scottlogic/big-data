/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataGenerator.generators;

import dataGenerator.data_handlers.Settings;
import dataGenerator.entities.products.Hat;
import dataGenerator.entities.LineItem;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import dataGenerator.enums.Enums.ProductType;

/**
 *
 * @author lcollingwood
 */
public class LineItemGenerator {

    private final Random random;
    private final ProductGenerator productGenerator;
    private final ProductType[] productList;
    private int maxQuantity;

    public LineItemGenerator(Random random, ProductGenerator productGenerator, ProductType[] productList) {
        this.random = random;
        this.productGenerator = productGenerator;
        this.productList = productList;
        this.maxQuantity = Settings.getIntSetting("MAX_PRODUCTS");
    }

    public ArrayList<LineItem> generateLineItems(int num) {
        ArrayList<LineItem> items = new ArrayList();
        for(int i = 0; i < num; i++) {
            //get a random product
            int productIndex = random.nextInt(productList.length);
            ProductType randomProduct = productList[productIndex];
            items.add(generateLineItem(randomProduct));
        }
        return items;
    }
    
    public LineItem generateLineItem(ProductType productType) {
        UUID id = UUID.randomUUID();
        switch (productType)
        {
            case HAT:
                Hat hat = (Hat)productGenerator.generateProduct(ProductType.HAT);
                return  getHatLineItem(id, hat);
        }
        return null;
    }
    
    private LineItem getHatLineItem(UUID id, Hat hat) {
        int quantity = random.nextInt(this.maxQuantity) + 1;
        LineItem lineItem = new LineItem(id, hat, quantity);
        return lineItem;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

}
