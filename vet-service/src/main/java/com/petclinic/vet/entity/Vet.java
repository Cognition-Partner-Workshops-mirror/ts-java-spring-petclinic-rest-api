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

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity representing a veterinarian.
 * Owns the many-to-many relationship with {@link Specialty} via a join table.
 */
@Entity
@Table(name = "vets")
public class Vet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** Vet's first name (1-30 characters, letters only with optional hyphens/apostrophes). */
    @Column(name = "first_name", nullable = false, length = 30)
    private String firstName;

    /** Vet's last name (1-30 characters, letters only with optional trailing period). */
    @Column(name = "last_name", nullable = false, length = 30)
    private String lastName;

    /** Audit timestamp set automatically on first persist. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Audit timestamp updated on every save. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Specialties assigned to this vet (owning side of the many-to-many). */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "vet_specialties",
        joinColumns = @JoinColumn(name = "vet_id"),
        inverseJoinColumns = @JoinColumn(name = "specialty_id")
    )
    private Set<Specialty> specialties = new HashSet<>();

    // -- Lifecycle callbacks --------------------------------------------------

    /** Sets audit timestamps before the first insert. */
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /** Refreshes the updatedAt timestamp before every update. */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // -- Getters and setters --------------------------------------------------

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

    public Set<Specialty> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(Set<Specialty> specialties) {
        this.specialties = specialties;
    }
}
