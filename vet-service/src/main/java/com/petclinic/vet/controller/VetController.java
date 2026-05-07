package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetRequest;
import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.service.VetService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vets")
public class VetController {

    private final VetService service;

    public VetController(VetService service) {
        this.service = service;
    }

    @GetMapping
    public List<VetResponse> listVets(
        @RequestParam(required = false) Integer specialtyId,
        @RequestParam(required = false) String name) {
        if (specialtyId != null) {
            return service.findBySpecialty(specialtyId);
        }
        if (name != null && !name.isBlank()) {
            return service.searchByName(name);
        }
        return service.findAll();
    }

    @GetMapping("/{vetId}")
    public VetResponse getVet(@PathVariable Integer vetId) {
        return service.findById(vetId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public VetResponse addVet(@Valid @RequestBody VetRequest request) {
        return service.create(request);
    }

    @PutMapping("/{vetId}")
    public VetResponse updateVet(@PathVariable Integer vetId,
                                 @Valid @RequestBody VetRequest request) {
        return service.update(vetId, request);
    }

    @DeleteMapping("/{vetId}")
    public VetResponse deleteVet(@PathVariable Integer vetId) {
        return service.delete(vetId);
    }
}
