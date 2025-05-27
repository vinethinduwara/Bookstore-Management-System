/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_coursework.resources.resource;

import com.mycompany.csa_coursework.resources.exception.CartNotFoundException;
import com.mycompany.csa_coursework.resources.exception.CustomerNotFoundException;
import com.mycompany.csa_coursework.resources.exception.InvalidInputException;
import com.mycompany.csa_coursework.resources.model.Order;
import com.mycompany.csa_coursework.resources.model.Carts;
import com.mycompany.csa_coursework.resources.model.Customers;
import com.mycompany.csa_coursework.resources.model.Books;
import java.util.ArrayList;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Path("/customers/{customerId}/orders")
public class OrderResource {
    private static final ConcurrentHashMap<String, Order> orderStore = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Customers> customerStore = CustomerResource.getCustomers();
    private static final ConcurrentHashMap<String, Books> bookStore = BookResource.getBooks();
    private static final ConcurrentHashMap<String, Carts> cartStore = CartResource.getCarts();



   
    public static ConcurrentHashMap<String, Carts> getCartStore() {
        return cartStore;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrder(@PathParam("customerId") String customerId) {
        if (customerId == null || !customerStore.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " does not exist.");
        }
        Carts cart = cartStore.get(customerId);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new CartNotFoundException("Cart for customer ID " + customerId + " is empty or does not exist.");
        }
        Order order = new Order(customerId);
        order.getItems().addAll(cart.getItems()); 
        
        cart.getItems().clear();
        orderStore.put(order.getId(), order);
        return Response.status(Response.Status.CREATED).entity(order).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOrders(@PathParam("customerId") String customerId) {
        if (customerId == null || !customerStore.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " does not exist.");
        }
        List<Order> customerOrders = new ArrayList<>();
        for (Order order : orderStore.values()) {
            if (order.getCustomerId().equals(customerId)) {
                customerOrders.add(order);
            }
        }
        return Response.ok(customerOrders).build();
    }

    @GET
    @Path("/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrder(@PathParam("customerId") String customerId, @PathParam("orderId") String orderId) {
        if (customerId == null || !customerStore.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " does not exist.");
        }
        Order order = orderStore.get(orderId);
        if (order == null) {
            throw new InvalidInputException("Order with ID " + orderId + " does not exist.");
        }
        if (!order.getCustomerId().equals(customerId)) {
            throw new InvalidInputException("Order with ID " + orderId + " does not belong to customer " + customerId + ".");
        }
        return Response.ok(order).build();
    }
}