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
 * REST controller for Vet endpoints matching the OpenAPI spec.
 * Handles CRUD operations on veterinarians and specialty-based filtering.
 */
@RestController
@RequestMapping("/vets")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    /**
     * GET /vets - List all vets, with optional filtering by lastName or specialtyName.
     * These query params extend the base OpenAPI spec for search/filtering support.
     */
    @GetMapping
    public ResponseEntity<List<VetResponseDto>> listVets(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String specialtyName) {
        if (lastName != null) {
            return ResponseEntity.ok(vetService.findByLastName(lastName));
        }
        if (specialtyName != null) {
            return ResponseEntity.ok(vetService.findBySpecialtyName(specialtyName));
        }
        return ResponseEntity.ok(vetService.getAllVets());
    }

    /** GET /vets/{vetId} - Get a vet by ID. */
    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.getVetById(vetId));
    }

    /** POST /vets - Create a new vet. */
    @PostMapping
    public ResponseEntity<VetResponseDto> addVet(@Valid @RequestBody VetRequestDto request) {
        VetResponseDto created = vetService.createVet(request);
        return ResponseEntity.status(HttpStatus.OK).body(created);
    }

    /** PUT /vets/{vetId} - Update a vet by ID. */
    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> updateVet(
            @PathVariable Integer vetId,
            @Valid @RequestBody VetRequestDto request) {
        return ResponseEntity.ok(vetService.updateVet(vetId, request));
    }

    /** DELETE /vets/{vetId} - Delete a vet by ID. */
    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> deleteVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.deleteVet(vetId));
    }
}
