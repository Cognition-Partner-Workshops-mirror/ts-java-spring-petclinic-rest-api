package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Vet entity.
 * Provides standard CRUD plus custom queries for filtering by specialty and name search.
 */
@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

    /**
     * Find all vets that have a given specialty (by specialty ID).
     */
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE s.id = :specialtyId")
    List<Vet> findBySpecialtyId(@Param("specialtyId") Integer specialtyId);

    /**
     * Case-insensitive search for vets whose last name contains the given string.
     */
    @Query("SELECT v FROM Vet v WHERE LOWER(v.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<Vet> findByLastNameContainingIgnoreCase(@Param("lastName") String lastName);
}
