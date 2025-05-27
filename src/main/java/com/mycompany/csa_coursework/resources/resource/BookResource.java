/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_coursework.resources.resource;

import com.mycompany.csa_coursework.resources.exception.BookNotFoundException;
import com.mycompany.csa_coursework.resources.exception.InvalidInputException;
import java.util.concurrent.ConcurrentHashMap;
import com.mycompany.csa_coursework.resources.model.Authors;
import com.mycompany.csa_coursework.resources.model.Books;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Year;


@Path("/books")
public class BookResource {
    private static final ConcurrentHashMap<String, Books> bookStore = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Authors> authorStore = AuthorResource.getAuthors();
    private static int currentDate = Year.now().getValue();
    
    public static ConcurrentHashMap<String, Books> getBooks() {
    return bookStore;
    }

    static {
        addInitialBooks();
    }
    
     private static void addInitialBooks() {
      
        ArrayList<Authors> authorsList = new ArrayList<>(authorStore.values());
        
        if (authorsList.isEmpty()) {    
            return;
        }
        Authors author1 = authorsList.get(0); 
        Books book1 = new Books("The Hobbit", author1.getId(), "978-0-618-00221-4", 1937, 15.99, 100);
        bookStore.put(book1.getId(), book1);
        
        if (authorsList.size() >= 2) {
            Authors author2 = authorsList.get(1); 
            Books book2 = new Books("The Lord of the Rings", author2.getId(), "978-0-618-05326-7", 1954, 20.99, 50);
            bookStore.put(book2.getId(), book2);
            
        } else {
            Books book2 = new Books("The Lord of the Rings", author1.getId(), "978-0-618-05326-7", 1954, 20.99, 50);
            bookStore.put(book2.getId(), book2);
        }
    }
    
    private boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBook(Books book) {
        if (isNullOrBlank(book.getTitle())  || isNullOrBlank(book.getAuthorId())|| isNullOrBlank(book.getIsbn()) ||
            book.getPublicationYear() <= 0 || book.getPrice() <= 0 || book.getStock() < 0 ) {
            throw new InvalidInputException("All fields (title, authorId, isbn, publicationYear, price, stock) are required and must be valid.");
        }
        
        for (Books existingBook : bookStore.values()) {
        if (existingBook.getTitle().equalsIgnoreCase(book.getTitle()) &&
            existingBook.getIsbn().equalsIgnoreCase(book.getIsbn())&&
            existingBook.getPublicationYear()==book.getPublicationYear()&&
            existingBook.getPrice()==book.getPrice() &&
            existingBook.getStock()==book.getStock()){
            return Response.status(Response.Status.CONFLICT)
                           .entity("Book ALREADY EXISTS. Book Title: " + existingBook.getTitle())
                           .build();
        }
    }
        int currentYear = Year.now().getValue();
        if (book.getPublicationYear() > currentYear) {
            throw new InvalidInputException("Publication year cannot be in the future.");
        }
        if (!authorStore.containsKey(book.getAuthorId())) {
            throw new InvalidInputException("Author with ID " + book.getAuthorId() + " does not exist.");
        }
        book.assignSequentialId();
        bookStore.put(book.getId(), book);
        return Response.status(Response.Status.CREATED).entity(book).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBooks() {
        return Response.ok(new ArrayList<>(bookStore.values())).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookById(@PathParam("id") String id) {
        Books book = bookStore.get(id);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + id + " does not exist.");
        }
        return Response.ok(book).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBook(@PathParam("id") String id, Books updatedBook) {
        Books existingBook = bookStore.get(id);
        if (existingBook == null) {
            throw new BookNotFoundException("Book with ID " + id + " does not exist.");
        }

        
        if (updatedBook.getTitle() == null &&
            updatedBook.getAuthorId() == null &&
            updatedBook.getIsbn() == null &&
            updatedBook.getPublicationYear() == 0 && 
            updatedBook.getPrice() == 0.0 &&        
            updatedBook.getStock() == -1) {         
            throw new InvalidInputException("At least one field (title, authorId, isbn, publicationYear, price, stock) must be provided to update.");
        }

        
        if (updatedBook.getTitle() != null || updatedBook.getIsbn() != null ||
            updatedBook.getPublicationYear() != 0 || updatedBook.getPrice() != 0.0 ||
            updatedBook.getStock() != -1) {
            String newTitle = updatedBook.getTitle() != null ? updatedBook.getTitle() : existingBook.getTitle();
            String newIsbn = updatedBook.getIsbn() != null ? updatedBook.getIsbn() : existingBook.getIsbn();
            int newPublicationYear = updatedBook.getPublicationYear() != 0 ? updatedBook.getPublicationYear() : existingBook.getPublicationYear();
            double newPrice = updatedBook.getPrice() != 0.0 ? updatedBook.getPrice() : existingBook.getPrice();
            int newStock = updatedBook.getStock() != -1 ? updatedBook.getStock() : existingBook.getStock();

            for (Books otherBook : bookStore.values()) {
                if (!otherBook.getId().equals(id) &&
                    otherBook.getTitle().equalsIgnoreCase(newTitle) &&
                    otherBook.getIsbn().equalsIgnoreCase(newIsbn) &&
                    otherBook.getPublicationYear() == newPublicationYear &&
                    otherBook.getPrice() == newPrice &&
                    otherBook.getStock() == newStock) {
                    return Response.status(Response.Status.CONFLICT)
                                   .entity("Book ALREADY EXISTS. Book Title: " + otherBook.getTitle())
                                   .build();
                }
            }
        }

       
        if (updatedBook.getTitle() != null) {
            if (isNullOrBlank(updatedBook.getTitle())) {
                throw new InvalidInputException("Title must not be empty or contain only whitespace");
            }
            existingBook.setTitle(updatedBook.getTitle());
        }

        if (updatedBook.getAuthorId() != null) {
            if (isNullOrBlank(updatedBook.getAuthorId())) {
                throw new InvalidInputException("Author ID must not be empty or contain only whitespace");
            }
            if (!authorStore.containsKey(updatedBook.getAuthorId())) {
                throw new InvalidInputException("Author with ID " + updatedBook.getAuthorId() + " does not exist.");
            }
            existingBook.setAuthorId(updatedBook.getAuthorId());
        }

        if (updatedBook.getIsbn() != null) {
            if (isNullOrBlank(updatedBook.getIsbn())) {
                throw new InvalidInputException("ISBN must not be empty or contain only whitespace");
            }
            existingBook.setIsbn(updatedBook.getIsbn());
        }

        if (updatedBook.getPublicationYear() != 0) {
            if (updatedBook.getPublicationYear() <= 0) {
                throw new InvalidInputException("Publication year must be a positive number");
            }
            int currentYear = Year.now().getValue();
            if (updatedBook.getPublicationYear() > currentYear) {
                throw new InvalidInputException("Publication year cannot be in the future. Current year is " + currentYear);
            }
            existingBook.setPublicationYear(updatedBook.getPublicationYear());
        }

        if (updatedBook.getPrice() != 0.0) {
            if (updatedBook.getPrice() <= 0) {
                throw new InvalidInputException("Price must be a positive number");
            }
            existingBook.setPrice(updatedBook.getPrice());
        }

        if (updatedBook.getStock() != -1) {
            if (updatedBook.getStock() < 0) {
                throw new InvalidInputException("Stock cannot be negative");
            }
            existingBook.setStock(updatedBook.getStock());
        }

        bookStore.put(id, existingBook);
        return Response.ok(existingBook).build();
    }
    @DELETE
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") String id) {
        Books removedBook = bookStore.remove(id);
        if (removedBook == null) {
            throw new BookNotFoundException("Book with ID " + id + " does not exist.");
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}