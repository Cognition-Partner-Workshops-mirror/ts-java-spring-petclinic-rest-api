package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Vet entity.
 * Provides custom query methods for filtering by specialty and name search.
 */
@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

    /** Case-insensitive search by last name */
    List<Vet> findByLastNameContainingIgnoreCase(String lastName);

    /** Case-insensitive search by first name */
    List<Vet> findByFirstNameContainingIgnoreCase(String firstName);

    /** Find all vets that have a specific specialty by specialty ID */
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE s.id = :specialtyId")
    List<Vet> findBySpecialtyId(@Param("specialtyId") Integer specialtyId);

    /** Find vets by specialty name (case-insensitive partial match) */
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :specialtyName, '%'))")
    List<Vet> findBySpecialtyNameContainingIgnoreCase(@Param("specialtyName") String specialtyName);
}
