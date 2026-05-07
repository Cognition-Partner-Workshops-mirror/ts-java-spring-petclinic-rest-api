package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
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
    public ResponseEntity<List<VetResponseDto>> listVets(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String specialtyName,
            @RequestParam(required = false) Integer specialtyId) {
        List<VetResponseDto> result;
        if (lastName != null) {
            result = vetService.findByLastName(lastName);
        } else if (specialtyName != null) {
            result = vetService.findBySpecialtyName(specialtyName);
        } else if (specialtyId != null) {
            result = vetService.findBySpecialtyId(specialtyId);
        } else {
            result = vetService.listAll();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.getById(vetId));
    }

    @PostMapping
    public ResponseEntity<VetResponseDto> addVet(@Valid @RequestBody VetRequestDto request) {
        return ResponseEntity.ok(vetService.create(request));
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> updateVet(@PathVariable Integer vetId,
                                                     @Valid @RequestBody VetRequestDto request) {
        return ResponseEntity.ok(vetService.update(vetId, request));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<VetResponseDto> deleteVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.delete(vetId));
    }
}
