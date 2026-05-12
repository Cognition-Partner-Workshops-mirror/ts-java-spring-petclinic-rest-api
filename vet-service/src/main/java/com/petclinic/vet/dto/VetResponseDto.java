package com.petclinic.vet.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Response DTO for vet data returned to clients.
 * Extends the OpenAPI Vet schema with audit timestamps (createdAt, updatedAt).
 */
public class VetResponseDto {

    private Integer id;
    private String firstName;
    private String lastName;
    private List<SpecialtyResponseDto> specialties = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;

    public VetResponseDto() {
    }

    public VetResponseDto(Integer id, String firstName, String lastName,
                          List<SpecialtyResponseDto> specialties,
                          Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialties = specialties != null ? specialties : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
