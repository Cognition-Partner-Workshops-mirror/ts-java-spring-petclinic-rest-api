package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {

    @Query("SELECT s FROM Specialty s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Specialty> findByNameContainingIgnoreCase(@Param("name") String name);
}
