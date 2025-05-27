/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_coursework.resources.exception;

/**
 *
 * @author Dell
 */
public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException (String message){
        super(message);
    }
    
}
