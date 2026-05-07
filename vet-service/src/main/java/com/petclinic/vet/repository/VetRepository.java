package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Spring Data JPA repository for Vet with custom queries for specialty and name search. */
@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

    // Filter vets by exact specialty name match via join
    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE s.name = :specialtyName")
    List<Vet> findBySpecialtyName(@Param("specialtyName") String specialtyName);

    // Case-insensitive partial match on first or last name
    @Query("SELECT v FROM Vet v WHERE LOWER(v.lastName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(v.firstName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Vet> findByNameContainingIgnoreCase(@Param("name") String name);
}
