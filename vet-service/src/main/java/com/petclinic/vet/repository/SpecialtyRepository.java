package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Specialty entities.
 * Includes custom query methods for name-based searching.
 */
@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {

    /** Find specialties whose name contains the given string (case-insensitive) */
    @Query("SELECT s FROM Specialty s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Specialty> findByNameContainingIgnoreCase(@Param("name") String name);

    /** Check if a specialty with the given name already exists (case-insensitive) */
    boolean existsByNameIgnoreCase(String name);
}
