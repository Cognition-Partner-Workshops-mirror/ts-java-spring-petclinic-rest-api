package com.petclinic.vet.exception;

/**
 * Thrown when a requested resource (Vet or Specialty) is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Integer id) {
        super("%s not found with id: %d".formatted(resourceName, id));
    }
}
