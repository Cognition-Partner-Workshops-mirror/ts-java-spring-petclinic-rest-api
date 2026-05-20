package com.petclinic.vet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity representing a veterinary specialty (e.g. radiology, dentistry).
 * Includes audit fields for tracking creation and modification timestamps.
 */
@Entity
@Table(name = "specialties")
@EntityListeners(AuditingEntityListener.class)
public class Specialty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** The name of the specialty, must be between 1 and 80 characters. */
    @Column(name = "name", length = 80, nullable = false)
    @NotEmpty
    @Size(min = 1, max = 80)
    private String name;

    /** Inverse side of the Vet-Specialty many-to-many relationship. */
    @ManyToMany(mappedBy = "specialties")
    private Set<Vet> vets = new HashSet<>();

    /** Timestamp when the record was created, managed by JPA auditing. */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Timestamp when the record was last updated, managed by JPA auditing. */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

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

    public Set<Vet> getVets() {
        return vets;
    }

    public void setVets(Set<Vet> vets) {
        this.vets = vets;
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
