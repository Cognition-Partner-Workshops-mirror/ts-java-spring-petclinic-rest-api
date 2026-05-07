package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.mapper.VetMapper;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

// REST controller for vet CRUD operations, specialty assignment, and search/filtering
@RestController
@RequestMapping("/api/vets")
public class VetController {

    private final VetService vetService;
    private final VetMapper vetMapper;

    public VetController(VetService vetService, VetMapper vetMapper) {
        this.vetService = vetService;
        this.vetMapper = vetMapper;
    }

    @GetMapping
    public ResponseEntity<List<VetResponseDto>> listVets() {
        List<VetResponseDto> vets = vetMapper.toResponseDtos(vetService.findAll());
        return ResponseEntity.ok(vets);
    }

    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> getVet(@PathVariable int vetId) {
        VetResponseDto dto = vetMapper.toResponseDto(vetService.findById(vetId));
        return ResponseEntity.ok(dto);
    }

    // Creates a new vet and returns 201 Created with Location header pointing to the new resource
    @PostMapping
    public ResponseEntity<VetResponseDto> addVet(@Valid @RequestBody VetRequestDto request, UriComponentsBuilder ucb) {
        VetResponseDto created = vetMapper.toResponseDto(vetService.create(request));
        return ResponseEntity
            .created(ucb.path("/api/vets/{id}").buildAndExpand(created.getId()).toUri())
            .body(created);
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> updateVet(@PathVariable int vetId, @Valid @RequestBody VetRequestDto request) {
        VetResponseDto updated = vetMapper.toResponseDto(vetService.update(vetId, request));
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<Void> deleteVet(@PathVariable int vetId) {
        vetService.delete(vetId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<VetResponseDto>> searchVets(@RequestParam String name) {
        List<VetResponseDto> vets = vetMapper.toResponseDtos(vetService.searchByName(name));
        return ResponseEntity.ok(vets);
    }

    @GetMapping("/specialty/{specialtyName}")
    public ResponseEntity<List<VetResponseDto>> findBySpecialty(@PathVariable String specialtyName) {
        List<VetResponseDto> vets = vetMapper.toResponseDtos(vetService.findBySpecialty(specialtyName));
        return ResponseEntity.ok(vets);
    }

    // Assigns an existing specialty to a vet by their respective IDs
    @PutMapping("/{vetId}/specialties/{specialtyId}")
    public ResponseEntity<VetResponseDto> assignSpecialty(@PathVariable int vetId, @PathVariable int specialtyId) {
        VetResponseDto dto = vetMapper.toResponseDto(vetService.assignSpecialty(vetId, specialtyId));
        return ResponseEntity.ok(dto);
    }

    // Removes a specialty from a vet by their respective IDs
    @DeleteMapping("/{vetId}/specialties/{specialtyId}")
    public ResponseEntity<Void> removeSpecialty(@PathVariable int vetId, @PathVariable int specialtyId) {
        vetService.removeSpecialty(vetId, specialtyId);
        return ResponseEntity.noContent().build();
    }
}
