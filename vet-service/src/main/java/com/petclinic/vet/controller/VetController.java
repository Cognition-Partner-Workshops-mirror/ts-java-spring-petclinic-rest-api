package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.service.VetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * Maps to /api/vets as defined in the OpenAPI spec.
 * Supports filtering by specialty and name search via query parameters.
 */
@RestController
@RequestMapping("/api/vets")
@Tag(name = "Vets", description = "Endpoints for managing veterinarians")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    /**
     * GET /api/vets — List all vets.
     * Supports optional filtering by specialty name or name search via query parameters.
     */
    @GetMapping
    @Operation(summary = "List vets", description = "Returns all vets, optionally filtered by specialty or name")
    public ResponseEntity<List<VetResponseDto>> listVets(
        @Parameter(description = "Filter by specialty name") @RequestParam(required = false) String specialty,
        @Parameter(description = "Search by first or last name") @RequestParam(required = false) String name) {
        List<VetResponseDto> vets;
        if (specialty != null && !specialty.isBlank()) {
            // Filter by specialty name
            vets = vetService.findBySpecialty(specialty);
        } else if (name != null && !name.isBlank()) {
            // Search by first or last name
            vets = vetService.searchByName(name);
        } else {
            vets = vetService.findAll();
        }
        return ResponseEntity.ok(vets);
    }

    /**
     * GET /api/vets/{vetId} — Get a vet by ID.
     */
    @GetMapping("/{vetId}")
    @Operation(summary = "Get vet by ID", responses = {
        @ApiResponse(responseCode = "200", description = "Vet found"),
        @ApiResponse(responseCode = "404", description = "Vet not found")})
    public ResponseEntity<VetResponseDto> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.findById(vetId));
    }

    /**
     * POST /api/vets — Create a new vet.
     * Specialties are resolved by name from existing specialty records.
     */
    @PostMapping
    @Operation(summary = "Create a vet", description = "Specialties are resolved by name from existing records")
    @ApiResponse(responseCode = "201", description = "Vet created")
    public ResponseEntity<VetResponseDto> addVet(@Valid @RequestBody VetRequestDto request) {
        VetResponseDto created = vetService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/vets/{vetId} — Update an existing vet.
     */
    @PutMapping("/{vetId}")
    @Operation(summary = "Update a vet")
    public ResponseEntity<VetResponseDto> updateVet(@PathVariable Integer vetId,
                                                    @Valid @RequestBody VetRequestDto request) {
        return ResponseEntity.ok(vetService.update(vetId, request));
    }

    /**
     * DELETE /api/vets/{vetId} — Delete a vet by ID.
     */
    @DeleteMapping("/{vetId}")
    @Operation(summary = "Delete a vet")
    @ApiResponse(responseCode = "204", description = "Vet deleted")
    public ResponseEntity<Void> deleteVet(@PathVariable Integer vetId) {
        vetService.delete(vetId);
        return ResponseEntity.noContent().build();
    }
}
