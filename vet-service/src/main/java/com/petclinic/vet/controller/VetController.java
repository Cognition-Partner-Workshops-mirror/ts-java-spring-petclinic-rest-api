package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.service.VetService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vets")
public class VetController {

    private final VetService service;

    public VetController(VetService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<VetResponseDto>> listVets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialty) {
        if (specialty != null && !specialty.isBlank()) {
            return ResponseEntity.ok(service.filterBySpecialty(specialty));
        }
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(service.searchByName(name));
        }
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(service.getById(vetId));
    }

    @PostMapping
    public ResponseEntity<VetResponseDto> addVet(@Valid @RequestBody VetRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> updateVet(
            @PathVariable Integer vetId,
            @Valid @RequestBody VetRequestDto dto) {
        return ResponseEntity.ok(service.update(vetId, dto));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> deleteVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(service.delete(vetId));
    }
}
