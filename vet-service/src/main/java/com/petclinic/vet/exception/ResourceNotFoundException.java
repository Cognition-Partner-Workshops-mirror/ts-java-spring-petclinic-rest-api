package com.petclinic.vet.exception;

/** Thrown when a requested resource (vet or specialty) is not found by ID. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Integer id) {
        super(resourceName + " not found with id: " + id);
    }
}
