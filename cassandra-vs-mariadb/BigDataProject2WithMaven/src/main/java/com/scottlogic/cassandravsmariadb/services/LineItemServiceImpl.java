/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scottlogic.cassandravsmariadb.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scottlogic.cassandravsmariadb.dao.LineItemDao;

import com.scottlogic.cassandravsmariadb.entities.LineItem;


/**
 *
 * @author dogle
 */
@Service("lineItemService")
@Transactional
public class LineItemServiceImpl implements LineItemService{
    @Autowired
    private LineItemDao dao;
    
    @Override
    public void saveLineItem(LineItem lineItem){
        dao.saveLineItem(lineItem);
    }
    
    @Override
    public List<LineItem> findAllLineItems() {
        return dao.findAllLineItems();
    }
     
    @Override
    public void deleteLineItemById(String id) {
        dao.deleteLineItemById(id);
    }
 
    @Override
    public LineItem findById(String id){
        return dao.findById(id);
    }
     
    @Override
    public void updateLineItem(LineItem lineItem){
        dao.updateLineItem(lineItem);
    }
}
