package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Vet entities.
 * Includes custom query methods for filtering by specialty and name search.
 */
@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

    /** Find vets who have a specific specialty by specialty name (case-insensitive). */
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE LOWER(s.name) = LOWER(:specialtyName)")
    List<Vet> findBySpecialtyName(@Param("specialtyName") String specialtyName);

    /** Search vets by last name containing the given string (case-insensitive). */
    List<Vet> findByLastNameContainingIgnoreCase(String lastName);

    /** Search vets by first name or last name containing the given string (case-insensitive). */
    @Query("SELECT v FROM Vet v WHERE LOWER(v.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(v.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Vet> findByNameContaining(@Param("name") String name);
}
