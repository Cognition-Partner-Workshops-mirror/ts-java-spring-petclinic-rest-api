package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.service.VetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/vets")
public class VetRestController {

    private final VetService vetService;
    private final VetMapper vetMapper;

    public VetRestController(VetService vetService, VetMapper vetMapper) {
        this.vetService = vetService;
        this.vetMapper = vetMapper;
    }

    @GetMapping
    public ResponseEntity<List<VetDto>> listVets(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String specialty) {

        List<Vet> vets;
        if (name != null && !name.isBlank()) {
            vets = vetService.searchVetsByName(name);
        } else if (specialty != null && !specialty.isBlank()) {
            vets = vetService.findVetsBySpecialty(specialty);
        } else {
            vets = vetService.findAllVets();
        }
        return ResponseEntity.ok(vetMapper.toDtoList(vets));
    }

    @GetMapping("/{vetId}")
    public ResponseEntity<VetDto> getVet(@PathVariable Integer vetId) {
        Vet vet = vetService.findVetById(vetId);
        return ResponseEntity.ok(vetMapper.toDto(vet));
    }

    @PostMapping
    public ResponseEntity<VetDto> addVet(@Valid @RequestBody VetRequestDto request, UriComponentsBuilder ucb) {
        Vet vet = vetMapper.toEntity(request);
        Vet saved = vetService.saveVet(vet);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucb.path("/vets/{id}").buildAndExpand(saved.getId()).toUri());
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(vetMapper.toDto(saved));
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetDto> updateVet(@PathVariable Integer vetId, @Valid @RequestBody VetDto vetDto) {
        Vet updated = vetMapper.toEntity(vetDto);
        Vet saved = vetService.updateVet(vetId, updated);
        return ResponseEntity.ok(vetMapper.toDto(saved));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<Void> deleteVet(@PathVariable Integer vetId) {
        vetService.deleteVet(vetId);
        return ResponseEntity.noContent().build();
    }
}
