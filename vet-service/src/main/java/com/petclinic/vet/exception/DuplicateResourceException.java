package com.petclinic.vet.exception;

/** Thrown when creating/updating a resource would violate a uniqueness constraint. Mapped to HTTP 400. */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
