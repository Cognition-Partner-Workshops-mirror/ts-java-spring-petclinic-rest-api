package com.petclinic.vet.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceType, Integer id) {
        super("%s not found with id %d".formatted(resourceType, id));
    }
}
