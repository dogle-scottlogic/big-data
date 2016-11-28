/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

    public Order(UUID id, ArrayList<LineItem> lineItems, Client client, double subTotal) {
        this.id = id;
        this.lineItems = lineItems;
        this.client = client;
        this.subTotal = subTotal; // Might want to sum this from line items
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

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }
}
