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
 * REST controller for veterinarians.
 * Endpoints match the OpenAPI paths /api/vets and /api/vets/{vetId}.
 * Additional search/filter endpoints extend the base CRUD set.
 */
@RestController
@RequestMapping("/api/vets")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    /** GET /api/vets - Returns all vets. */
    @GetMapping
    public ResponseEntity<List<VetResponseDto>> listVets() {
        List<VetResponseDto> vets = vetService.getAllVets();
        return ResponseEntity.ok(vets);
    }

    /** GET /api/vets/{vetId} - Returns a single vet by ID. */
    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> getVet(@PathVariable Integer vetId) {
        VetResponseDto vet = vetService.getVetById(vetId);
        return ResponseEntity.ok(vet);
    }

    /** POST /api/vets - Creates a new vet. Validates the request body. */
    @PostMapping
    public ResponseEntity<VetResponseDto> addVet(@Valid @RequestBody VetRequestDto dto) {
        VetResponseDto created = vetService.createVet(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** PUT /api/vets/{vetId} - Updates an existing vet by ID. */
    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> updateVet(
            @PathVariable Integer vetId,
            @Valid @RequestBody VetRequestDto dto) {
        VetResponseDto updated = vetService.updateVet(vetId, dto);
        return ResponseEntity.ok(updated);
    }

    /** DELETE /api/vets/{vetId} - Deletes a vet by ID. */
    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> deleteVet(@PathVariable Integer vetId) {
        VetResponseDto deleted = vetService.deleteVet(vetId);
        return ResponseEntity.ok(deleted);
    }

    /**
     * GET /api/vets/search?name={name} - Searches vets by first or last name.
     * Extends the OpenAPI spec with a convenient search endpoint.
     */
    @GetMapping("/search")
    public ResponseEntity<List<VetResponseDto>> searchVets(
            @RequestParam String name) {
        List<VetResponseDto> vets = vetService.searchByName(name);
        return ResponseEntity.ok(vets);
    }

    /**
     * GET /api/vets/specialty/{specialtyId} - Filters vets by specialty.
     * Extends the OpenAPI spec with a filter-by-specialty endpoint.
     */
    @GetMapping("/specialty/{specialtyId}")
    public ResponseEntity<List<VetResponseDto>> findBySpecialty(
            @PathVariable Integer specialtyId) {
        List<VetResponseDto> vets = vetService.findBySpecialtyId(specialtyId);
        return ResponseEntity.ok(vets);
    }
}
