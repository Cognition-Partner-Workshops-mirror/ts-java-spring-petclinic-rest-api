package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA repository for Vet entity.
 * Provides custom query methods for filtering by specialty and name search.
 */
@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

    /**
     * Find vets by last name containing the given string (case-insensitive).
     */
    @Query("SELECT v FROM Vet v WHERE LOWER(v.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<Vet> findByLastNameContainingIgnoreCase(@Param("lastName") String lastName);

    /**
     * Find vets that have a specific specialty by specialty ID.
     */
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE s.id = :specialtyId")
    List<Vet> findBySpecialtyId(@Param("specialtyId") Integer specialtyId);

    /**
     * Find vets that have a specific specialty by specialty name (case-insensitive).
     */
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE LOWER(s.name) = LOWER(:specialtyName)")
    List<Vet> findBySpecialtyName(@Param("specialtyName") String specialtyName);
}
