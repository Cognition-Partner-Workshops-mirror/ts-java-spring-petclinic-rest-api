package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.service.SpecialtyService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for specialty endpoints matching the OpenAPI spec.
 * Provides CRUD operations for veterinary specialties.
 */
@RestController
@RequestMapping("/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyResponse>> listSpecialties() {
        return ResponseEntity.ok(specialtyService.listAll());
    }

    @GetMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponse> getSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.getById(specialtyId));
    }

    @PostMapping
    public ResponseEntity<SpecialtyResponse> addSpecialty(@Valid @RequestBody SpecialtyRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(specialtyService.create(request));
    }

    @PutMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponse> updateSpecialty(@PathVariable Integer specialtyId,
                                                              @Valid @RequestBody SpecialtyRequest request) {
        return ResponseEntity.ok(specialtyService.update(specialtyId, request));
    }

    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponse> deleteSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.delete(specialtyId));
    }
}
