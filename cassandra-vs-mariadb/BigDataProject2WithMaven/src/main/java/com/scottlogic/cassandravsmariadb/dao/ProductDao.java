/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scottlogic.cassandravsmariadb.dao;

import com.scottlogic.cassandravsmariadb.entities.Product;
import java.util.List;

/**
 *
 * @author dogle
 */
public interface ProductDao {
    void saveProduct(Product product);
    
    List<Product> findAllProducts();
    
    void deleteProductById(String id);
    
    Product findById(String id);

    void updateProduct(Product product);
}

