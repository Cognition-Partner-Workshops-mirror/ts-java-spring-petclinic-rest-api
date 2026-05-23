package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Vet entities with custom query methods.
 */
@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

    /**
     * Finds vets whose last name contains the given string, case-insensitive.
     */
    List<Vet> findByLastNameContainingIgnoreCase(String lastName);

    /**
     * Finds all vets who have a specialty with the given ID.
     */
    @Query("SELECT v FROM Vet v JOIN v.specialties s WHERE s.id = :specialtyId")
    List<Vet> findBySpecialtyId(@Param("specialtyId") Integer specialtyId);

    /**
     * Finds all vets who have a specialty matching the given name (case-insensitive).
     */
    @Query("SELECT v FROM Vet v JOIN v.specialties s WHERE LOWER(s.name) = LOWER(:specialtyName)")
    List<Vet> findBySpecialtyName(@Param("specialtyName") String specialtyName);
}
