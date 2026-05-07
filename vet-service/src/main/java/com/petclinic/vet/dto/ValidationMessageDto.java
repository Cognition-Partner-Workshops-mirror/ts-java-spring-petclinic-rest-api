package com.petclinic.vet.dto;

public class ValidationMessageDto {

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
