package com.petclinic.vet.dto;

/**
 * Response DTO for Specialty, returned by the API.
 * Matches the OpenAPI Specialty schema: id + name.
 */
public class SpecialtyResponseDto {

    /** The unique identifier of the specialty (read-only). */
    private Integer id;

    /** The name of the specialty. */
    private String name;

    public SpecialtyResponseDto() {
    }

    public SpecialtyResponseDto(Integer id, String name) {
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
