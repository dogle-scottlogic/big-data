/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bigdatabenchproject2;

import entities.Client;
import entities.LineItem;
import entities.Order;
import entities.Product;
import generators.ClientGenerator;
import generators.LineItemGenerator;
import generators.OrderGenerator;
import generators.ProductGenerator;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author lcollingwood
 */
public class BigDataBenchProject2 {
   
  
    
    public static void main(String[] args) {
        Random random = new Random(12345/* Seed Here*/);
        
        int nClients = 11;
        int nProducts = 11;
        int nOrderLimit = 11;
        
        ClientGenerator clientGenerator;
        ProductGenerator productGenerator;
        LineItemGenerator lineItemGenerator;
        
        ArrayList<Client> clients = new ArrayList<>();
        ArrayList<Product> products = new ArrayList<>();
        ArrayList<Order> orders = new ArrayList<>();
        
        clientGenerator = new ClientGenerator(random);
        clients.addAll(clientGenerator.getClients(nClients));
        
        productGenerator = new ProductGenerator(random);
        products.addAll(productGenerator.generateProducts(nProducts));
        
        lineItemGenerator = new LineItemGenerator(random, products);
        
        
        // For Each Client, generate orders.
        clients.forEach(client -> {
            OrderGenerator orderGenerator = new OrderGenerator(random, lineItemGenerator, client);
            orders.addAll(orderGenerator.generateOrders(nOrderLimit));
        });
    }

}
