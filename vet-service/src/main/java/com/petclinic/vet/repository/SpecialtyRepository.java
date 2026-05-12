package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Specialty} entities.
 * Provides custom query methods for name-based lookups.
 */
@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {

    /** Find a specialty by its exact name (case-insensitive) */
    Optional<Specialty> findByNameIgnoreCase(String name);

    /** Search specialties whose name contains the given string (case-insensitive) */
    List<Specialty> findByNameContainingIgnoreCase(String name);

    /** Check if a specialty with the given name already exists (case-insensitive) */
    boolean existsByNameIgnoreCase(String name);
}
