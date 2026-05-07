package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.service.VetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/specialties")
public class SpecialtyRestController {

    private final VetService vetService;
    private final SpecialtyMapper specialtyMapper;

    public SpecialtyRestController(VetService vetService, SpecialtyMapper specialtyMapper) {
        this.vetService = vetService;
        this.specialtyMapper = specialtyMapper;
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyDto>> listSpecialties() {
        List<Specialty> specialties = vetService.findAllSpecialties();
        return ResponseEntity.ok(specialtyMapper.toDtoList(specialties));
    }

    @GetMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyDto> getSpecialty(@PathVariable Integer specialtyId) {
        Specialty specialty = vetService.findSpecialtyById(specialtyId);
        return ResponseEntity.ok(specialtyMapper.toDto(specialty));
    }

    @PostMapping
    public ResponseEntity<SpecialtyDto> addSpecialty(@Valid @RequestBody SpecialtyDto dto, UriComponentsBuilder ucb) {
        Specialty entity = specialtyMapper.toEntity(dto);
        Specialty saved = vetService.saveSpecialty(entity);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucb.path("/specialties/{id}").buildAndExpand(saved.getId()).toUri());
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(specialtyMapper.toDto(saved));
    }

    @PutMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyDto> updateSpecialty(@PathVariable Integer specialtyId,
                                                        @Valid @RequestBody SpecialtyDto dto) {
        Specialty entity = specialtyMapper.toEntity(dto);
        Specialty saved = vetService.updateSpecialty(specialtyId, entity);
        return ResponseEntity.ok(specialtyMapper.toDto(saved));
    }

    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<Void> deleteSpecialty(@PathVariable Integer specialtyId) {
        vetService.deleteSpecialty(specialtyId);
        return ResponseEntity.noContent().build();
    }
}
