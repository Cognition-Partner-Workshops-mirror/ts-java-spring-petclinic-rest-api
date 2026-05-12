package com.petclinic.vet.exception;

/**
 * Exception thrown when attempting to create a resource that already exists.
 * Maps to HTTP 400 in the global exception handler.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
