package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

    List<Vet> findByLastNameContainingIgnoreCase(String lastName);

    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE s.name = :specialtyName")
    List<Vet> findBySpecialtyName(@Param("specialtyName") String specialtyName);

    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Vet> findBySpecialtyNameContaining(@Param("name") String name);

    @Query("SELECT v FROM Vet v WHERE LOWER(v.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(v.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Vet> findByNameContaining(@Param("name") String name);
}
