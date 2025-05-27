/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_coursework.resources.model;





import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class CartItem {
    
    private String bookId; 
    private int quantity;

    public CartItem() {
    }

    public CartItem(String bookId, int quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    // Getters and Setters
    @JsonProperty("bookId")
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    @JsonProperty("quantity")
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    

    @Override
    public String toString() {
        return "CartItem{bookId='" + bookId + "', quantity=" + quantity + "}";
    }

}
