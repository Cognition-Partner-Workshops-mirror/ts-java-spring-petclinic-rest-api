package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;

import java.util.List;

/** Service interface for specialty CRUD operations. */
public interface SpecialtyService {

    List<SpecialtyResponse> listAll();

    SpecialtyResponse getById(Integer id);

    SpecialtyResponse create(SpecialtyRequest request);

    SpecialtyResponse update(Integer id, SpecialtyRequest request);

    SpecialtyResponse delete(Integer id);
}
