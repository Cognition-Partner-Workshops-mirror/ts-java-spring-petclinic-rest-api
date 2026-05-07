package com.petclinic.vet.controller;

import com.petclinic.vet.dto.request.VetRequest;
import com.petclinic.vet.dto.response.VetResponse;
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

import java.util.List;

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
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer specialtyId) {
        List<VetResponse> vets;
        if (specialtyId != null) {
            vets = vetService.findBySpecialty(specialtyId);
        } else if (lastName != null) {
            vets = vetService.searchByLastName(lastName);
        } else if (name != null) {
            vets = vetService.searchByName(name);
        } else {
            vets = vetService.listVets();
        }
        return ResponseEntity.ok(vets);
    }

    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponse> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.getVet(vetId));
    }

    @PostMapping
    public ResponseEntity<VetResponse> addVet(@Valid @RequestBody VetRequest request) {
        VetResponse created = vetService.createVet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponse> updateVet(
            @PathVariable Integer vetId,
            @Valid @RequestBody VetRequest request) {
        return ResponseEntity.ok(vetService.updateVet(vetId, request));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetResponse> deleteVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.deleteVet(vetId));
    }
}
