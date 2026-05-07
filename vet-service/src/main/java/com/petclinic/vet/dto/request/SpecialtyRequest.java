package com.petclinic.vet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SpecialtyRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 80, message = "Name must be between 1 and 80 characters")
    private String name;

    public SpecialtyRequest() {
    }

    public SpecialtyRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
