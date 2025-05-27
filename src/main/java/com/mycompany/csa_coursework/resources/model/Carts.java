/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_coursework.resources.model;

import java.util.ArrayList;
import java.util.List;

public class Carts {
    private String customerId;
    private List<CartItem> items;

    public Carts(String customerId) {
        this.customerId = customerId;
        this.items = new ArrayList<>();
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

    @Override
    public String toString() {
        return "Cart{customerId='" + customerId + "', items=" + items + "}";
    }
}
