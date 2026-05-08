package com.petclinic.vet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity representing a veterinarian.
 * Has a many-to-many relationship with Specialty via the vet_specialties join table.
 * Includes audit fields for tracking creation and modification timestamps.
 */
@Entity
@Table(name = "vets")
public class Vet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", length = 30, nullable = false)
    @NotEmpty
    @Size(min = 1, max = 30)
    private String firstName;

    @Column(name = "last_name", length = 30, nullable = false)
    @NotEmpty
    @Size(min = 1, max = 30)
    private String lastName;

    // Many-to-many relationship: a vet can have multiple specialties
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "vet_specialties",
        joinColumns = @JoinColumn(name = "vet_id"),
        inverseJoinColumns = @JoinColumn(name = "specialty_id")
    )
    private Set<Specialty> specialties = new HashSet<>();

    // Audit field: timestamp when the record was first created
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Audit field: timestamp when the record was last updated
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Automatically sets audit timestamps before initial persist.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * Automatically updates the modification timestamp before each update.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
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

    public Set<Specialty> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(Set<Specialty> specialties) {
        this.specialties = specialties;
    }

    /**
     * Convenience method to add a single specialty to this vet.
     */
    public void addSpecialty(Specialty specialty) {
        this.specialties.add(specialty);
    }

    /**
     * Convenience method to remove all specialties from this vet.
     */
    public void clearSpecialties() {
        this.specialties.clear();
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
