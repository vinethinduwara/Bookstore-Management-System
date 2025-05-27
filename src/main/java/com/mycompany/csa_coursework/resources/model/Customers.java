package com.mycompany.csa_coursework.resources.model;

import java.util.UUID;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Dell
 */
public class Customers {
    private String id;
    private String name;
    private String email;
    private String password;
    private static int idCounter;

    
    public Customers() {
    }

    public Customers(String name, String email, String password) {
        this.id = generateNextId();
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
        private static synchronized String generateNextId() {
        return String.valueOf(++idCounter);
    }

    
    public void assignSequentialId() {
        if (this.id == null) {
            this.id = generateNextId();
        }
    }

    @Override
    public String toString() {
        return "Customer{id='" + id + "', name='" + name + "', email='" + email + "', password='[protected]'}";
    }
}
