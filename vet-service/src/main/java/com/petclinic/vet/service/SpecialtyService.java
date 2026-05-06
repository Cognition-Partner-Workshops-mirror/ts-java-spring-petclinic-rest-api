package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import java.util.List;

public interface SpecialtyService {

    List<SpecialtyDto> findAll();

    SpecialtyDto findById(Integer id);

    SpecialtyDto create(SpecialtyDto dto);

    SpecialtyDto update(Integer id, SpecialtyDto dto);

    void delete(Integer id);
}
