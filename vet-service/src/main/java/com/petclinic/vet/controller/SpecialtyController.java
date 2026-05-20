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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for Specialty CRUD operations.
 * Maps to /api/specialties as defined in the OpenAPI spec.
 */
@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    /**
     * GET /api/specialties — List all specialties.
     * Supports optional name search via query parameter.
     */
    @GetMapping
    public ResponseEntity<List<SpecialtyResponseDto>> listSpecialties(
        @RequestParam(required = false) String name) {
        List<SpecialtyResponseDto> specialties;
        if (name != null && !name.isBlank()) {
            // Filter by name if query parameter is provided
            specialties = specialtyService.searchByName(name);
        } else {
            specialties = specialtyService.findAll();
        }
        return ResponseEntity.ok(specialties);
    }

    /**
     * GET /api/specialties/{specialtyId} — Get a specialty by ID.
     */
    @GetMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> getSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.findById(specialtyId));
    }

    /**
     * POST /api/specialties — Create a new specialty.
     */
    @PostMapping
    public ResponseEntity<SpecialtyResponseDto> addSpecialty(@Valid @RequestBody SpecialtyRequestDto request) {
        SpecialtyResponseDto created = specialtyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/specialties/{specialtyId} — Update an existing specialty.
     */
    @PutMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> updateSpecialty(@PathVariable Integer specialtyId,
                                                                @Valid @RequestBody SpecialtyRequestDto request) {
        return ResponseEntity.ok(specialtyService.update(specialtyId, request));
    }

    /**
     * DELETE /api/specialties/{specialtyId} — Delete a specialty by ID.
     */
    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<Void> deleteSpecialty(@PathVariable Integer specialtyId) {
        specialtyService.delete(specialtyId);
        return ResponseEntity.noContent().build();
    }
}
