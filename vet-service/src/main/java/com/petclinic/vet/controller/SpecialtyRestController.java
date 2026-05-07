package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.service.SpecialtyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import java.net.URI;
import java.util.List;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("/api/specialties")
public class SpecialtyRestController {

    private final SpecialtyService specialtyService;

    public SpecialtyRestController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyResponseDto>> listSpecialties(
        @RequestParam(required = false) String name) {

        List<SpecialtyResponseDto> specialties;
        if (name != null && !name.isBlank()) {
            specialties = specialtyService.searchByName(name);
        } else {
            specialties = specialtyService.findAll();
        }
        return ResponseEntity.ok(specialties);
    }

    @GetMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> getSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.findById(specialtyId));
    }

    @PostMapping
    public ResponseEntity<SpecialtyResponseDto> addSpecialty(@Valid @RequestBody SpecialtyRequestDto dto, UriComponentsBuilder ucb) {
        SpecialtyResponseDto created = specialtyService.create(dto);
        URI location = ucb.path("/api/specialties/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> updateSpecialty(@PathVariable Integer specialtyId, @Valid @RequestBody SpecialtyRequestDto dto) {
        return ResponseEntity.ok(specialtyService.update(specialtyId, dto));
    }

    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<Void> deleteSpecialty(@PathVariable Integer specialtyId) {
        specialtyService.delete(specialtyId);
        return ResponseEntity.noContent().build();
    }
}
