package com.petclinic.vet.exception;

/**
 * Thrown when attempting to create a resource that already exists (e.g. duplicate specialty name).
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
