package com.petclinic.vet.controller;

import com.petclinic.vet.dto.request.VetRequestDto;
import com.petclinic.vet.dto.response.VetResponseDto;
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
@RequestMapping("/vets")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    @GetMapping
    public ResponseEntity<List<VetResponseDto>> listVets(
        @RequestParam(required = false) String specialty,
        @RequestParam(required = false) String name) {

        List<VetResponseDto> vets;
        if (specialty != null && !specialty.isBlank()) {
            vets = vetService.findBySpecialty(specialty);
        } else if (name != null && !name.isBlank()) {
            vets = vetService.searchByName(name);
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
    public ResponseEntity<VetResponseDto> addVet(@Valid @RequestBody VetRequestDto dto, UriComponentsBuilder ucb) {
        VetResponseDto created = vetService.create(dto);
        return ResponseEntity
            .created(ucb.path("/vets/{id}").buildAndExpand(created.id()).toUri())
            .body(created);
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> updateVet(@PathVariable Integer vetId, @Valid @RequestBody VetRequestDto dto) {
        return ResponseEntity.ok(vetService.update(vetId, dto));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> deleteVet(@PathVariable Integer vetId) {
        VetResponseDto deleted = vetService.delete(vetId);
        return ResponseEntity.ok(deleted);
    }
}
