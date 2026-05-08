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
 * REST controller for Specialty endpoints matching the OpenAPI spec.
 * Handles CRUD operations on veterinary specialties.
 */
@RestController
@RequestMapping("/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    /** GET /specialties - List all specialties. */
    @GetMapping
    public ResponseEntity<List<SpecialtyResponseDto>> listSpecialties() {
        return ResponseEntity.ok(specialtyService.getAllSpecialties());
    }

    /** GET /specialties/{specialtyId} - Get a specialty by ID. */
    @GetMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> getSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.getSpecialtyById(specialtyId));
    }

    /** POST /specialties - Create a new specialty. */
    @PostMapping
    public ResponseEntity<SpecialtyResponseDto> addSpecialty(@Valid @RequestBody SpecialtyRequestDto request) {
        SpecialtyResponseDto created = specialtyService.createSpecialty(request);
        return ResponseEntity.status(HttpStatus.OK).body(created);
    }

    /** PUT /specialties/{specialtyId} - Update a specialty by ID. */
    @PutMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> updateSpecialty(
            @PathVariable Integer specialtyId,
            @Valid @RequestBody SpecialtyRequestDto request) {
        return ResponseEntity.ok(specialtyService.updateSpecialty(specialtyId, request));
    }

    /** DELETE /specialties/{specialtyId} - Delete a specialty by ID. */
    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> deleteSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.deleteSpecialty(specialtyId));
    }
}
