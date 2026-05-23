package com.petclinic.vet.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for Vet entity, maps to the Vet OpenAPI schema.
 * Extends VetRequestDto concept with id and audit timestamps.
 */
public class VetResponseDto {

    private Integer id;
    private String firstName;
    private String lastName;
    private List<SpecialtyDto> specialties;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VetResponseDto() {
    }

    public VetResponseDto(Integer id, String firstName, String lastName,
                          List<SpecialtyDto> specialties, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialties = specialties;
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

    public List<SpecialtyDto> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<SpecialtyDto> specialties) {
        this.specialties = specialties;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
