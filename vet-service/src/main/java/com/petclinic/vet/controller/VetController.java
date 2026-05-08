package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.service.VetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for vet endpoints.
 * Maps to /vets as defined in the OpenAPI spec.
 */
@RestController
@RequestMapping("/vets")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    /**
     * GET /vets - List all vets.
     * Supports optional query parameters for filtering by lastName or specialtyId.
     */
    @GetMapping
    public ResponseEntity<List<VetResponseDto>> listVets(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Integer specialtyId) {
        List<VetResponseDto> vets;
        if (lastName != null && !lastName.isBlank()) {
            // Filter by last name search
            vets = vetService.searchByLastName(lastName);
        } else if (specialtyId != null) {
            // Filter by specialty
            vets = vetService.filterBySpecialty(specialtyId);
        } else {
            // Return all vets
            vets = vetService.getAllVets();
        }
        return ResponseEntity.ok(vets);
    }

    /**
     * GET /vets/{vetId} - Get a vet by ID.
     */
    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> getVet(@PathVariable Integer vetId) {
        VetResponseDto vet = vetService.getVetById(vetId);
        return ResponseEntity.ok(vet);
    }

    /**
     * POST /vets - Create a new vet.
     */
    @PostMapping
    public ResponseEntity<VetResponseDto> addVet(@Valid @RequestBody VetRequestDto request) {
        VetResponseDto created = vetService.createVet(request);
        return ResponseEntity.status(HttpStatus.OK).body(created);
    }

    /**
     * PUT /vets/{vetId} - Update a vet by ID.
     */
    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> updateVet(
            @PathVariable Integer vetId,
            @Valid @RequestBody VetRequestDto request) {
        VetResponseDto updated = vetService.updateVet(vetId, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /vets/{vetId} - Delete a vet by ID.
     */
    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> deleteVet(@PathVariable Integer vetId) {
        VetResponseDto deleted = vetService.deleteVet(vetId);
        return ResponseEntity.ok(deleted);
    }
}
