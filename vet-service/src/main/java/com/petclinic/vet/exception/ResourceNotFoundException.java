package com.petclinic.vet.exception;

/**
 * Thrown when a requested resource (vet or specialty) is not found in the database.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
