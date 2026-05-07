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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/vets")
public class VetRestController {

    private final VetService vetService;

    public VetRestController(VetService vetService) {
        this.vetService = vetService;
    }

    @GetMapping
    public ResponseEntity<List<VetResponseDto>> listVets(
        @RequestParam(required = false) String specialty,
        @RequestParam(required = false) String lastName) {

        List<VetResponseDto> vets;
        if (specialty != null && !specialty.isBlank()) {
            vets = vetService.findBySpecialty(specialty);
        } else if (lastName != null && !lastName.isBlank()) {
            vets = vetService.searchByLastName(lastName);
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
    public ResponseEntity<VetResponseDto> addVet(@Valid @RequestBody VetRequestDto vetDto, UriComponentsBuilder ucb) {
        VetResponseDto created = vetService.create(vetDto);
        return ResponseEntity
            .created(ucb.path("/api/vets/{id}").buildAndExpand(created.getId()).toUri())
            .body(created);
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> updateVet(@PathVariable Integer vetId,
                                                     @Valid @RequestBody VetRequestDto vetDto) {
        return ResponseEntity.ok(vetService.update(vetId, vetDto));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<Void> deleteVet(@PathVariable Integer vetId) {
        vetService.delete(vetId);
        return ResponseEntity.noContent().build();
    }
}
