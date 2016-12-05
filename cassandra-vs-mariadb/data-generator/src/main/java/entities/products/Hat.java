package entities.products;

import entities.Product;
import enums.Enums.ProductType;

import java.util.List;
import java.util.UUID;

/**
 * Created by dogle on 01/12/2016.
 */
public class Hat extends Product {

    private String colour;
    private String size;

    public Hat(
            UUID id,
            ProductType productType,
            String name,
            String colour,
            String size,
            double weight,
            double price
            ) {
        super(id, productType, name, weight, price);
        this.colour = colour;
        this.size = size;
    }

    public String getColour() {
        return this.colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
