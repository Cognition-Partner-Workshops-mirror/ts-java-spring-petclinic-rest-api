package com.petclinic.vet.repository;

import com.petclinic.vet.entity.SpecialtyEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialtyRepository extends JpaRepository<SpecialtyEntity, Integer> {

    Optional<SpecialtyEntity> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
