package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.service.SpecialtyService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyDto>> listSpecialties() {
        return ResponseEntity.ok(specialtyService.findAll());
    }

    @GetMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyDto> getSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.findById(specialtyId));
    }

    @PostMapping
    public ResponseEntity<SpecialtyDto> addSpecialty(@Valid @RequestBody SpecialtyRequestDto dto) {
        return ResponseEntity.ok(specialtyService.create(dto));
    }

    @PutMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyDto> updateSpecialty(@PathVariable Integer specialtyId,
                                                        @Valid @RequestBody SpecialtyRequestDto dto) {
        return ResponseEntity.ok(specialtyService.update(specialtyId, dto));
    }

    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyDto> deleteSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.delete(specialtyId));
    }
}
