/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generators;

import entities.LineItem;
import entities.Product;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author lcollingwood
 */
public class LineItemGenerator {

    private final Random random;
    private final ArrayList<Product> products;

    public LineItemGenerator(Random random, ArrayList<Product> products) {
        this.random = random;
        this.products = products;
    }

    public ArrayList<LineItem> generateLineItems(int num) {
        ArrayList<LineItem> items = new ArrayList();
        for(int i = 0; i < num; i++) {
            items.add(generateLineItem());
        }
        return items;
    }
    
    public LineItem generateLineItem() {
        UUID id = UUID.randomUUID();
        Product product = getProduct();
        int quantity = random.nextInt(10) + 1;
        int colourIndex = random.nextInt(product.getColors().size());
        String colour = product.getColors().get(colourIndex);
        int sizeIndex = random.nextInt(product.getSizes().size());
        String size = product.getSizes().get(sizeIndex);
        LineItem lineItem = new LineItem(id, product, quantity, colour, size);
        return lineItem;
    }
    
    private Product getProduct() {
       int productIndex = random.nextInt(this.products.size());
       return this.products.get(productIndex);
    }

}
