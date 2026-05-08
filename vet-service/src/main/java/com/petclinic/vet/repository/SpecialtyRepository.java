package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Specialty entity.
 * Provides standard CRUD plus custom query methods for name-based searching.
 */
@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {

    /**
     * Case-insensitive search for specialties whose name contains the given string.
     */
    @Query("SELECT s FROM Specialty s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Specialty> findByNameContainingIgnoreCase(@Param("name") String name);
}
