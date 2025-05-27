/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_coursework.resources.resource;

import com.mycompany.csa_coursework.resources.exception.BookNotFoundException;
import com.mycompany.csa_coursework.resources.exception.CartNotFoundException;
import com.mycompany.csa_coursework.resources.exception.CustomerNotFoundException;
import com.mycompany.csa_coursework.resources.exception.InvalidInputException;
import com.mycompany.csa_coursework.resources.exception.OutOfStockException;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.Path;
import com.mycompany.csa_coursework.resources.model.Books;
import com.mycompany.csa_coursework.resources.model.CartItem;
import com.mycompany.csa_coursework.resources.model.Customers;
import com.mycompany.csa_coursework.resources.model.Carts;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;


@Path("/customers/{customerId}/cart")
public class CartResource {
    private static final ConcurrentHashMap<String, Carts> cartStore = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Customers> customerStore = CustomerResource.getCustomers();
    private static final ConcurrentHashMap<String, Books> bookStore = BookResource.getBooks();
  
    public static ConcurrentHashMap<String, Carts> getCarts() {
    return cartStore;
    }


    
    
    private Carts createCartForCustomer(String customerId) {
        return new Carts(customerId);
    }
    
    private void removeItemsByBookId(Carts cart, String bookId) {
        List<CartItem> filteredItems = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            if (!item.getBookId().equals(bookId)) {
                filteredItems.add(item);
            }
        }
        cart.setItems(filteredItems);
    }
    private CartItem findItemByBookId(Carts cart, String bookId) {
        for (CartItem item : cart.getItems()) {
            if (item.getBookId().equals(bookId)) {
                return item;
            }
        }
        return null;
    }
    
     
    
    
    @POST
    @Path("/items")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addItemToCart(@PathParam("customerId") String customerId, CartItem item) {
        if (!customerStore.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " does not exist.");
        }
        if (item.getBookId() == null ||  item.getBookId().equals("")) {
            throw new InvalidInputException("Book ID must not be empty ");
        }
        if (item.getQuantity() <= 0) {
            throw new InvalidInputException("Quantity must be positive");
        }
        Books book = bookStore.get(item.getBookId());
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + item.getBookId() + " does not exist.");
        }

        Carts cart = cartStore.get(customerId);
        if (cart == null) {
            cart = createCartForCustomer(customerId);
            cartStore.put(customerId, cart);
        }

        CartItem existingItem = findItemByBookId(cart, item.getBookId());
        int totalQuantity;
        if (existingItem != null) {
            
            totalQuantity = existingItem.getQuantity() + item.getQuantity();
            
            if (book.getStock() < item.getQuantity()) {
                throw new OutOfStockException("Not enough stock for book ID " + item.getBookId() + ". Available: " + book.getStock());
            }
            
            removeItemsByBookId(cart, item.getBookId());
            CartItem updatedItem = new CartItem(item.getBookId(), totalQuantity);
            cart.getItems().add(updatedItem);
        } else {
            
            totalQuantity = item.getQuantity();
            if (book.getStock() < totalQuantity) {
                throw new OutOfStockException("Not enough stock for book ID " + item.getBookId() + ". Available: " + book.getStock());
            }
            cart.getItems().add(item);
        }

        
        book.setStock(book.getStock() - item.getQuantity());
        return Response.status(Response.Status.CREATED).entity(cart).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCart(@PathParam("customerId") String customerId) {
        if (!customerStore.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " does not exist.");
        }
        Carts cart = cartStore.get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart for customer ID " + customerId + " does not exist.");
        }
        return Response.ok(cart).build();
    }

    @PUT
    @Path("/items/{bookId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCartItem(@PathParam("customerId") String customerId, @PathParam("bookId") String bookId, CartItem updatedItem) {
        if (!customerStore.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " does not exist.");
        }
        Carts cart = cartStore.get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart for customer ID " + customerId + " does not exist.");
        }

        
        if (updatedItem.getBookId() == null || updatedItem.getQuantity() <= 0) {
            throw new InvalidInputException("Book ID and positive quantity are required.");
        }

        
        CartItem existingItem = findItemByBookId(cart, bookId);
        if (existingItem == null) {
            throw new InvalidInputException("Book ID " + bookId + " not found in cart");
        }

        
        Books newBook = bookStore.get(updatedItem.getBookId());
        if (newBook == null) {
            throw new BookNotFoundException("Book with ID " + updatedItem.getBookId() + " does not exist");
        }

       
        if (!bookId.equals(updatedItem.getBookId())) {
            CartItem existingItemWithNewBookId = findItemByBookId(cart, updatedItem.getBookId());
            if (existingItemWithNewBookId != null) {
                throw new InvalidInputException("Book ID " + updatedItem.getBookId() + " is already in the cart. Use update to modify its quantity");
            }
        }

        
        Books originalBook = bookStore.get(bookId);
        originalBook.setStock(originalBook.getStock() + existingItem.getQuantity());

        
        if (newBook.getStock() < updatedItem.getQuantity()) {
            throw new OutOfStockException("Not enough stock for book ID " + updatedItem.getBookId() + ". Available: " + newBook.getStock());
        }

      
        cart.getItems().remove(existingItem);
        CartItem newItem = new CartItem(updatedItem.getBookId(), updatedItem.getQuantity());
        cart.getItems().add(newItem);

        
        newBook.setStock(newBook.getStock() - updatedItem.getQuantity());

        return Response.ok(cart).build();
    }
    
    @DELETE
    @Path("/items/{bookId}")
    public Response deleteCartItem(@PathParam("customerId") String customerId, @PathParam("bookId") String bookId) {
        if (!customerStore.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " does not exist.");
        }
        Carts cart = cartStore.get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart for customer ID " + customerId + " does not exist.");
        }
        CartItem itemToRemove = findItemByBookId(cart, bookId);
        if (itemToRemove == null) {
            throw new BookNotFoundException("Book ID " + bookId + " not found in cart.");
        }
        cart.getItems().remove(itemToRemove);
        Books book = bookStore.get(bookId);
        if (book != null) {
            book.setStock(book.getStock() + itemToRemove.getQuantity());
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
    
   
        
        
    
}
