package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequest;
import com.petclinic.vet.dto.VetResponse;
import java.util.List;

public interface VetService {

    List<VetResponse> listAll();

    VetResponse getById(int id);

    VetResponse create(VetRequest request);

    VetResponse update(int id, VetRequest request);

    VetResponse delete(int id);

    List<VetResponse> findByLastName(String lastName);

    List<VetResponse> findBySpecialty(String specialtyName);
}
