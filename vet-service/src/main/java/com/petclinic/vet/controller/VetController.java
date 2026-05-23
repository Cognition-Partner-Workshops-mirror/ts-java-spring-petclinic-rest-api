package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for Vet endpoints matching the OpenAPI spec.
 */
@RestController
@RequestMapping("/api/vets")
public class VetController {

    private final VetService vetService;
    private final VetMapper vetMapper;

    public VetController(VetService vetService, VetMapper vetMapper) {
        this.vetService = vetService;
        this.vetMapper = vetMapper;
    }

    /**
     * GET /api/vets - Returns all vets, 404 if the list is empty (per monolith pattern).
     */
    @GetMapping
    public ResponseEntity<List<VetResponseDto>> listVets() {
        List<Vet> vets = vetService.findAll();
        if (vets.isEmpty()) {
            throw new ResourceNotFoundException("No vets found");
        }
        return ResponseEntity.ok(vetMapper.toResponseDtos(vets));
    }

    /**
     * POST /api/vets - Creates a new vet, returns 200 with VetResponseDto.
     */
    @PostMapping
    public ResponseEntity<VetResponseDto> addVet(@Valid @RequestBody VetRequestDto vetRequestDto) {
        Vet saved = vetService.save(vetRequestDto);
        return ResponseEntity.ok(vetMapper.toResponseDto(saved));
    }

    /**
     * GET /api/vets/{vetId} - Returns a single vet by ID, 404 if not found.
     */
    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> getVet(@PathVariable Integer vetId) {
        Vet vet = vetService.findById(vetId);
        return ResponseEntity.ok(vetMapper.toResponseDto(vet));
    }

    /**
     * PUT /api/vets/{vetId} - Updates an existing vet, 404 if not found.
     */
    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> updateVet(@PathVariable Integer vetId,
                                                     @Valid @RequestBody VetRequestDto vetRequestDto) {
        Vet updated = vetService.update(vetId, vetRequestDto);
        return ResponseEntity.ok(vetMapper.toResponseDto(updated));
    }

    /**
     * DELETE /api/vets/{vetId} - Deletes a vet, returns the deleted vet, 404 if not found.
     */
    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> deleteVet(@PathVariable Integer vetId) {
        Vet vet = vetService.findById(vetId);
        VetResponseDto response = vetMapper.toResponseDto(vet);
        vetService.delete(vetId);
        return ResponseEntity.ok(response);
    }
}
