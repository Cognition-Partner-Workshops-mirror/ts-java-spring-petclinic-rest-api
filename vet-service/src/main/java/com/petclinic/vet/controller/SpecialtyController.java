package com.petclinic.vet.controller;

import com.petclinic.vet.dto.request.SpecialtyRequest;
import com.petclinic.vet.dto.response.SpecialtyResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyResponse>> listSpecialties(
            @RequestParam(required = false) String name) {
        List<SpecialtyResponse> specialties;
        if (name != null) {
            specialties = specialtyService.searchByName(name);
        } else {
            specialties = specialtyService.listSpecialties();
        }
        return ResponseEntity.ok(specialties);
    }

    @GetMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponse> getSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.getSpecialty(specialtyId));
    }

    @PostMapping
    public ResponseEntity<SpecialtyResponse> addSpecialty(@Valid @RequestBody SpecialtyRequest request) {
        SpecialtyResponse created = specialtyService.createSpecialty(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponse> updateSpecialty(
            @PathVariable Integer specialtyId,
            @Valid @RequestBody SpecialtyRequest request) {
        return ResponseEntity.ok(specialtyService.updateSpecialty(specialtyId, request));
    }

    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponse> deleteSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.deleteSpecialty(specialtyId));
    }
}
