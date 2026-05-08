package com.petclinic.vet.dto;

import java.util.List;

/**
 * Response DTO representing a veterinarian returned by the API.
 * Mirrors the OpenAPI Vet schema: id (read-only), firstName, lastName, specialties.
 */
public class VetDto {

    private Integer id;
    private String firstName;
    private String lastName;
    private List<SpecialtyDto> specialties;

    public VetDto() {
    }

    public VetDto(Integer id, String firstName, String lastName, List<SpecialtyDto> specialties) {
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

    public List<SpecialtyDto> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<SpecialtyDto> specialties) {
        this.specialties = specialties;
    }
}
