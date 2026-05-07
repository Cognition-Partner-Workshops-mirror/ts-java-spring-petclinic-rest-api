package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Vet entities.
 * Custom JPQL queries eagerly fetch specialties to avoid N+1 issues.
 */
@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

    // Fetch all vets with specialties in a single query
    @Query("SELECT DISTINCT v FROM Vet v LEFT JOIN FETCH v.specialties")
    List<Vet> findAllWithSpecialties();

    // Fetch a single vet by ID with specialties eagerly loaded
    @Query("SELECT v FROM Vet v LEFT JOIN FETCH v.specialties WHERE v.id = :id")
    Optional<Vet> findByIdWithSpecialties(@Param("id") Integer id);

    // Case-insensitive partial match on last name
    @Query("SELECT DISTINCT v FROM Vet v LEFT JOIN FETCH v.specialties " +
           "WHERE LOWER(v.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<Vet> findByLastNameContainingIgnoreCase(@Param("lastName") String lastName);

    // Find vets that have a specific specialty assigned (by specialty ID)
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s LEFT JOIN FETCH v.specialties " +
           "WHERE s.id = :specialtyId")
    List<Vet> findBySpecialtyId(@Param("specialtyId") Integer specialtyId);

    // Find vets by partial specialty name match (case-insensitive)
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s LEFT JOIN FETCH v.specialties " +
           "WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :specialtyName, '%'))")
    List<Vet> findBySpecialtyNameContainingIgnoreCase(@Param("specialtyName") String specialtyName);
}
