package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetRequest;
import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.service.VetService;
import jakarta.validation.Valid;
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
            @RequestParam(required = false) Integer specialtyId) {
        if (lastName != null) {
            return ResponseEntity.ok(vetService.searchByLastName(lastName));
        }
        if (specialtyId != null) {
            return ResponseEntity.ok(vetService.findBySpecialtyId(specialtyId));
        }
        return ResponseEntity.ok(vetService.findAll());
    }

    @PostMapping
    public ResponseEntity<VetResponse> addVet(@Valid @RequestBody VetRequest request) {
        return ResponseEntity.ok(vetService.create(request));
    }

    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponse> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.findById(vetId));
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponse> updateVet(@PathVariable Integer vetId,
                                                  @Valid @RequestBody VetRequest request) {
        return ResponseEntity.ok(vetService.update(vetId, request));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetResponse> deleteVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.delete(vetId));
    }
}
