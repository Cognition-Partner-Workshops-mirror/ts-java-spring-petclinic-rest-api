package com.petclinic.vet.exception;

/**
 * Exception thrown when a requested resource (Vet or Specialty) is not found.
 * Handled by GlobalExceptionHandler to return RFC 7807 Problem Details.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
