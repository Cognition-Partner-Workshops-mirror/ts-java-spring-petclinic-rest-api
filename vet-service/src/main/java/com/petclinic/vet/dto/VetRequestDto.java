package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class VetRequestDto {

    @NotEmpty
    @Size(min = 1, max = 30)
    @Pattern(regexp = "^[\\p{L}]+([ '\\-][\\p{L}]+){0,2}$")
    private String firstName;

    @NotEmpty
    @Size(min = 1, max = 30)
    @Pattern(regexp = "^[\\p{L}]+([ '\\-][\\p{L}]+){0,2}\\.?$")
    private String lastName;

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
