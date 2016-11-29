package com.scottlogic.cassandravsmariadb.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 *
 * @author lcollingwood
 */
@Entity
@Table(name="PRODUCT")

public class Product {
    @Id
    private String id; 
    
    @Column(name="NAME", nullable=false)
    private String name;
    
    @ElementCollection
//    @CollectionTable(name="AVAILABLE_COLOURS", joinColumns=@JoinColumn(name="id"))
    @Column(name = "COLOUR")
    private List<String> availableColours;
    
    @ElementCollection
//    @CollectionTable(name="AVAILABLE_SIZES", joinColumns=@JoinColumn(name="id"))
    @Column(name = "SIZE")
    private List<String> availableSizes;
    
    @Column(name="WEIGHT", nullable=false)
    private double weight;
        
    @Column(name="PRICE", nullable=false)
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
