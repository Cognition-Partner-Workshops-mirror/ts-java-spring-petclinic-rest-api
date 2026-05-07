package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Spring Data JPA repository for Specialty entities. Supports lookup by name. */
@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {

    Optional<Specialty> findByNameIgnoreCase(String name);
}
