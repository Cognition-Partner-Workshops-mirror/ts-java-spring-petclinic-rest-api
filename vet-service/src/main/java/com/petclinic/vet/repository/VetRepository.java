package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Vet entities.
 * Includes custom query methods for filtering by name and specialty.
 */
@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

    /** Find vets whose last name contains the given string (case-insensitive) */
    @Query("SELECT DISTINCT v FROM Vet v LEFT JOIN FETCH v.specialties " +
           "WHERE LOWER(v.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<Vet> findByLastNameContainingIgnoreCase(@Param("lastName") String lastName);

    /** Find vets that have a specific specialty (by specialty name, case-insensitive) */
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s " +
           "WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :specialtyName, '%'))")
    List<Vet> findBySpecialtyNameContainingIgnoreCase(@Param("specialtyName") String specialtyName);

    /** Find vets by first name and last name (case-insensitive) */
    @Query("SELECT DISTINCT v FROM Vet v LEFT JOIN FETCH v.specialties " +
           "WHERE LOWER(v.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) " +
           "AND LOWER(v.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<Vet> findByFirstNameAndLastNameContainingIgnoreCase(
        @Param("firstName") String firstName, @Param("lastName") String lastName);
}
