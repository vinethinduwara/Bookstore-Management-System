/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.csa_coursework.resources.exception;

/**
 *
 * @author Dell
 */
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;



@Provider
public class CommonExceptionMapper implements ExceptionMapper<RuntimeException> {
    @Override
    public Response toResponse(RuntimeException exception) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", exception.getClass().getSimpleName());
        errorResponse.put("message", exception.getMessage());

        int status;
        if (exception instanceof BookNotFoundException ||
            exception instanceof AuthorNotFoundException ||
            exception instanceof CustomerNotFoundException ||
            exception instanceof CartNotFoundException) {
            status = Response.Status.NOT_FOUND.getStatusCode();
        } else if (exception instanceof InvalidInputException ||
                   exception instanceof OutOfStockException) {
            status = Response.Status.BAD_REQUEST.getStatusCode();
        } 
        else {
            status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
            errorResponse.put("error", "InternalServerError");
            errorResponse.put("message", "An unexpected error occurred.");
        }

        return Response.status(status)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

