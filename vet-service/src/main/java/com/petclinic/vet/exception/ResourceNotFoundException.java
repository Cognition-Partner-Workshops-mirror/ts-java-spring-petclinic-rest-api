package com.petclinic.vet.exception;

public class ResourceNotFoundException extends RuntimeException {

    private final String entityName;
    private final int entityId;

    public ResourceNotFoundException(String entityName, int entityId) {
        super(entityName + " not found with id " + entityId);
        this.entityName = entityName;
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public int getEntityId() {
        return entityId;
    }
}
