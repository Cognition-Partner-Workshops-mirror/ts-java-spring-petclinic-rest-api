package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {

    Optional<Specialty> findByNameIgnoreCase(String name);

    List<Specialty> findByNameInIgnoreCase(Collection<String> names);

    List<Specialty> findByNameContainingIgnoreCase(String fragment);
}
