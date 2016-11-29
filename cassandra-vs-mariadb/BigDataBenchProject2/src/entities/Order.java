package entities;

import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author lcollingwood
 */
public class Order {
    private UUID id; // Should be seedable
    private ArrayList<LineItem> lineItems;
    private Client client;
    private double subTotal;

    public Order(UUID id, ArrayList<LineItem> lineItems, Client client) {
        this.id = id;
        this.lineItems = lineItems;
        this.client = client;
    }

    public double getSubTotal() {
        lineItems.forEach((lineItem) -> {
            this.subTotal = this.subTotal + lineItem.getLinePrice();
        });
        return this.subTotal;
    }
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ArrayList<LineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(ArrayList<LineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }
}
