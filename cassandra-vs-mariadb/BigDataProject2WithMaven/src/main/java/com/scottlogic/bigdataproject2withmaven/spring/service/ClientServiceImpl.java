/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scottlogic.bigdataproject2withmaven.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scottlogic.bigdataproject2withmaven.spring.dao.ClientDao;

import entities.Client;

/**
 *
 * @author dogle
 */
@Service("clientService")
@Transactional
public class ClientServiceImpl implements ClientService{
    
    @Autowired
    private ClientDao dao;
    
    @Override
    public void saveClient(Client client){
        dao.saveClient(client);
    }
    
    @Override
    public List<Client> findAllClients() {
        return dao.findAllClients();
    }
     
    @Override
    public void deleteClientById(String id) {
        dao.deleteClientById(id);
    }
 
    @Override
    public Client findById(String id){
        return dao.findById(id);
    }
     
    @Override
    public void updateClient(Client client){
        dao.updateClient(client);
    }
}
