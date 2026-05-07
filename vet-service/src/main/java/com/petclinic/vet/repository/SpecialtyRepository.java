package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {

    List<Specialty> findByNameIn(Set<String> names);

    Optional<Specialty> findByName(String name);
}
