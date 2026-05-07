package com.petclinic.vet.dto.response;

import java.util.List;

public class VetResponse {

    private Integer id;
    private String firstName;
    private String lastName;
    private List<SpecialtyResponse> specialties;

    public VetResponse() {
    }

    public VetResponse(Integer id, String firstName, String lastName, List<SpecialtyResponse> specialties) {
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

    public List<SpecialtyResponse> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<SpecialtyResponse> specialties) {
        this.specialties = specialties;
    }
}
