package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;

import java.util.List;

public interface SpecialtyService {

    List<SpecialtyResponse> findAll();

    SpecialtyResponse findById(Integer id);

    SpecialtyResponse create(SpecialtyRequest request);

    SpecialtyResponse update(Integer id, SpecialtyRequest request);

    SpecialtyResponse delete(Integer id);
}
