package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetRequest;
import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.service.VetService;
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
@RequestMapping("/vets")
public class VetController {

    private final VetService service;

    public VetController(VetService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<VetResponse>> listVets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer specialtyId) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(service.searchByName(name));
        }
        if (specialtyId != null) {
            return ResponseEntity.ok(service.findBySpecialtyId(specialtyId));
        }
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponse> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(service.findById(vetId));
    }

    @PostMapping
    public ResponseEntity<VetResponse> addVet(@Valid @RequestBody VetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponse> updateVet(
            @PathVariable Integer vetId,
            @Valid @RequestBody VetRequest request) {
        return ResponseEntity.ok(service.update(vetId, request));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetResponse> deleteVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(service.delete(vetId));
    }
}
