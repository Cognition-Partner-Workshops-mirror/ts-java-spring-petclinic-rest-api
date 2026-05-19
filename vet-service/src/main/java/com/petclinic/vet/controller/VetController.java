package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.service.VetService;
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
 * REST controller for vet endpoints.
 * Maps to /api/vets as defined in the OpenAPI spec, with additional
 * query-parameter-based search/filter capabilities.
 */
@RestController
@RequestMapping("/api/vets")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    /**
     * GET /api/vets — list all vets.
     * Supports optional query parameters for filtering:
     *   ?lastName=... — filter by last name (partial, case-insensitive)
     *   ?specialty=... — filter by specialty name (partial, case-insensitive)
     */
    @GetMapping
    public ResponseEntity<List<VetResponseDto>> listVets(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String specialty) {
        List<VetResponseDto> vets;
        if (lastName != null && !lastName.isBlank()) {
            vets = vetService.searchByLastName(lastName);
        } else if (specialty != null && !specialty.isBlank()) {
            vets = vetService.filterBySpecialty(specialty);
        } else {
            vets = vetService.getAllVets();
        }
        return ResponseEntity.ok(vets);
    }

    /** GET /api/vets/{vetId} — get a vet by ID */
    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> getVet(@PathVariable Integer vetId) {
        VetResponseDto vet = vetService.getVetById(vetId);
        return ResponseEntity.ok(vet);
    }

    /** POST /api/vets — create a new vet */
    @PostMapping
    public ResponseEntity<VetResponseDto> addVet(
            @Valid @RequestBody VetRequestDto request) {
        VetResponseDto created = vetService.createVet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** PUT /api/vets/{vetId} — update an existing vet */
    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> updateVet(
            @PathVariable Integer vetId,
            @Valid @RequestBody VetRequestDto request) {
        VetResponseDto updated = vetService.updateVet(vetId, request);
        return ResponseEntity.ok(updated);
    }

    /** DELETE /api/vets/{vetId} — delete a vet by ID */
    @DeleteMapping("/{vetId}")
    public ResponseEntity<Void> deleteVet(@PathVariable Integer vetId) {
        vetService.deleteVet(vetId);
        return ResponseEntity.noContent().build();
    }
}
