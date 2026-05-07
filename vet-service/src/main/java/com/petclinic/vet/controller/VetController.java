package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
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
@RequestMapping("/vets")
public class VetController {

    private final VetService service;

    public VetController(VetService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<VetDto>> listVets(
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String lastName) {
        List<VetDto> result;
        if (specialty != null && lastName != null) {
            result = service.findBySpecialtyAndLastName(specialty, lastName);
        } else if (specialty != null) {
            result = service.findBySpecialty(specialty);
        } else if (lastName != null) {
            result = service.findByLastName(lastName);
        } else {
            result = service.listAll();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{vetId}")
    public ResponseEntity<VetDto> getVet(@PathVariable int vetId) {
        return ResponseEntity.ok(service.getById(vetId));
    }

    @PostMapping
    public ResponseEntity<VetDto> addVet(@Valid @RequestBody VetRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetDto> updateVet(
            @PathVariable int vetId,
            @Valid @RequestBody VetRequestDto dto) {
        return ResponseEntity.ok(service.update(vetId, dto));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetDto> deleteVet(@PathVariable int vetId) {
        return ResponseEntity.ok(service.delete(vetId));
    }
}
