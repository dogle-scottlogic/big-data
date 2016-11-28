/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.util.UUID;

/**
 *
 * @author lcollingwood
 */
public class LineItem {
        private UUID id; // Should be seedable
        private Product product;
        private int quantity;
        private String color; // enum type
        private String size; //enum type

    public LineItem(UUID id, Product product, int quantity, String color, String size) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.color = color;
        this.size = size;
        /*
            Sub total field could be added here.
        */
    }
    
    public void display() {
        System.out.println("Line Item(" + this.id + "): " + this.quantity + "x " + this.size + " " + this.color + " "+ this.product.getName());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
        
}
