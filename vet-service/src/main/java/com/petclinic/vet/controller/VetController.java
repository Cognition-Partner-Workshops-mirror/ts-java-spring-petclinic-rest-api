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
@RequestMapping("/api/vets")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    @GetMapping
    public ResponseEntity<List<VetResponse>> listVets(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Integer specialtyId,
            @RequestParam(required = false) String specialtyName) {
        List<VetResponse> result;
        if (lastName != null) {
            result = vetService.searchByLastName(lastName);
        } else if (specialtyId != null) {
            result = vetService.findBySpecialty(specialtyId);
        } else if (specialtyName != null) {
            result = vetService.findBySpecialtyName(specialtyName);
        } else {
            result = vetService.listAll();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponse> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.getById(vetId));
    }

    @PostMapping
    public ResponseEntity<VetResponse> addVet(@Valid @RequestBody VetRequest request) {
        VetResponse created = vetService.create(request);
        return ResponseEntity.status(HttpStatus.OK).body(created);
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponse> updateVet(
            @PathVariable Integer vetId,
            @Valid @RequestBody VetRequest request) {
        return ResponseEntity.ok(vetService.update(vetId, request));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetResponse> deleteVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.delete(vetId));
    }
}
