package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for Specialty entities.
 * Provides case-insensitive name lookups for duplicate detection.
 */
@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {

    // Find a specialty by exact name (case-insensitive) for uniqueness checks
    Optional<Specialty> findByNameIgnoreCase(String name);

    // Check whether a specialty name already exists (case-insensitive)
    boolean existsByNameIgnoreCase(String name);
}
