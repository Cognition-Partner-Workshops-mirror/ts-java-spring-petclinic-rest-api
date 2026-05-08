package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
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
 * REST controller for managing veterinarians.
 * Endpoints match the OpenAPI spec paths under /api/vets.
 */
@RestController
@RequestMapping("/api/vets")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    /**
     * GET /api/vets — List all vets, or filter by specialty or last name if query params provided.
     */
    @GetMapping
    public ResponseEntity<List<VetDto>> listVets(
            @RequestParam(required = false) Integer specialtyId,
            @RequestParam(required = false) String lastName) {
        if (specialtyId != null) {
            return ResponseEntity.ok(vetService.findBySpecialty(specialtyId));
        }
        if (lastName != null && !lastName.isBlank()) {
            return ResponseEntity.ok(vetService.searchByLastName(lastName));
        }
        return ResponseEntity.ok(vetService.listVets());
    }

    /**
     * GET /api/vets/{vetId} — Get a vet by ID.
     */
    @GetMapping("/{vetId}")
    public ResponseEntity<VetDto> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.getVet(vetId));
    }

    /**
     * POST /api/vets — Create a new vet.
     */
    @PostMapping
    public ResponseEntity<VetDto> addVet(@Valid @RequestBody VetRequestDto request) {
        VetDto created = vetService.createVet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/vets/{vetId} — Update an existing vet.
     */
    @PutMapping("/{vetId}")
    public ResponseEntity<VetDto> updateVet(
            @PathVariable Integer vetId,
            @Valid @RequestBody VetRequestDto request) {
        return ResponseEntity.ok(vetService.updateVet(vetId, request));
    }

    /**
     * DELETE /api/vets/{vetId} — Delete a vet by ID.
     */
    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetDto> deleteVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.deleteVet(vetId));
    }
}
