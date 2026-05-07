package com.petclinic.vet.exception;

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
