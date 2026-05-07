package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Vet entities.
 * Provides custom query methods for filtering by last name, specialty, or both.
 */
@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

    /** Find vets whose last name contains the given string (case-insensitive). */
    List<Vet> findByLastNameContainingIgnoreCase(String lastName);

    /** Find vets that have a specific specialty by specialty ID. */
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE s.id = :specialtyId")
    List<Vet> findBySpecialtyId(@Param("specialtyId") Integer specialtyId);

    /** Find vets that have a specific specialty by specialty name (case-insensitive). */
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE LOWER(s.name) = LOWER(:specialtyName)")
    List<Vet> findBySpecialtyName(@Param("specialtyName") String specialtyName);

    /** Find vets matching both last name (partial, case-insensitive) and specialty name. */
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s " +
           "WHERE LOWER(v.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')) " +
           "AND LOWER(s.name) = LOWER(:specialtyName)")
    List<Vet> findByLastNameAndSpecialtyName(@Param("lastName") String lastName,
                                             @Param("specialtyName") String specialtyName);
}
