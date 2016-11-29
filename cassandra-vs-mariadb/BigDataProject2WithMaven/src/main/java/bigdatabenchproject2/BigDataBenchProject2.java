/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bigdatabenchproject2;

import com.scottlogic.cassandravsmariadb.configuration.AppConfig;
import com.scottlogic.cassandravsmariadb.services.ClientService;
import com.scottlogic.cassandravsmariadb.entities.Client;
import com.scottlogic.cassandravsmariadb.entities.LineItem;
import com.scottlogic.cassandravsmariadb.entities.Order;
import com.scottlogic.cassandravsmariadb.entities.Product;
import com.scottlogic.cassandravsmariadb.services.LineItemService;
import com.scottlogic.cassandravsmariadb.services.ProductService;
import generators.ClientGenerator;
import generators.DataGenerator;
import generators.LineItemGenerator;
import generators.OrderGenerator;
import generators.ProductGenerator;
import java.util.ArrayList;
import java.util.Random;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 *
 * @author lcollingwood
 */
public class BigDataBenchProject2 {
    
    public static void main(String[] args) {
        
        DataGenerator dg = new DataGenerator();
        dg.populateRedis();
        
        
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
//        ClientService clientService = (ClientService) context.getBean("clientService");
        ProductService productService = (ProductService) context.getBean("productService");
        LineItemService lis = (LineItemService) context.getBean("lineItemService");

        int seed = 3423423;
        Random random = new Random(seed);
        
        int nClients = 11;
        int nProducts = 11;
        int nOrderLimit = 11;
        
        ClientGenerator clientGenerator;
        ProductGenerator productGenerator;
        LineItemGenerator lineItemGenerator;
        
        
        ArrayList<Client> clients = new ArrayList<>();
        ArrayList<Product> products = new ArrayList<>();
        ArrayList<Order> orders = new ArrayList<>();
        
//        clientGenerator = new ClientGenerator(random);
//        clients.addAll(clientGenerator.getClients(nClients));
        
        productGenerator = new ProductGenerator(random);
//        products.addAll(productGenerator.generateProducts(nProducts));
//             
//        products.forEach(product -> productService.saveProduct(product));
//        
//        lineItemGenerator = new LineItemGenerator(random, products);
//        ArrayList<LineItem> items = lineItemGenerator.generateLineItems(2);
//        items.forEach(item -> lis.saveLineItem(item));
        // For Each Client, generate orders.
//        clients.forEach(client -> {
//            OrderGenerator orderGenerator = new OrderGenerator(random, lineItemGenerator, client);
//            orders.addAll(orderGenerator.generateOrders(nOrderLimit));
//        });
    }

}
