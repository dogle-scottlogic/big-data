/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scottlogic.cassandravsmariadb.dao;

import com.scottlogic.cassandravsmariadb.entities.Client;
import java.util.List;
 
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 *
 * @author dogle
 */
@Repository("clientDao")
public class ClientDaoImpl extends AbstractDao implements ClientDao{
    
    @Override
    public void saveClient(Client client){
        persist(client);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Client> findAllClients() {
        Criteria criteria = getSession().createCriteria(Client.class);
        return (List<Client>) criteria.list(); 
    }
     
    @Override
    public void deleteClientById(String ssn) {
        Query query = getSession().createSQLQuery("delete from Employee where ssn = :ssn");
        query.setString("ssn", ssn);
        query.executeUpdate();
    }
 
     
    @Override
    public Client findById(String id){
        Criteria criteria = getSession().createCriteria(Client.class);
        criteria.add(Restrictions.eq("id",id));
        return (Client) criteria.uniqueResult();
    }
     
    @Override
    public void updateClient(Client client){
        getSession().update(client);
    }
}
