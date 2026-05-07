package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.service.VetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api/vets")
public class VetRestController {

    private final VetService vetService;

    public VetRestController(VetService vetService) {
        this.vetService = vetService;
    }

    @GetMapping
    public ResponseEntity<List<VetDto>> listVets() {
        List<VetDto> vets = vetService.findAll();
        return ResponseEntity.ok(vets);
    }

    @GetMapping("/{vetId}")
    public ResponseEntity<VetDto> getVet(@PathVariable int vetId) {
        return ResponseEntity.ok(vetService.findById(vetId));
    }

    @PostMapping
    public ResponseEntity<VetDto> addVet(@Valid @RequestBody VetRequestDto request, UriComponentsBuilder ucb) {
        VetDto created = vetService.create(request);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucb.path("/api/vets/{id}").buildAndExpand(created.id()).toUri());
        return new ResponseEntity<>(created, headers, HttpStatus.CREATED);
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetDto> updateVet(@PathVariable int vetId, @Valid @RequestBody VetRequestDto request) {
        return ResponseEntity.ok(vetService.update(vetId, request));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<Void> deleteVet(@PathVariable int vetId) {
        vetService.delete(vetId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<VetDto>> searchVets(
        @RequestParam(required = false) String lastName,
        @RequestParam(required = false) String specialty,
        @RequestParam(required = false) String name) {

        if (lastName != null) {
            return ResponseEntity.ok(vetService.findByLastName(lastName));
        }
        if (specialty != null) {
            return ResponseEntity.ok(vetService.findBySpecialty(specialty));
        }
        if (name != null) {
            return ResponseEntity.ok(vetService.search(name));
        }
        return ResponseEntity.ok(vetService.findAll());
    }
}
