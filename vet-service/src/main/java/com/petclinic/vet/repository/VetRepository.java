package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Vet} entities.
 * Provides built-in CRUD plus custom queries for filtering by specialty and name search.
 */
@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

    /** Finds all vets that hold a specialty with the given ID. */
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE s.id = :specialtyId")
    List<Vet> findBySpecialtyId(@Param("specialtyId") Integer specialtyId);

    /** Finds vets whose last name contains the given substring (case-insensitive). */
    @Query("SELECT v FROM Vet v WHERE LOWER(v.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<Vet> findByLastNameContainingIgnoreCase(@Param("lastName") String lastName);

    /**
     * Full-text style search across first and last name (case-insensitive).
     * Useful for a general search box on the UI.
     */
    @Query("SELECT v FROM Vet v WHERE LOWER(v.firstName) LIKE LOWER(CONCAT('%', :name, '%')) "
         + "OR LOWER(v.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Vet> searchByName(@Param("name") String name);
}
