package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for Specialty entities.
 * Includes custom query methods for name-based searching.
 */
@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {

    /** Find specialties whose names match any of the given names (case-insensitive). */
    @Query("SELECT s FROM Specialty s WHERE LOWER(s.name) IN :names")
    List<Specialty> findByNameInIgnoreCase(@Param("names") Set<String> names);

    /** Search specialties by name containing the given string (case-insensitive). */
    List<Specialty> findByNameContainingIgnoreCase(String name);
}
