package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Vet} entities.
 * Provides custom query methods for filtering by specialty and name search.
 */
@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

    /** Find vets whose last name contains the given substring (case-insensitive) */
    List<Vet> findByLastNameContainingIgnoreCase(String lastName);

    /** Find vets who hold a specific specialty (by specialty name, case-insensitive) */
    @Query("SELECT v FROM Vet v JOIN v.specialties s WHERE LOWER(s.name) = LOWER(:specialtyName)")
    List<Vet> findBySpecialtyName(@Param("specialtyName") String specialtyName);

    /** Find vets by first name and last name containing (case-insensitive) */
    List<Vet> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
        String firstName, String lastName);
}
