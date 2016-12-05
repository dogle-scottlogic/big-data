package entities;

import java.util.UUID;

/**
 *
 * @author lcollingwood
 */

public class LineItem {
    private String id;
    private Product product;
    private int quantity;
    private String colour;
    private String size;
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
