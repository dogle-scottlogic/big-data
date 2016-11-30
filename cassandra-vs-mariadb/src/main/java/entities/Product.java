package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author lcollingwood
 */
public class Product {
    private String id;
    private String name;
    private List<String> availableColours;
    private List<String> availableSizes;
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
        this.id = id.toString();
        this.name = name;
        this.availableColours = availableColours;
        this.availableSizes = availableSizes;
        this.weight = weight;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
  
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getColors() {
        return availableColours;
    }

    public void setColors(List<String> colors) {
        this.availableColours = colors;
    }

    public List<String> getSizes() {
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
