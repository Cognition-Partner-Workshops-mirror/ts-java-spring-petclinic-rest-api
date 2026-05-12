package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Vet} entities.
 * Provides custom query methods for filtering by name and specialty.
 */
@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

    /** Find vets whose last name contains the given string (case-insensitive) */
    List<Vet> findByLastNameContainingIgnoreCase(String lastName);

    /** Find vets whose first name contains the given string (case-insensitive) */
    List<Vet> findByFirstNameContainingIgnoreCase(String firstName);

    /** Find vets that hold a specific specialty by specialty ID */
    @Query("SELECT v FROM Vet v JOIN v.specialties s WHERE s.id = :specialtyId")
    List<Vet> findBySpecialtyId(@Param("specialtyId") Integer specialtyId);

    /** Combined search: filter vets by last name substring and specialty ID */
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s " +
           "WHERE LOWER(v.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')) " +
           "AND s.id = :specialtyId")
    List<Vet> findByLastNameAndSpecialtyId(@Param("lastName") String lastName,
                                           @Param("specialtyId") Integer specialtyId);
}
