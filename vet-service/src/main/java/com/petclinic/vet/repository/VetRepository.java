package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

    @Query("SELECT DISTINCT v FROM Vet v LEFT JOIN FETCH v.specialties")
    List<Vet> findAllWithSpecialties();

    @Query("SELECT v FROM Vet v LEFT JOIN FETCH v.specialties WHERE v.id = :id")
    Optional<Vet> findByIdWithSpecialties(@Param("id") Integer id);

    @Query("SELECT DISTINCT v FROM Vet v LEFT JOIN FETCH v.specialties " +
           "WHERE LOWER(v.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<Vet> findByLastNameContainingIgnoreCase(@Param("lastName") String lastName);

    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s LEFT JOIN FETCH v.specialties " +
           "WHERE s.id = :specialtyId")
    List<Vet> findBySpecialtyId(@Param("specialtyId") Integer specialtyId);

    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s LEFT JOIN FETCH v.specialties " +
           "WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :specialtyName, '%'))")
    List<Vet> findBySpecialtyNameContainingIgnoreCase(@Param("specialtyName") String specialtyName);
}
