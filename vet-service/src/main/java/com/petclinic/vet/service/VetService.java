package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.entity.Vet;

import java.util.List;

public interface VetService {

    List<Vet> findAll();

    Vet findById(int id);

    Vet create(VetRequestDto dto);

    Vet update(int id, VetRequestDto dto);

    void delete(int id);

    List<Vet> findBySpecialtyName(String name);

    List<Vet> findByLastName(String lastName);
}
