package com.petclinic.vet.dto;

/**
 * DTO for field-level validation errors.
 * Matches the OpenAPI ValidationMessage schema.
 */
public class ValidationMessageDto {

    /** The validation error message. */
    private String message;

    public ValidationMessageDto() {
    }

    public ValidationMessageDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
