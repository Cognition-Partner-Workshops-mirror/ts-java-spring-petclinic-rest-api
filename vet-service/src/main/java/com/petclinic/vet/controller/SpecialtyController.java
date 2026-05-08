package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.service.SpecialtyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for specialty endpoints.
 * Maps to /specialties as defined in the OpenAPI spec.
 */
@RestController
@RequestMapping("/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    /**
     * GET /specialties - List all specialties.
     */
    @GetMapping
    public ResponseEntity<List<SpecialtyResponseDto>> listSpecialties() {
        List<SpecialtyResponseDto> specialties = specialtyService.getAllSpecialties();
        return ResponseEntity.ok(specialties);
    }

    /**
     * GET /specialties/{specialtyId} - Get a specialty by ID.
     */
    @GetMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> getSpecialty(@PathVariable Integer specialtyId) {
        SpecialtyResponseDto specialty = specialtyService.getSpecialtyById(specialtyId);
        return ResponseEntity.ok(specialty);
    }

    /**
     * POST /specialties - Create a new specialty.
     */
    @PostMapping
    public ResponseEntity<SpecialtyResponseDto> addSpecialty(@Valid @RequestBody SpecialtyRequestDto request) {
        SpecialtyResponseDto created = specialtyService.createSpecialty(request);
        return ResponseEntity.status(HttpStatus.OK).body(created);
    }

    /**
     * PUT /specialties/{specialtyId} - Update a specialty by ID.
     */
    @PutMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> updateSpecialty(
            @PathVariable Integer specialtyId,
            @Valid @RequestBody SpecialtyRequestDto request) {
        SpecialtyResponseDto updated = specialtyService.updateSpecialty(specialtyId, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /specialties/{specialtyId} - Delete a specialty by ID.
     */
    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> deleteSpecialty(@PathVariable Integer specialtyId) {
        SpecialtyResponseDto deleted = specialtyService.deleteSpecialty(specialtyId);
        return ResponseEntity.ok(deleted);
    }
}
