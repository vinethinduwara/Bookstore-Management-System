/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_coursework.resources.model;


import java.util.ArrayList;
import java.util.List;


public class Order {
    private String id;
    private String customerId;
    private List<CartItem> items; 
    private static int idCounter;

    public Order(String customerId) {
        this.id = generateNextId();
        this.customerId = customerId;
        this.items = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
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
        return "Order{id='" + id + "', customerId='" + customerId + "', items=" + items + "}";
    }
}
