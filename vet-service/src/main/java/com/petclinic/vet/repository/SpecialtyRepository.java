package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for Specialty entity.
 * Provides custom query methods for filtering by name.
 */
@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {

    /**
     * Find a specialty by exact name (case-insensitive).
     */
    Optional<Specialty> findByNameIgnoreCase(String name);

    /**
     * Search specialties by name containing the given string (case-insensitive).
     */
    @Query("SELECT s FROM Specialty s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Specialty> findByNameContainingIgnoreCase(@Param("name") String name);
}
