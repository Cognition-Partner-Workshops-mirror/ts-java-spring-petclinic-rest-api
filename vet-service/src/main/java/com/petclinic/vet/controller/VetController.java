package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetRequest;
import com.petclinic.vet.dto.VetResponse;
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

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    @GetMapping
    public ResponseEntity<List<VetResponse>> listVets(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String specialty) {
        if (lastName != null && !lastName.isBlank()) {
            return ResponseEntity.ok(vetService.findByLastName(lastName));
        }
        if (specialty != null && !specialty.isBlank()) {
            return ResponseEntity.ok(vetService.findBySpecialty(specialty));
        }
        return ResponseEntity.ok(vetService.listAll());
    }

    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponse> getVet(@PathVariable int vetId) {
        return ResponseEntity.ok(vetService.getById(vetId));
    }

    @PostMapping
    public ResponseEntity<VetResponse> addVet(@Valid @RequestBody VetRequest request) {
        return ResponseEntity.ok(vetService.create(request));
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponse> updateVet(@PathVariable int vetId,
                                                  @Valid @RequestBody VetRequest request) {
        return ResponseEntity.ok(vetService.update(vetId, request));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetResponse> deleteVet(@PathVariable int vetId) {
        return ResponseEntity.ok(vetService.delete(vetId));
    }
}
