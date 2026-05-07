package com.petclinic.vet.dto.response;

public class SpecialtyResponse {

    private Integer id;
    private String name;

    public SpecialtyResponse() {
    }

    public SpecialtyResponse(Integer id, String name) {
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
