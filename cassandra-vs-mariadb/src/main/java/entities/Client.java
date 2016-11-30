package entities;

import java.util.UUID;

/**
 *
 * @author lcollingwood
 */
public class Client {
    private String id;
    private String name;
    private String address;
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