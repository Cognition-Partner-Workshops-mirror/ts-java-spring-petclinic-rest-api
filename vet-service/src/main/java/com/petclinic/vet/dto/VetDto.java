package com.petclinic.vet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class VetDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    @NotBlank
    @Size(min = 1, max = 30)
    @Pattern(regexp = "^[\\p{L}]+([ '-][\\p{L}]+){0,2}$")
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 30)
    @Pattern(regexp = "^[\\p{L}]+([ '-][\\p{L}]+){0,2}\\.?$")
    private String lastName;

    @NotNull
    @Valid
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
