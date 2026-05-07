package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.service.SpecialtyService;
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
@RequestMapping("/api/specialties")
@Tag(name = "Specialties", description = "Endpoints for managing veterinary specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping
    @Operation(summary = "List specialties", description = "Returns all specialties, optionally filtered by name")
    @ApiResponse(responseCode = "200", description = "List of specialties")
    public ResponseEntity<List<SpecialtyResponseDto>> listSpecialties(
            @Parameter(description = "Filter by specialty name") @RequestParam(required = false) String name) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(specialtyService.searchByName(name));
        }
        return ResponseEntity.ok(specialtyService.findAll());
    }

    @PostMapping
    @Operation(summary = "Add a specialty", description = "Creates a new veterinary specialty")
    @ApiResponse(responseCode = "201", description = "Specialty created")
    public ResponseEntity<SpecialtyResponseDto> addSpecialty(
            @Valid @RequestBody SpecialtyRequestDto request) {
        SpecialtyResponseDto created = specialtyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{specialtyId}")
    @Operation(summary = "Get a specialty", description = "Returns a single specialty by ID")
    @ApiResponse(responseCode = "200", description = "Specialty found")
    @ApiResponse(responseCode = "404", description = "Specialty not found")
    public ResponseEntity<SpecialtyResponseDto> getSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.findById(specialtyId));
    }

    @PutMapping("/{specialtyId}")
    @Operation(summary = "Update a specialty", description = "Updates an existing specialty")
    @ApiResponse(responseCode = "200", description = "Specialty updated")
    @ApiResponse(responseCode = "404", description = "Specialty not found")
    public ResponseEntity<SpecialtyResponseDto> updateSpecialty(
            @PathVariable Integer specialtyId,
            @Valid @RequestBody SpecialtyRequestDto request) {
        return ResponseEntity.ok(specialtyService.update(specialtyId, request));
    }

    @DeleteMapping("/{specialtyId}")
    @Operation(summary = "Delete a specialty", description = "Deletes a specialty by ID")
    @ApiResponse(responseCode = "200", description = "Specialty deleted")
    @ApiResponse(responseCode = "404", description = "Specialty not found")
    public ResponseEntity<SpecialtyResponseDto> deleteSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.delete(specialtyId));
    }
}
