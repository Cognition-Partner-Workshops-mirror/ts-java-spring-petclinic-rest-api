package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Vet entities.
 * Provides custom JPQL queries for filtering by specialty and searching by last name.
 */
@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

    /** Find all vets that hold a given specialty (exact match on specialty name). */
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE s.name = :specialtyName")
    List<Vet> findBySpecialtyName(@Param("specialtyName") String specialtyName);

    /** Case-insensitive partial match on vet last name. */
    @Query("SELECT v FROM Vet v WHERE LOWER(v.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<Vet> findByLastNameContainingIgnoreCase(@Param("lastName") String lastName);

    /** Combined filter: partial last-name match AND exact specialty name match. */
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s " +
           "WHERE LOWER(v.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')) " +
           "AND s.name = :specialtyName")
    List<Vet> findByLastNameContainingIgnoreCaseAndSpecialtyName(
        @Param("lastName") String lastName,
        @Param("specialtyName") String specialtyName);
}
