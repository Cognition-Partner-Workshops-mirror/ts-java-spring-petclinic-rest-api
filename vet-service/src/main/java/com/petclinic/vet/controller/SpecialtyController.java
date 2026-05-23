package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyDto;
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

import java.util.List;

/**
 * REST controller for Specialty endpoints matching the OpenAPI spec.
 */
@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;
    private final SpecialtyMapper specialtyMapper;

    public SpecialtyController(SpecialtyService specialtyService, SpecialtyMapper specialtyMapper) {
        this.specialtyService = specialtyService;
        this.specialtyMapper = specialtyMapper;
    }

    /**
     * GET /api/specialties - Returns all specialties.
     */
    @GetMapping
    public ResponseEntity<List<SpecialtyDto>> listSpecialties() {
        List<Specialty> specialties = specialtyService.findAll();
        return ResponseEntity.ok(specialtyMapper.toDtos(specialties));
    }

    /**
     * POST /api/specialties - Creates a new specialty, returns 200 with SpecialtyDto.
     */
    @PostMapping
    public ResponseEntity<SpecialtyDto> addSpecialty(@Valid @RequestBody SpecialtyDto specialtyDto) {
        Specialty saved = specialtyService.save(specialtyDto);
        return ResponseEntity.ok(specialtyMapper.toDto(saved));
    }

    /**
     * GET /api/specialties/{specialtyId} - Returns a specialty by ID, 404 if not found.
     */
    @GetMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyDto> getSpecialty(@PathVariable Integer specialtyId) {
        Specialty specialty = specialtyService.findById(specialtyId);
        return ResponseEntity.ok(specialtyMapper.toDto(specialty));
    }

    /**
     * PUT /api/specialties/{specialtyId} - Updates a specialty, 404 if not found.
     */
    @PutMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyDto> updateSpecialty(@PathVariable Integer specialtyId,
                                                         @Valid @RequestBody SpecialtyDto specialtyDto) {
        Specialty updated = specialtyService.update(specialtyId, specialtyDto);
        return ResponseEntity.ok(specialtyMapper.toDto(updated));
    }

    /**
     * DELETE /api/specialties/{specialtyId} - Deletes a specialty, returns it, 404 if not found.
     */
    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyDto> deleteSpecialty(@PathVariable Integer specialtyId) {
        Specialty specialty = specialtyService.findById(specialtyId);
        SpecialtyDto response = specialtyMapper.toDto(specialty);
        specialtyService.delete(specialtyId);
        return ResponseEntity.ok(response);
    }
}
