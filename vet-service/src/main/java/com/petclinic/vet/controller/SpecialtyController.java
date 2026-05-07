package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.service.SpecialtyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;
    private final SpecialtyMapper specialtyMapper;

    public SpecialtyController(SpecialtyService specialtyService, SpecialtyMapper specialtyMapper) {
        this.specialtyService = specialtyService;
        this.specialtyMapper = specialtyMapper;
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyResponseDto>> listSpecialties() {
        List<Specialty> specialties = specialtyService.findAll();
        return ResponseEntity.ok(specialtyMapper.toResponseDtos(specialties));
    }

    @PostMapping
    public ResponseEntity<SpecialtyResponseDto> addSpecialty(@Valid @RequestBody SpecialtyRequestDto dto) {
        Specialty created = specialtyService.create(dto);
        SpecialtyResponseDto response = specialtyMapper.toResponseDto(created);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.getId())
            .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> getSpecialty(@PathVariable Integer specialtyId) {
        Specialty specialty = specialtyService.findById(specialtyId);
        return ResponseEntity.ok(specialtyMapper.toResponseDto(specialty));
    }

    @PutMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> updateSpecialty(@PathVariable Integer specialtyId,
                                                                 @Valid @RequestBody SpecialtyRequestDto dto) {
        Specialty updated = specialtyService.update(specialtyId, dto);
        return ResponseEntity.ok(specialtyMapper.toResponseDto(updated));
    }

    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<Void> deleteSpecialty(@PathVariable Integer specialtyId) {
        specialtyService.delete(specialtyId);
        return ResponseEntity.noContent().build();
    }
}
