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
 * REST controller for veterinarian CRUD and search operations.
 * Endpoints match the OpenAPI spec at /api/vets with optional query-param filtering.
 */
@RestController
@RequestMapping("/api/vets")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    // List all vets, or filter by lastName / specialtyId / specialtyName query params
    @GetMapping
    public ResponseEntity<List<VetResponseDto>> listVets(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Integer specialtyId,
            @RequestParam(required = false) String specialtyName) {
        List<VetResponseDto> result;
        if (lastName != null && !lastName.isBlank()) {
            result = vetService.findByLastName(lastName);
        } else if (specialtyId != null) {
            result = vetService.findBySpecialtyId(specialtyId);
        } else if (specialtyName != null && !specialtyName.isBlank()) {
            result = vetService.findBySpecialtyName(specialtyName);
        } else {
            result = vetService.findAll();
        }
        return ResponseEntity.ok(result);
    }

    // Retrieve a single vet by ID, returns 404 if not found
    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.findById(vetId));
    }

    // Create a new vet; @Valid triggers bean validation on the request DTO
    @PostMapping
    public ResponseEntity<VetResponseDto> addVet(@Valid @RequestBody VetRequestDto request) {
        VetResponseDto created = vetService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Update an existing vet's name and specialty assignments
    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> updateVet(
            @PathVariable Integer vetId,
            @Valid @RequestBody VetRequestDto request) {
        return ResponseEntity.ok(vetService.update(vetId, request));
    }

    // Delete a vet by ID; returns the deleted entity for confirmation
    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> deleteVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.delete(vetId));
    }
}
