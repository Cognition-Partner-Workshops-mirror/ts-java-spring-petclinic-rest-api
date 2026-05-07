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

@RestController
@RequestMapping("/api/vets")
@Tag(name = "Vets", description = "Endpoints for managing veterinarians")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    @GetMapping
    @Operation(summary = "List vets", description = "Returns all vets, optionally filtered by name or specialty")
    @ApiResponse(responseCode = "200", description = "List of vets")
    public ResponseEntity<List<VetResponseDto>> listVets(
            @Parameter(description = "Filter by vet name (first or last)") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by specialty ID") @RequestParam(required = false) Integer specialtyId) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(vetService.searchByName(name));
        }
        if (specialtyId != null) {
            return ResponseEntity.ok(vetService.findBySpecialtyId(specialtyId));
        }
        return ResponseEntity.ok(vetService.findAll());
    }

    @PostMapping
    @Operation(summary = "Add a vet", description = "Creates a new veterinarian")
    @ApiResponse(responseCode = "201", description = "Vet created")
    public ResponseEntity<VetResponseDto> addVet(@Valid @RequestBody VetRequestDto request) {
        VetResponseDto created = vetService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{vetId}")
    @Operation(summary = "Get a vet", description = "Returns a single vet by ID")
    @ApiResponse(responseCode = "200", description = "Vet found")
    @ApiResponse(responseCode = "404", description = "Vet not found")
    public ResponseEntity<VetResponseDto> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.findById(vetId));
    }

    @PutMapping("/{vetId}")
    @Operation(summary = "Update a vet", description = "Updates an existing veterinarian")
    @ApiResponse(responseCode = "200", description = "Vet updated")
    @ApiResponse(responseCode = "404", description = "Vet not found")
    public ResponseEntity<VetResponseDto> updateVet(
            @PathVariable Integer vetId,
            @Valid @RequestBody VetRequestDto request) {
        return ResponseEntity.ok(vetService.update(vetId, request));
    }

    @DeleteMapping("/{vetId}")
    @Operation(summary = "Delete a vet", description = "Deletes a veterinarian by ID")
    @ApiResponse(responseCode = "200", description = "Vet deleted")
    @ApiResponse(responseCode = "404", description = "Vet not found")
    public ResponseEntity<VetResponseDto> deleteVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.delete(vetId));
    }
}
