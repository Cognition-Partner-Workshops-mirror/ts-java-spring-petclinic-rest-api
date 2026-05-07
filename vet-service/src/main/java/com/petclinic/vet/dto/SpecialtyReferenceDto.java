package com.petclinic.vet.dto;

public class SpecialtyReferenceDto {

    private Integer id;
    private String name;

    public SpecialtyReferenceDto() {
    }

    public SpecialtyReferenceDto(Integer id, String name) {
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
