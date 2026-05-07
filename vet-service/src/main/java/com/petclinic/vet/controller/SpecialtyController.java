package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.service.SpecialtyService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/specialties")
public class SpecialtyController {

    private final SpecialtyService service;

    public SpecialtyController(SpecialtyService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyResponse>> listSpecialties() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<SpecialtyResponse> addSpecialty(@Valid @RequestBody SpecialtyRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponse> getSpecialty(@PathVariable int specialtyId) {
        return ResponseEntity.ok(service.findById(specialtyId));
    }

    @PutMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponse> updateSpecialty(
            @PathVariable int specialtyId,
            @Valid @RequestBody SpecialtyRequest request) {
        return ResponseEntity.ok(service.update(specialtyId, request));
    }

    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponse> deleteSpecialty(@PathVariable int specialtyId) {
        return ResponseEntity.ok(service.delete(specialtyId));
    }
}
