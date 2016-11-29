/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scottlogic.bigdataproject2withmaven.spring.service;

import entities.Client;
import java.util.List;

/**
 *
 * @author dogle
 */
public interface ClientService {
    void saveClient(Client client);
    
    List<Client> findAllClients();
    
    void deleteClientById(String id);
    
    Client findById(String id);

    void updateClient(Client client);
}
