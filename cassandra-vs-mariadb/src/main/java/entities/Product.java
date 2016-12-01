package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author lcollingwood
 */
public abstract class Product {
    private String id;
    private String name;
    private double weight;
    private double price;

    public Product(
        UUID id, 
        String name, 
        double price,
        double weight
    ) {
        this.id = id.toString();
        this.name = name;
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
