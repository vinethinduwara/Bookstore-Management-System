/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_coursework.resources.model;





public class Authors {
    private String id;
    private String name;
    private String biography;
    private static int idCounter;

    // Default constructor for JSON deserialization
    public Authors() {
    }

    public Authors(String name, String biography) {
        this.id = generateNextId();
        this.name = name;
        this.biography = biography;
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

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
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
        return "Author{id='" + id + "', name='" + name + "', biography='" + biography + "'}";
    }
}
