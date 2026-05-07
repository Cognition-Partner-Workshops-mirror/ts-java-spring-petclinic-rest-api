package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import java.util.List;

public interface SpecialtyService {

    List<SpecialtyResponse> listAll();

    SpecialtyResponse getById(int id);

    SpecialtyResponse create(SpecialtyRequest request);

    SpecialtyResponse update(int id, SpecialtyRequest request);

    SpecialtyResponse delete(int id);
}
