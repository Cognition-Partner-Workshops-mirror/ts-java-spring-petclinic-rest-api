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
 * REST controller for Vet CRUD operations.
 * Endpoints match the OpenAPI spec: /api/vets and /api/vets/{vetId}.
 * Also provides search/filter endpoints for lastName and specialty.
 */
@RestController
@RequestMapping("/api/vets")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    /** GET /api/vets — list all vets, with optional filtering by lastName or specialty */
    @GetMapping
    public ResponseEntity<List<VetResponseDto>> listVets(
        @RequestParam(required = false) String lastName,
        @RequestParam(required = false) String specialty) {

        List<VetResponseDto> result;
        if (lastName != null && !lastName.isBlank()) {
            // Search by last name substring
            result = vetService.searchByLastName(lastName);
        } else if (specialty != null && !specialty.isBlank()) {
            // Filter by specialty name
            result = vetService.findBySpecialtyName(specialty);
        } else {
            result = vetService.findAll();
        }
        return ResponseEntity.ok(result);
    }

    /** GET /api/vets/{vetId} — get a vet by ID */
    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.findById(vetId));
    }

    /** POST /api/vets — create a new vet */
    @PostMapping
    public ResponseEntity<VetResponseDto> addVet(
        @Valid @RequestBody VetRequestDto request) {

        VetResponseDto created = vetService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** PUT /api/vets/{vetId} — update an existing vet */
    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> updateVet(
        @PathVariable Integer vetId,
        @Valid @RequestBody VetRequestDto request) {

        return ResponseEntity.ok(vetService.update(vetId, request));
    }

    /** DELETE /api/vets/{vetId} — delete a vet by ID */
    @DeleteMapping("/{vetId}")
    public ResponseEntity<Void> deleteVet(@PathVariable Integer vetId) {
        vetService.delete(vetId);
        return ResponseEntity.noContent().build();
    }
}
