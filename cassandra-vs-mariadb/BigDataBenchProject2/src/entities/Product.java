package entities;

import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author lcollingwood
 */
public class Product {
    private UUID id; 
    private String name;
    private ArrayList<String> availableColours; 
    private ArrayList<String> availableSizes;
    private double weight;
    private double price;

    public Product(
        UUID id, 
        String name, 
        double price,
        double weight, 
        ArrayList<String> availableColours, 
        ArrayList<String> availableSizes
    ) {
        this.id = id;
        this.name = name;
        this.availableColours = availableColours;
        this.availableSizes = availableSizes;
        this.weight = weight;
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getColors() {
        return availableColours;
    }

    public void setColors(ArrayList<String> colors) {
        this.availableColours = colors;
    }

    public ArrayList<String> getSizes() {
        return availableSizes;
    }

    public void setSizes(ArrayList<String> sizes) {
        this.availableSizes = sizes;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
