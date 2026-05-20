package com.petclinic.vet.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Response DTO for Vet, returned by the API.
 * Matches the OpenAPI Vet schema: id, firstName, lastName, specialties.
 */
public class VetResponseDto {

    /** The unique identifier of the vet (read-only). */
    private Integer id;

    /** The first name of the vet. */
    private String firstName;

    /** The last name of the vet. */
    private String lastName;

    /** The specialties assigned to this vet. */
    private List<SpecialtyResponseDto> specialties = new ArrayList<>();

    public VetResponseDto() {
    }

    public VetResponseDto(Integer id, String firstName, String lastName, List<SpecialtyResponseDto> specialties) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialties = specialties != null ? specialties : new ArrayList<>();
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
