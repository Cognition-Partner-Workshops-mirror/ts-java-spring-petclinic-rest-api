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

@RestController
@RequestMapping("/vets")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    @GetMapping
    public ResponseEntity<List<VetResponseDto>> listVets(
        @RequestParam(required = false) String lastName,
        @RequestParam(required = false) Integer specialtyId,
        @RequestParam(required = false) String specialtyName) {

        List<VetResponseDto> vets;
        if (lastName != null && !lastName.isBlank()) {
            vets = vetService.searchByLastName(lastName);
        } else if (specialtyId != null) {
            vets = vetService.findBySpecialtyId(specialtyId);
        } else if (specialtyName != null && !specialtyName.isBlank()) {
            vets = vetService.findBySpecialtyName(specialtyName);
        } else {
            vets = vetService.findAll();
        }
        return ResponseEntity.ok(vets);
    }

    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.findById(vetId));
    }

    @PostMapping
    public ResponseEntity<VetResponseDto> addVet(@Valid @RequestBody VetRequestDto dto) {
        VetResponseDto created = vetService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> updateVet(
        @PathVariable Integer vetId,
        @Valid @RequestBody VetRequestDto dto) {
        return ResponseEntity.ok(vetService.update(vetId, dto));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> deleteVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.delete(vetId));
    }

    @PostMapping("/{vetId}/specialties")
    public ResponseEntity<VetResponseDto> assignSpecialties(
        @PathVariable Integer vetId,
        @RequestBody List<Integer> specialtyIds) {
        return ResponseEntity.ok(vetService.assignSpecialties(vetId, specialtyIds));
    }
}
