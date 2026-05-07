package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequest;
import com.petclinic.vet.dto.VetResponse;

import java.util.List;

public interface VetService {

    List<VetResponse> findAll();

    VetResponse findById(Integer id);

    VetResponse create(VetRequest request);

    VetResponse update(Integer id, VetRequest request);

    VetResponse delete(Integer id);

    List<VetResponse> findBySpecialty(String specialtyName);

    List<VetResponse> searchByName(String name);
}
