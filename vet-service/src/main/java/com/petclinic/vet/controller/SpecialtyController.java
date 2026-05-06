package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.service.SpecialtyService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SpecialtyDto>> listSpecialties() {
        return ResponseEntity.ok(specialtyService.findAll());
    }

    @GetMapping(value = "/{specialtyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SpecialtyDto> getSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.findById(specialtyId));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SpecialtyDto> addSpecialty(@Valid @RequestBody SpecialtyDto dto) {
        SpecialtyDto created = specialtyService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping(value = "/{specialtyId}",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SpecialtyDto> updateSpecialty(
            @PathVariable Integer specialtyId,
            @Valid @RequestBody SpecialtyDto dto) {
        return ResponseEntity.ok(specialtyService.update(specialtyId, dto));
    }

    @DeleteMapping(value = "/{specialtyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteSpecialty(@PathVariable Integer specialtyId) {
        specialtyService.delete(specialtyId);
        return ResponseEntity.noContent().build();
    }
}
