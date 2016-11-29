/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scottlogic.cassandravsmariadb.dao;

import com.scottlogic.cassandravsmariadb.entities.LineItem;
import java.util.List;
 
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 *
 * @author dogle
 */
@Repository("lineItemDao")
public class LineItemDaoImpl extends AbstractDao implements LineItemDao{
    
    @Override
    public void saveLineItem(LineItem lineItem) {
        persist(lineItem);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<LineItem> findAllLineItems() {
        Criteria criteria = getSession().createCriteria(LineItem.class);
        return (List<LineItem>) criteria.list();
    }
    
    @Override
    public void deleteLineItemById(String id) {
        Query query = getSession().createSQLQuery("delete from LineItem where id = :id");
        query.setString("id", id);
        query.executeUpdate();
    }
    
    @Override
    public LineItem findById(String id){
        Criteria criteria = getSession().createCriteria(LineItem.class);
        criteria.add(Restrictions.eq("id",id));
        return (LineItem) criteria.uniqueResult();
    }
     
    @Override
    public void updateLineItem(LineItem lineItem){
        getSession().update(lineItem);
    }
}
