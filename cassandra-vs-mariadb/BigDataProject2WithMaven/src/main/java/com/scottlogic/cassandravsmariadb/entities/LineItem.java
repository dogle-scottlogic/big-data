package com.scottlogic.cassandravsmariadb.entities;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author lcollingwood
 */

@Entity
@Table(name="LINE_ITEM")
public class LineItem {
    @Id
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Product", nullable = false)
    private Product product;
    
    @Column(name="QUANTITY", nullable=false)
    private int quantity;
    
    @Column(name = "COLOUR", nullable=false)
    private String colour;
    
    @Column(name= "SIZE", nullable=false)
    private String size;
    
    @Column(name="LINE_PRICE", nullable=false)
    private double linePrice;

    public LineItem(
        UUID id, 
        Product product, 
        int quantity, 
        String color, 
        String size
    ) {
        this.id = id.toString();
        this.product = product;
        this.quantity = quantity;
        this.colour = color;
        this.size = size;
        this.linePrice = this.quantity * this.product.getPrice();
    }
    
    public double getLinePrice() {
        return this.quantity * this.product.getPrice();
    }
    
    public void display() {
        System.out.println(
            "Line Item(" + this.id + "): " + this.quantity + "x " + this.size 
            + " " + this.colour + " "+ this.product.getName()
        );
    }
      
    public String getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id.toString();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.linePrice = this.quantity * this.product.getPrice();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.linePrice = this.quantity * this.product.getPrice();
    }

    public String getColor() {
        return colour;
    }

    public void setColor(String color) {
        this.colour = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
