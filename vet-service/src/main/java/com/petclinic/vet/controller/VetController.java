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
 * REST controller for vet CRUD and search/filter operations.
 * Endpoints match the OpenAPI /vets paths with additional filtering query parameters.
 */
@RestController
@RequestMapping("/api/vets")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    /**
     * GET /api/vets — list all vets with optional filtering.
     * Supports filtering by lastName (substring), specialtyId, or both.
     */
    @GetMapping
    public ResponseEntity<List<VetResponseDto>> listVets(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Integer specialtyId) {
        List<VetResponseDto> result;
        if (lastName != null && specialtyId != null) {
            // Combined filter: last name + specialty
            result = vetService.filterByLastNameAndSpecialty(lastName, specialtyId);
        } else if (lastName != null) {
            result = vetService.searchByLastName(lastName);
        } else if (specialtyId != null) {
            result = vetService.filterBySpecialty(specialtyId);
        } else {
            result = vetService.getAllVets();
        }
        return ResponseEntity.ok(result);
    }

    /** GET /api/vets/{vetId} — get a single vet by ID */
    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> getVet(@PathVariable Integer vetId) {
        VetResponseDto dto = vetService.getVetById(vetId);
        return ResponseEntity.ok(dto);
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

    /** DELETE /api/vets/{vetId} — delete a vet */
    @DeleteMapping("/{vetId}")
    public ResponseEntity<Void> deleteVet(@PathVariable Integer vetId) {
        vetService.deleteVet(vetId);
        return ResponseEntity.noContent().build();
    }
}
