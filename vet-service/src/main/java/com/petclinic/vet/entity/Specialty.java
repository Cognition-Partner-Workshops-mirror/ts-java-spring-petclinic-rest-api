package com.petclinic.vet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity representing a veterinary specialty (e.g. radiology, surgery).
 * Participates in a many-to-many relationship with {@link Vet}.
 */
@Entity
@Table(name = "specialties")
public class Specialty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** The display name of this specialty (1-80 characters). */
    @Column(nullable = false, length = 80)
    private String name;

    /** Audit timestamp set automatically on first persist. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Audit timestamp updated on every save. */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Vets that hold this specialty (inverse side of the relationship). */
    @ManyToMany(mappedBy = "specialties")
    private Set<Vet> vets = new HashSet<>();

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Set<Vet> getVets() {
        return vets;
    }

    public void setVets(Set<Vet> vets) {
        this.vets = vets;
    }
}
