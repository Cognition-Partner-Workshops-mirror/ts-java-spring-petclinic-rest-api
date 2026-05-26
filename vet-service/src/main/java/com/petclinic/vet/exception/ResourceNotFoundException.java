package com.petclinic.vet.exception;

/**
 * Thrown when a requested entity (vet or specialty) cannot be found by its ID.
 * Handled globally by {@link GlobalExceptionHandler} to produce an RFC 7807 response.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
