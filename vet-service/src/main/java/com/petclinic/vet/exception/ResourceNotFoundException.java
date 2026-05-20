package com.petclinic.vet.exception;

/**
 * Exception thrown when a requested resource (Vet or Specialty) is not found.
 * Results in an HTTP 404 response via the global exception handler.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
