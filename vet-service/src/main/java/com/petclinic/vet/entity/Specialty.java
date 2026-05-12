package com.petclinic.vet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity representing a veterinary specialty (e.g. radiology, surgery).
 * Participates in a many-to-many relationship with {@link Vet}.
 */
@Entity
@Table(name = "specialties")
public class Specialty extends BaseEntity {

    /** Unique specialty name — constrained at the DB level */
    @Column(name = "name", nullable = false, length = 80)
    private String name;

    /** Vets that hold this specialty (inverse side of the relationship) */
    @ManyToMany(mappedBy = "specialties")
    private Set<Vet> vets = new HashSet<>();

    public Specialty() {
    }

    public Specialty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Vet> getVets() {
        return vets;
    }

    public void setVets(Set<Vet> vets) {
        this.vets = vets;
    }
}
