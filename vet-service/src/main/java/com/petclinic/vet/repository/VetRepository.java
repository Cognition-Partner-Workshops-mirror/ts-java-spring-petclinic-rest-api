package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VetRepository extends JpaRepository<Vet, Integer> {

    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE s.id = :specialtyId")
    List<Vet> findBySpecialtyId(@Param("specialtyId") Integer specialtyId);

    @Query("SELECT v FROM Vet v WHERE LOWER(v.lastName) LIKE LOWER(CONCAT('%', :name, '%')) " +
        "OR LOWER(v.firstName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Vet> searchByName(@Param("name") String name);
}
