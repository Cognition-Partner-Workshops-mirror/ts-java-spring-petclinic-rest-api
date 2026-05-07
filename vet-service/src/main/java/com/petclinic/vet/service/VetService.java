package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetRequest;
import com.petclinic.vet.dto.VetResponse;

import java.util.List;

/** Service interface for vet CRUD, specialty assignment, and search/filtering operations. */
public interface VetService {

    List<VetResponse> listAll();

    VetResponse getById(Integer id);

    VetResponse create(VetRequest request);

    VetResponse update(Integer id, VetRequest request);

    VetResponse delete(Integer id);

    List<VetResponse> searchByLastName(String lastName);

    List<VetResponse> filterBySpecialty(String specialtyName);

    List<VetResponse> searchByLastNameAndSpecialty(String lastName, String specialtyName);
}
