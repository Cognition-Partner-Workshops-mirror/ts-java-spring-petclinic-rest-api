package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Vet entities.
 * Provides built-in CRUD plus custom query methods for filtering by specialty and name search.
 */
@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

    // Search vets by last name containing the given string (case-insensitive)
    @Query("SELECT v FROM Vet v WHERE LOWER(v.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<Vet> searchByLastName(@Param("lastName") String lastName);

    // Filter vets by specialty name (case-insensitive, joins through many-to-many)
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE LOWER(s.name) = LOWER(:specialtyName)")
    List<Vet> findBySpecialtyName(@Param("specialtyName") String specialtyName);

    // Combined search: filter by last name and specialty name
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s " +
           "WHERE LOWER(v.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')) " +
           "AND LOWER(s.name) = LOWER(:specialtyName)")
    List<Vet> findByLastNameAndSpecialtyName(
        @Param("lastName") String lastName,
        @Param("specialtyName") String specialtyName
    );
}
