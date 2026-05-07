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
 * REST controller for veterinarian endpoints.
 * Maps to /vets as defined in the OpenAPI spec.
 * Supports filtering by lastName, specialtyId, and specialtyName query parameters.
 */
@RestController
@RequestMapping("/vets")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    /**
     * GET /vets — list all vets with optional filtering.
     * Supports query params: lastName, specialtyId, specialtyName.
     */
    @GetMapping
    public ResponseEntity<List<VetResponseDto>> listVets(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Integer specialtyId,
            @RequestParam(required = false) String specialtyName) {
        List<VetResponseDto> result;
        if (lastName != null && !lastName.isBlank()) {
            result = vetService.searchByLastName(lastName);
        } else if (specialtyId != null) {
            result = vetService.filterBySpecialtyId(specialtyId);
        } else if (specialtyName != null && !specialtyName.isBlank()) {
            result = vetService.filterBySpecialtyName(specialtyName);
        } else {
            result = vetService.listVets();
        }
        return ResponseEntity.ok(result);
    }

    /** POST /vets — create a new vet */
    @PostMapping
    public ResponseEntity<VetResponseDto> addVet(
            @Valid @RequestBody VetRequestDto request) {
        VetResponseDto created = vetService.addVet(request);
        return ResponseEntity.status(HttpStatus.OK).body(created);
    }

    /** GET /vets/{vetId} — get a vet by ID */
    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> getVet(@PathVariable Integer vetId) {
        VetResponseDto vet = vetService.getVet(vetId);
        return ResponseEntity.ok(vet);
    }

    /** PUT /vets/{vetId} — update a vet by ID */
    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> updateVet(
            @PathVariable Integer vetId,
            @Valid @RequestBody VetRequestDto request) {
        VetResponseDto updated = vetService.updateVet(vetId, request);
        return ResponseEntity.ok(updated);
    }

    /** DELETE /vets/{vetId} — delete a vet by ID */
    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> deleteVet(@PathVariable Integer vetId) {
        VetResponseDto deleted = vetService.deleteVet(vetId);
        return ResponseEntity.ok(deleted);
    }
}
