/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scottlogic.cassandravsmariadb.dao;

import com.scottlogic.cassandravsmariadb.entities.Product;
import java.util.List;
 
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 *
 * @author dogle
 */
@Repository("productDao")
public class ProductDaoImpl extends AbstractDao implements ProductDao{
    
    @Override
    public void saveProduct(Product product){
        persist(product);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Product> findAllProducts() {
        Criteria criteria = getSession().createCriteria(Product.class);
        return (List<Product>) criteria.list(); 
    }
     
    @Override
    public void deleteProductById(String ssn) {
        Query query = getSession().createSQLQuery("delete from Employee where ssn = :ssn");
        query.setString("ssn", ssn);
        query.executeUpdate();
    }
 
     
    @Override
    public Product findById(String id){
        Criteria criteria = getSession().createCriteria(Product.class);
        criteria.add(Restrictions.eq("id",id));
        return (Product) criteria.uniqueResult();
    }
     
    @Override
    public void updateProduct(Product product){
        getSession().update(product);
    }
}
