package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Specialty entities.
 * Provides custom query methods for name-based searching.
 */
@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {

    /** Find specialties whose name contains the given string (case-insensitive). */
    List<Specialty> findByNameContainingIgnoreCase(String name);
}
