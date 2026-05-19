package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.service.SpecialtyService;
import jakarta.validation.Valid;
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

import java.util.List;

/**
 * REST controller for specialty endpoints.
 * Maps to /api/specialties as defined in the OpenAPI spec.
 */
@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    /** GET /api/specialties — list all specialties */
    @GetMapping
    public ResponseEntity<List<SpecialtyResponseDto>> listSpecialties() {
        List<SpecialtyResponseDto> specialties = specialtyService.getAllSpecialties();
        return ResponseEntity.ok(specialties);
    }

    /** GET /api/specialties/{specialtyId} — get a specialty by ID */
    @GetMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> getSpecialty(
            @PathVariable Integer specialtyId) {
        SpecialtyResponseDto specialty = specialtyService.getSpecialtyById(specialtyId);
        return ResponseEntity.ok(specialty);
    }

    /** POST /api/specialties — create a new specialty */
    @PostMapping
    public ResponseEntity<SpecialtyResponseDto> addSpecialty(
            @Valid @RequestBody SpecialtyRequestDto request) {
        SpecialtyResponseDto created = specialtyService.createSpecialty(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** PUT /api/specialties/{specialtyId} — update an existing specialty */
    @PutMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> updateSpecialty(
            @PathVariable Integer specialtyId,
            @Valid @RequestBody SpecialtyRequestDto request) {
        SpecialtyResponseDto updated = specialtyService.updateSpecialty(specialtyId, request);
        return ResponseEntity.ok(updated);
    }

    /** DELETE /api/specialties/{specialtyId} — delete a specialty by ID */
    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<Void> deleteSpecialty(@PathVariable Integer specialtyId) {
        specialtyService.deleteSpecialty(specialtyId);
        return ResponseEntity.noContent().build();
    }
}
