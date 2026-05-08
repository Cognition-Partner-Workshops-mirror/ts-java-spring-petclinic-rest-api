package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request DTO for creating or updating a veterinarian.
 * Mirrors the OpenAPI VetFields schema: firstName, lastName, specialties (by ID).
 */
public class VetRequestDto {

    @NotEmpty(message = "First name must not be empty")
    @Size(min = 1, max = 30, message = "First name must be between 1 and 30 characters")
    private String firstName;

    @NotEmpty(message = "Last name must not be empty")
    @Size(min = 1, max = 30, message = "Last name must be between 1 and 30 characters")
    private String lastName;

    // List of specialty IDs to assign to this vet
    private List<Integer> specialtyIds;

    public VetRequestDto() {
    }

    public VetRequestDto(String firstName, String lastName, List<Integer> specialtyIds) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialtyIds = specialtyIds;
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

    public List<Integer> getSpecialtyIds() {
        return specialtyIds;
    }

    public void setSpecialtyIds(List<Integer> specialtyIds) {
        this.specialtyIds = specialtyIds;
    }
}
