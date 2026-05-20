package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Request DTO for creating or updating a Vet.
 * Matches the OpenAPI VetFields schema: firstName, lastName, specialties.
 */
public class VetRequestDto {

    /** The first name of the vet, required, 1-30 characters. */
    @NotEmpty(message = "First name must not be empty")
    @Size(min = 1, max = 30, message = "First name must be between 1 and 30 characters")
    private String firstName;

    /** The last name of the vet, required, 1-30 characters. */
    @NotEmpty(message = "Last name must not be empty")
    @Size(min = 1, max = 30, message = "Last name must be between 1 and 30 characters")
    private String lastName;

    /** List of specialty references to assign to this vet. */
    private List<SpecialtyResponseDto> specialties = new ArrayList<>();

    public VetRequestDto() {
    }

    public VetRequestDto(String firstName, String lastName, List<SpecialtyResponseDto> specialties) {
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

    public List<SpecialtyResponseDto> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<SpecialtyResponseDto> specialties) {
        this.specialties = specialties;
    }
}
