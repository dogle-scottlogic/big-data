/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scottlogic.cassandravsmariadb.services;

import com.scottlogic.cassandravsmariadb.entities.LineItem;
import java.util.List;

/**
 *
 * @author dogle
 */
public interface LineItemService {
    
    void saveLineItem(LineItem lineItem);
    
    List<LineItem> findAllLineItems();
    
    void deleteLineItemById(String id);
    
    LineItem findById(String id);

    void updateLineItem(LineItem lineItem);
}
