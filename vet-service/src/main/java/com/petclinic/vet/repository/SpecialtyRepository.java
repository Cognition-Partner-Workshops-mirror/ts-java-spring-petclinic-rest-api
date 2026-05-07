package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {

    List<Specialty> findByNameContainingIgnoreCase(String name);

    @Query("SELECT s FROM Specialty s WHERE s.name IN :names")
    List<Specialty> findByNameIn(@Param("names") Set<String> names);
}
