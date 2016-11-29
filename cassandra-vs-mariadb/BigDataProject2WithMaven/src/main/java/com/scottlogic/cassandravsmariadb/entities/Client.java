package com.scottlogic.cassandravsmariadb.entities;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author lcollingwood
 */
@Entity
@Table(name="CLIENT")
public class Client {
    @Id
    private String id;
    
    @Column(name="NAME", nullable=false)
    private String name;
    
    @Column(name = "ADDRESS", nullable=false)
    private String address;
    
    @Column(name = "EMAIL", nullable=true)
    private String email;
    
    public Client(UUID id, String name, String address, String email) {
        this.id = id.toString();
        this.name = name;
        this.address = address;
        this.email = email;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
