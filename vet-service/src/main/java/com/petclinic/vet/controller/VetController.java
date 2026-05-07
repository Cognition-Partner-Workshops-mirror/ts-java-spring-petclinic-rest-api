package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.service.VetService;
import jakarta.validation.Valid;
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
 * REST controller for vet CRUD and search operations.
 * Maps to /vets endpoints matching the PetClinic OpenAPI spec.
 * Supports optional query params (lastName, specialty) for filtering.
 */
@RestController
@RequestMapping("/vets")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    @GetMapping
    public ResponseEntity<List<VetDto>> listVets(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String specialty) {
        List<VetDto> result;
        if (lastName != null && specialty != null) {
            result = vetService.findByLastNameAndSpecialtyName(lastName, specialty);
        } else if (lastName != null) {
            result = vetService.findByLastName(lastName);
        } else if (specialty != null) {
            result = vetService.findBySpecialtyName(specialty);
        } else {
            result = vetService.findAll();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{vetId}")
    public ResponseEntity<VetDto> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.findById(vetId));
    }

    @PostMapping
    public ResponseEntity<VetDto> addVet(@Valid @RequestBody VetDto dto) {
        return ResponseEntity.ok(vetService.create(dto));
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetDto> updateVet(@PathVariable Integer vetId,
                                            @Valid @RequestBody VetDto dto) {
        return ResponseEntity.ok(vetService.update(vetId, dto));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetDto> deleteVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.delete(vetId));
    }
}
