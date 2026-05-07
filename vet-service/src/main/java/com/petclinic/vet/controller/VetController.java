package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
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

@RestController
@RequestMapping("/vets")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    @GetMapping
    public ResponseEntity<List<VetResponseDto>> listVets(
            @RequestParam(required = false) String specialtyName,
            @RequestParam(required = false) Integer specialtyId,
            @RequestParam(required = false) String name) {
        List<VetResponseDto> vets;
        if (specialtyId != null) {
            vets = vetService.findBySpecialtyId(specialtyId);
        } else if (specialtyName != null && !specialtyName.isBlank()) {
            vets = vetService.findBySpecialtyName(specialtyName);
        } else if (name != null && !name.isBlank()) {
            vets = vetService.searchByName(name);
        } else {
            vets = vetService.getAllVets();
        }
        return ResponseEntity.ok(vets);
    }

    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.getVetById(vetId));
    }

    @PostMapping
    public ResponseEntity<VetResponseDto> addVet(@Valid @RequestBody VetRequestDto requestDto) {
        return ResponseEntity.ok(vetService.createVet(requestDto));
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> updateVet(
            @PathVariable Integer vetId,
            @Valid @RequestBody VetRequestDto requestDto) {
        return ResponseEntity.ok(vetService.updateVet(vetId, requestDto));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> deleteVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.deleteVet(vetId));
    }
}
