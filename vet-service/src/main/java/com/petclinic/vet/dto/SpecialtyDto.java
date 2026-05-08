package com.petclinic.vet.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * Response DTO representing a specialty returned by the API.
 * Mirrors the OpenAPI Specialty schema with id (read-only) and name fields.
 */
public class SpecialtyDto {

    private Integer id;

    @NotEmpty
    @Size(min = 1, max = 80)
    private String name;

    public SpecialtyDto() {
    }

    public SpecialtyDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
