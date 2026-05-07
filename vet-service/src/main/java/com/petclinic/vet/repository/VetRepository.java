package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

    List<Vet> findByLastNameContainingIgnoreCase(String lastName);

    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE s.name = :specialtyName")
    List<Vet> findBySpecialtyName(@Param("specialtyName") String specialtyName);

    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :specialtyName, '%'))")
    List<Vet> findBySpecialtyNameContainingIgnoreCase(@Param("specialtyName") String specialtyName);
}
