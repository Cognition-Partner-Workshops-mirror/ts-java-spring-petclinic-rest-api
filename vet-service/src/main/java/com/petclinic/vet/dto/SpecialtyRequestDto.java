package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class SpecialtyRequestDto {

    @NotEmpty
    @Size(min = 1, max = 80)
    private String name;

    public SpecialtyRequestDto() {
    }

    public SpecialtyRequestDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
