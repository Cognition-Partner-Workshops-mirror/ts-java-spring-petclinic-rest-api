package com.petclinic.vet.exception;

/**
 * Thrown when a requested resource (Vet or Specialty) is not found.
 * Handled by {@link GlobalExceptionHandler} to produce a 404 RFC 7807 Problem Detail.
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final Integer resourceId;

    public ResourceNotFoundException(String resourceName, Integer resourceId) {
        super(String.format("%s not found with id: %d", resourceName, resourceId));
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Integer getResourceId() {
        return resourceId;
    }
}
