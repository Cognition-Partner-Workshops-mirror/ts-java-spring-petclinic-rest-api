package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Spring Data JPA repository for Specialty entities.
 */
@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {

    /**
     * Finds specialties whose names match any in the given set.
     */
    List<Specialty> findByNameIn(Set<String> names);

    /**
     * Finds a specialty by name, case-insensitive.
     */
    Optional<Specialty> findByNameIgnoreCase(String name);
}
