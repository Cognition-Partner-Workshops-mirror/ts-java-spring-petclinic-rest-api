package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class VetRequestDto {

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 30, message = "First name must be between 1 and 30 characters")
    @Pattern(regexp = "^[\\p{L}]+([ '\\-][\\p{L}]+){0,2}$", message = "First name contains invalid characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 30, message = "Last name must be between 1 and 30 characters")
    @Pattern(regexp = "^[\\p{L}]+([ '\\-][\\p{L}]+){0,2}\\.?$", message = "Last name contains invalid characters")
    private String lastName;

    private List<SpecialtyRequestDto> specialties = new ArrayList<>();

    public VetRequestDto() {
    }

    public VetRequestDto(String firstName, String lastName, List<SpecialtyRequestDto> specialties) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialties = specialties != null ? specialties : new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<SpecialtyRequestDto> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<SpecialtyRequestDto> specialties) {
        this.specialties = specialties;
    }
}
