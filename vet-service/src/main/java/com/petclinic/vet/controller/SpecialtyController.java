package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.service.SpecialtyService;
import jakarta.validation.Valid;
import java.util.List;
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

@RestController
@RequestMapping("/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyResponseDto>> listSpecialties(
            @RequestParam(required = false) String name) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(specialtyService.searchByName(name));
        }
        return ResponseEntity.ok(specialtyService.listAll());
    }

    @GetMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> getSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.getById(specialtyId));
    }

    @PostMapping
    public ResponseEntity<SpecialtyResponseDto> addSpecialty(
            @Valid @RequestBody SpecialtyRequestDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(specialtyService.create(dto));
    }

    @PutMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> updateSpecialty(
            @PathVariable Integer specialtyId,
            @Valid @RequestBody SpecialtyRequestDto dto) {
        return ResponseEntity.ok(specialtyService.update(specialtyId, dto));
    }

    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyResponseDto> deleteSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.delete(specialtyId));
    }
}
