/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_coursework.resources.resource;


import com.mycompany.csa_coursework.resources.exception.AuthorNotFoundException;
import com.mycompany.csa_coursework.resources.exception.InvalidInputException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import com.mycompany.csa_coursework.resources.model.Authors;
import com.mycompany.csa_coursework.resources.model.Books;


@Path("/authors")
public class AuthorResource {
    private static final ConcurrentHashMap<String, Authors> authorStore = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Books> bookStore = BookResource.getBooks();
    
    public static ConcurrentHashMap<String, Authors> getAuthors() {
    return authorStore;
    }


    static {
        addInitialAuthors();
    }

    private static void addInitialAuthors() {
        Authors author1 = new Authors("J.R.R. Tolkien", "British author, best known for The Lord of the Rings.");
        Authors author2 = new Authors("J.K. Rowling", "British author, best known for Harry Potter series.");
        authorStore.put(author1.getId(), author1);
        authorStore.put(author2.getId(), author2);
       
       
    }
    
     private boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
     
     

@POST
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public Response createAuthor(Authors author) {

    if (isNullOrBlank(author.getName()) || isNullOrBlank(author.getBiography())) {
            throw new InvalidInputException("Name and biography must not be null, empty.");
        }

    for (Authors existingAuthor : authorStore.values()) {
        if (existingAuthor.getName().equalsIgnoreCase(author.getName()) ||
            existingAuthor.getBiography().equalsIgnoreCase(author.getBiography())) {
            return Response.status(Response.Status.CONFLICT)
                           .entity("Author ALREADY EXISTS. Author Name: " + existingAuthor.getName())
                           .build();
        }
    }

    author.assignSequentialId();
    authorStore.put(author.getId(), author);

    return Response.status(Response.Status.CREATED)
                   .entity(author)
                   .build();
}


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAuthors() {
        return Response.ok(new ArrayList<>(authorStore.values())).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthorById(@PathParam("id") String id) {
        Authors author = authorStore.get(id);
        if (author == null) {
            throw new AuthorNotFoundException("Author with ID " + id + " does not exist.");
        }
        return Response.ok(author).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAuthor(@PathParam("id") String id, Authors updatedAuthor) {
        Authors existingAuthor = authorStore.get(id);
        if (existingAuthor == null) {
            throw new AuthorNotFoundException("Author with ID " + id + " does not exist.");
        }

        
        if (updatedAuthor.getName() == null && updatedAuthor.getBiography() == null) {
            throw new InvalidInputException("At least one field (name or biography) must be provided to update.");
        }

      
        if (updatedAuthor.getName() != null) {
            for (Authors otherAuthor : authorStore.values()) {
                if (!otherAuthor.getId().equals(id) && 
                    otherAuthor.getName().equalsIgnoreCase(updatedAuthor.getName())) {
                    return Response.status(Response.Status.CONFLICT)
                                   .entity("Name already in use by another author: " + otherAuthor.getName())
                                   .build();
                }
            }
        }

       
        if (updatedAuthor.getBiography() != null) {
            for (Authors otherAuthor : authorStore.values()) {
                if (!otherAuthor.getId().equals(id) && 
                    otherAuthor.getBiography().equalsIgnoreCase(updatedAuthor.getBiography())) {
                    return Response.status(Response.Status.CONFLICT)
                                   .entity("Biography already in use by another author: " + otherAuthor.getName())
                                   .build();
                }
            }
        }

       
        if (updatedAuthor.getName() != null) {
            if (isNullOrBlank(updatedAuthor.getName())) {
                throw new InvalidInputException("Name must not be empty or contain only whitespace");
            }
            existingAuthor.setName(updatedAuthor.getName());
        }

        if (updatedAuthor.getBiography() != null) {
            if (isNullOrBlank(updatedAuthor.getBiography())) {
                throw new InvalidInputException("Biography must not be empty or contain only whitespace");
            }
            existingAuthor.setBiography(updatedAuthor.getBiography());
        }

        authorStore.put(id, existingAuthor);
        return Response.ok(existingAuthor).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAuthor(@PathParam("id") String id) {
        Authors removedAuthor = authorStore.remove(id);
        if (removedAuthor == null) {
            throw new AuthorNotFoundException("Author with ID " + id + " does not exist.");
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
    
    @GET
    @Path("/{id}/books")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksByAuthor(@PathParam("id") String id) {
        if (!authorStore.containsKey(id)) {
            throw new AuthorNotFoundException("Author with ID " + id + " does not exist.");
        }
        List<Books> books = bookStore.values().stream()
                .filter(book -> book.getAuthorId().equals(id))
                .collect(Collectors.toList());
        return Response.ok(books).build();
    }

    
}
