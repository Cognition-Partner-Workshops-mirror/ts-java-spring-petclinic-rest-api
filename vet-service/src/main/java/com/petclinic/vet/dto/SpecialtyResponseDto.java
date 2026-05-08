package com.petclinic.vet.dto;

/**
 * Response DTO for Specialty, matching the OpenAPI Specialty schema.
 * Contains read-only id plus the specialty name.
 */
public class SpecialtyResponseDto {

    private Integer id;
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
