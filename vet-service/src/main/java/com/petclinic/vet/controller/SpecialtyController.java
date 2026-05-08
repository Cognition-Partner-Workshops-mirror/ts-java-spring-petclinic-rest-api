package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;
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
 * REST controller for managing veterinary specialties.
 * Endpoints match the OpenAPI spec paths under /api/specialties.
 */
@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    /**
     * GET /api/specialties — List all specialties, or search by name if query param provided.
     */
    @GetMapping
    public ResponseEntity<List<SpecialtyDto>> listSpecialties(
            @RequestParam(required = false) String name) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(specialtyService.searchByName(name));
        }
        return ResponseEntity.ok(specialtyService.listSpecialties());
    }

    /**
     * GET /api/specialties/{specialtyId} — Get a specialty by ID.
     */
    @GetMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyDto> getSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.getSpecialty(specialtyId));
    }

    /**
     * POST /api/specialties — Create a new specialty.
     */
    @PostMapping
    public ResponseEntity<SpecialtyDto> addSpecialty(@Valid @RequestBody SpecialtyRequestDto request) {
        SpecialtyDto created = specialtyService.createSpecialty(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/specialties/{specialtyId} — Update an existing specialty.
     */
    @PutMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyDto> updateSpecialty(
            @PathVariable Integer specialtyId,
            @Valid @RequestBody SpecialtyRequestDto request) {
        return ResponseEntity.ok(specialtyService.updateSpecialty(specialtyId, request));
    }

    /**
     * DELETE /api/specialties/{specialtyId} — Delete a specialty by ID.
     */
    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyDto> deleteSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.deleteSpecialty(specialtyId));
    }
}
