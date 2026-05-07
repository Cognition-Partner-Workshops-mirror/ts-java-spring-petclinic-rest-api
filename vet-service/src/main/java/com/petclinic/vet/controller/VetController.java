package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.service.VetService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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

/**
 * REST controller for Vet CRUD operations.
 * Supports filtering by specialty name and searching by vet name.
 */
@RestController
@RequestMapping("/api/vets")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    // Supports optional query params: ?specialty=X or ?name=Y for filtering
    @GetMapping
    public ResponseEntity<List<VetResponseDto>> listVets(
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String name) {
        if (specialty != null && !specialty.isBlank()) {
            return ResponseEntity.ok(vetService.findBySpecialty(specialty));
        }
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(vetService.searchByName(name));
        }
        return ResponseEntity.ok(vetService.findAll());
    }

    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.findById(vetId));
    }

    @PostMapping
    public ResponseEntity<VetResponseDto> addVet(@Valid @RequestBody VetRequestDto request) {
        VetResponseDto created = vetService.create(request);
        URI location = URI.create("/api/vets/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> updateVet(
            @PathVariable Integer vetId,
            @Valid @RequestBody VetRequestDto request) {
        return ResponseEntity.ok(vetService.update(vetId, request));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<Void> deleteVet(@PathVariable Integer vetId) {
        vetService.delete(vetId);
        return ResponseEntity.noContent().build();
    }
}
