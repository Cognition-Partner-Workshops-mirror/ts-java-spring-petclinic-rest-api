package com.petclinic.vet.repository;

import com.petclinic.vet.entity.VetEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VetRepository extends JpaRepository<VetEntity, Integer> {

    @Query("SELECT DISTINCT v FROM VetEntity v JOIN v.specialties s WHERE s.id = :specialtyId")
    List<VetEntity> findBySpecialtyId(@Param("specialtyId") Integer specialtyId);

    @Query("SELECT v FROM VetEntity v WHERE LOWER(v.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<VetEntity> searchByLastName(@Param("name") String name);

    @Query("SELECT DISTINCT v FROM VetEntity v JOIN v.specialties s WHERE LOWER(s.name) = LOWER(:specialtyName)")
    List<VetEntity> findBySpecialtyName(@Param("specialtyName") String specialtyName);
}
