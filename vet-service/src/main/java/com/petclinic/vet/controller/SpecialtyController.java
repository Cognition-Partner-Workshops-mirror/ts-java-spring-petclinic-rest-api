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
 * REST controller for veterinary specialties.
 * Endpoints match the OpenAPI paths /api/specialties and /api/specialties/{specialtyId}.
 */
@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    /** GET /api/specialties - Returns all specialties. */
    @GetMapping
    public ResponseEntity<List<SpecialtyResponseDto>> listSpecialties() {
        List<SpecialtyResponseDto> specialties = specialtyService.getAllSpecialties();
        return ResponseEntity.ok(specialties);
    }

    /** GET /api/specialties/{specialtyId} - Returns a single specialty by ID. */
    @GetMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> getSpecialty(
            @PathVariable Integer specialtyId) {
        SpecialtyResponseDto specialty = specialtyService.getSpecialtyById(specialtyId);
        return ResponseEntity.ok(specialty);
    }

    /** POST /api/specialties - Creates a new specialty. Validates the request body. */
    @PostMapping
    public ResponseEntity<SpecialtyResponseDto> addSpecialty(
            @Valid @RequestBody SpecialtyRequestDto dto) {
        SpecialtyResponseDto created = specialtyService.createSpecialty(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** PUT /api/specialties/{specialtyId} - Updates an existing specialty by ID. */
    @PutMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> updateSpecialty(
            @PathVariable Integer specialtyId,
            @Valid @RequestBody SpecialtyRequestDto dto) {
        SpecialtyResponseDto updated = specialtyService.updateSpecialty(specialtyId, dto);
        return ResponseEntity.ok(updated);
    }

    /** DELETE /api/specialties/{specialtyId} - Deletes a specialty by ID. */
    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> deleteSpecialty(
            @PathVariable Integer specialtyId) {
        SpecialtyResponseDto deleted = specialtyService.deleteSpecialty(specialtyId);
        return ResponseEntity.ok(deleted);
    }
}
