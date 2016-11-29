/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generators;

import com.scottlogic.cassandravsmariadb.entities.Product;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author lcollingwood
 */
public class ProductGenerator {

    private Random random;
    
    private final String[] productNames = {
        "Beret", "Fedora", "Beanie", "Cap", "Pork Pie", "Stetson", "Panama",
        "Fez", "Deer Stalker", "Cloche", "Sombrero", "Fruit", "Flat", "Bowler",
        "Boonie", "Bobble"
    };
    
    private final String[] productColours = {
        "Cerulean", "Mauve", "Marroon", "Burgundy", "Burnt Sienna", "Salmon", 
        "Coral", "Puce", "Racing Green", "Fandango", "Fushcia", "Marigold", 
        "Prussian Blue", "Schwartz", "Aubergine", "Teal"
    };
    
    private final String[] productSizes = {
        "XXS", "XS", "S", "M", "L", "XL", "XXL"
    };    
    
    public ProductGenerator(Random random) {
        this.random = random;
    }

    public Product generateProduct() {
        UUID id = UUID.randomUUID();
        String name = this.generateName();
        double price = this.generatePrice();
        double weight = this.generateWeight();
        ArrayList<String> availableColours = this.generateAvailableColours();
        ArrayList<String> availableSizes = this.generateAvailableSizes();

        Product product = new Product(
                id, name, price, weight, availableColours, availableSizes
        );
        return product;
    }

    private String generateName() {
        return productNames[this.random.nextInt(productNames.length)];
    }

    private double generatePrice() {
        int pennies = this.random.nextInt(9001) + 99;
        return pennies / 100;
    }

    private double generateWeight() {
        return this.random.nextInt(1000);
    }

    private ArrayList<String> pickFromList(int n, String[] list) {
        HashSet<String> picks = new HashSet();
        for (int i = 0; i < n; i++) {
            picks.add(list[this.random.nextInt(list.length)]);
        }
        return new ArrayList(picks);
    }
    
    private ArrayList<String> generateAvailableColours(){
        return this.pickFromList(
            this.random.nextInt(4) + 1, this.productColours
        );
    }

    private ArrayList<String> generateAvailableSizes() {
        return this.pickFromList(
            this.random.nextInt(4) + 1, this.productSizes
        );
    }

    public ArrayList<Product> generateProducts(int n) {
        ArrayList<Product> generatedProducts = new ArrayList();
        for (int i = 0; i < n; i++) {
            generatedProducts.add(this.generateProduct());
        }
        return generatedProducts;
    }

}
