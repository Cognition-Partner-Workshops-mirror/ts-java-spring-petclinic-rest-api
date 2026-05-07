package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Specialty entity.
 * Provides custom query methods for searching by name.
 */
@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {

    /** Case-insensitive partial name search */
    List<Specialty> findByNameContainingIgnoreCase(String name);
}
