/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bigdatabenchproject2;

import entities.LineItem;
import entities.Product;
import generators.ClientGenerator;
import generators.LineItemGenerator;
import generators.ProductGenerator;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author lcollingwood
 */
public class BigDataBenchProject2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Random random = new Random(2134/* Seed Here*/);
//        ClientGenerator clientGenerator = new ClientGenerator(random);
//        ArrayList<Client> clients = clientGenerator.getClients(11);
//        clients.forEach((client) ->  System.out.println(client.getName()));
        ProductGenerator pg = new ProductGenerator(random);
        ArrayList<Product> products = pg.generateProducts(11);
        products.forEach((product) -> System.out.println(product.getId()));
        LineItemGenerator lg = new LineItemGenerator(random, products);
        ArrayList<LineItem> lineItems = lg.generateLineItems(11);
        lineItems.forEach(lineItem -> lineItem.display());
    }

}
