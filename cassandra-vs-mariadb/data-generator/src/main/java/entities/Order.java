package entities;

import enums.Enums.OrderStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * @author lcollingwood
 */
public class Order {
    private String id;
    private ArrayList<LineItem> lineItems;
    private Client client;
    private Date created;
    private OrderStatus status;
    private double subTotal;

    public Order(UUID id, ArrayList<LineItem> lineItems, Client client, OrderStatus status) {
        this.id = id.toString();
        this.lineItems = lineItems;
        this.client = client;
        this.created = new Date();
        this.status = status;
    }

    public double getSubTotal() {
        double subTotal = 0;
        for (LineItem lineItem : lineItems) {
            subTotal = subTotal + lineItem.getLinePrice();
        }
        return subTotal;
    }

    public String getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id.toString();
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

    public Date getDate() {
        return this.created;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }
}
