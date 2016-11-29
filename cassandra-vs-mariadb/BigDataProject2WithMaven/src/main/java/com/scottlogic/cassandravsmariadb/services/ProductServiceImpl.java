/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scottlogic.cassandravsmariadb.services;

import com.scottlogic.cassandravsmariadb.dao.ProductDao;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scottlogic.cassandravsmariadb.entities.Product;

/**
 *
 * @author dogle
 */
@Service("productService")
@Transactional
public class ProductServiceImpl implements ProductService{
    
    @Autowired
    private ProductDao dao;
    
    @Override
    public void saveProduct(Product product){
        dao.saveProduct(product);
    }
    
    @Override
    public List<Product> findAllProducts() {
        return dao.findAllProducts();
    }
     
    @Override
    public void deleteProductById(String id) {
        dao.deleteProductById(id);
    }
 
    @Override
    public Product findById(String id){
        return dao.findById(id);
    }
     
    @Override
    public void updateProduct(Product product){
        dao.updateProduct(product);
    }
}
