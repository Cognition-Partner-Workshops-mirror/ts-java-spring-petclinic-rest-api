package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.service.VetService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/vets")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VetDto>> listVets() {
        return ResponseEntity.ok(vetService.findAll());
    }

    @GetMapping(value = "/{vetId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VetDto> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.findById(vetId));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VetDto> addVet(@Valid @RequestBody VetDto dto) {
        VetDto created = vetService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping(value = "/{vetId}",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VetDto> updateVet(
            @PathVariable Integer vetId,
            @Valid @RequestBody VetDto dto) {
        return ResponseEntity.ok(vetService.update(vetId, dto));
    }

    @DeleteMapping(value = "/{vetId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteVet(@PathVariable Integer vetId) {
        vetService.delete(vetId);
        return ResponseEntity.noContent().build();
    }
}
