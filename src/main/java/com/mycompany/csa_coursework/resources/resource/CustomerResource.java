/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_coursework.resources.resource;

import com.mycompany.csa_coursework.resources.exception.CustomerNotFoundException;
import com.mycompany.csa_coursework.resources.exception.InvalidInputException;
import com.mycompany.csa_coursework.resources.model.Customers;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


    @Path("/customers")
    public class CustomerResource {
    private static final ConcurrentHashMap<String, Customers> customerStore = new ConcurrentHashMap<>();
    
    public static ConcurrentHashMap<String, Customers> getCustomers() {
    return customerStore;
    }
    
    static {
        addInitialCustomers();
    }
    
    private static void addInitialCustomers() {
        Customers customer = new Customers("John Doe", "john@example.com", "password123");
        Customers customer2 = new Customers("kasun", "kasun@example.com", "password12345");
        customerStore.put(customer.getId(), customer);
        customerStore.put(customer2.getId(), customer2);
    }
    
    private boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCustomer(Customers customer) {
        if (isNullOrBlank(customer.getName()) || isNullOrBlank(customer.getEmail()) || isNullOrBlank(customer.getPassword())) {
            throw new InvalidInputException("All fields (name, email, password) are required and must be valid.");
        }
        
        
        for (Customers existingCustomer : customerStore.values()) {
            if (existingCustomer.getEmail().equalsIgnoreCase(customer.getEmail())||
            existingCustomer.getName().equalsIgnoreCase(customer.getName())) {
                return Response.status(Response.Status.CONFLICT)
                               .entity("Email or Name already in use by another customer: " + existingCustomer.getName())
                               .build();
            }
        }
        
        if (!customer.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidInputException("Invalid email format.");
        }
        
        customer.assignSequentialId();
        customerStore.put(customer.getId(), customer);
        return Response.status(Response.Status.CREATED).entity(customer).build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomers() {
        return Response.ok(new ArrayList<>(customerStore.values())).build();
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerById(@PathParam("id") String id) {
        Customers customer = customerStore.get(id);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer with ID " + id + " does not exist.");
        }
        return Response.ok(customer).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@PathParam("id") String id, Customers updatedCustomer) {
        Customers existingCustomer = customerStore.get(id);
        if (existingCustomer == null) {
            throw new CustomerNotFoundException("Customer with ID " + id + " does not exist.");
        }

        if (updatedCustomer.getName() == null && 
            updatedCustomer.getEmail() == null && 
            updatedCustomer.getPassword() == null) {
            throw new InvalidInputException("At least one field (name, email, or password) must be provided to update.");
        }

        if (updatedCustomer.getEmail() != null) {
            for (Customers otherCustomer : customerStore.values()) {
                if (!otherCustomer.getId().equals(id) && 
                    otherCustomer.getEmail().equalsIgnoreCase(updatedCustomer.getEmail())) {
                    return Response.status(Response.Status.CONFLICT)
                                   .entity("Email already in use by another customer: " + otherCustomer.getName())
                                   .build();
                }
            }
        }

        if (updatedCustomer.getName() != null) {
            for (Customers otherCustomer : customerStore.values()) {
                if (!otherCustomer.getId().equals(id) && 
                    otherCustomer.getName().equalsIgnoreCase(updatedCustomer.getName())) {
                    return Response.status(Response.Status.CONFLICT)
                                   .entity("Name already in use by another customer: " + otherCustomer.getName())
                                   .build();
                }
            }
        }

        if (updatedCustomer.getName() != null) {
            if (isNullOrBlank(updatedCustomer.getName())) {
                throw new InvalidInputException("Name must not be empty or contain only whitespace.");
            }
            existingCustomer.setName(updatedCustomer.getName());
        }

        if (updatedCustomer.getEmail() != null) {
            if (isNullOrBlank(updatedCustomer.getEmail())) {
                throw new InvalidInputException("Email must not be empty or contain only whitespace.");
            }
            if (!updatedCustomer.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new InvalidInputException("Invalid email format.");
            }
            existingCustomer.setEmail(updatedCustomer.getEmail());
        }

        if (updatedCustomer.getPassword() != null) {
            if (isNullOrBlank(updatedCustomer.getPassword())) {
                throw new InvalidInputException("Password must not be empty or contain only whitespace.");
            }
            existingCustomer.setPassword(updatedCustomer.getPassword());
        }

        customerStore.put(id, existingCustomer);
        return Response.ok(existingCustomer).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") String id) {
        Customers removedCustomer = customerStore.remove(id);
        if (removedCustomer == null) {
            throw new CustomerNotFoundException("Customer with ID " + id + " does not exist.");
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}