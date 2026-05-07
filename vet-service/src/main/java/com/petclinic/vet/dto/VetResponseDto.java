package com.petclinic.vet.dto;

import java.util.List;

/**
 * Response DTO for a vet, matching the OpenAPI Vet schema.
 * Includes the read-only id and nested specialty details.
 */
public class VetResponseDto {

    private Integer id;
    private String firstName;
    private String lastName;
    private List<SpecialtyResponseDto> specialties;

    public VetResponseDto() {
    }

    public VetResponseDto(Integer id, String firstName, String lastName,
                          List<SpecialtyResponseDto> specialties) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialties = specialties;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
