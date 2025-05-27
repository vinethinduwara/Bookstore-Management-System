/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_coursework.resources.model;



import java.util.UUID;

public class Books {
    private String id;
    private String title;
    private String authorId;
    private String isbn;
    private int publicationYear;
    private double price;
    private int stock;
    private static int idCounter = 0;

    // Default constructor for JSON deserialization
    public Books() {
    }

    public Books(String title, String authorId, String isbn, int publicationYear, double price, int stock) {
        this.id = generateNextId();
        this.title = title;
        this.authorId = authorId;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.price = price;
        this.stock = stock;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
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
        return "Book{id='" + id + "', title='" + title + "', authorId='" + authorId + "', isbn='" + isbn + "', publicationYear=" + publicationYear + ", price=" + price + ", stock=" + stock + "}";
    }
}
