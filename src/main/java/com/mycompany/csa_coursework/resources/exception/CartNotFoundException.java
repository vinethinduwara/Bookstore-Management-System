/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_coursework.resources.exception;

/**
 *
 * @author Dell
 */
public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String message){
        super(message);
    }
    
}
