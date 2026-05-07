package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.service.SpecialtyService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {

    private final SpecialtyService service;

    public SpecialtyController(SpecialtyService service) {
        this.service = service;
    }

    @GetMapping
    public List<SpecialtyResponse> listSpecialties() {
        return service.findAll();
    }

    @GetMapping("/{specialtyId}")
    public SpecialtyResponse getSpecialty(@PathVariable Integer specialtyId) {
        return service.findById(specialtyId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public SpecialtyResponse addSpecialty(@Valid @RequestBody SpecialtyRequest request) {
        return service.create(request);
    }

    @PutMapping("/{specialtyId}")
    public SpecialtyResponse updateSpecialty(@PathVariable Integer specialtyId,
                                             @Valid @RequestBody SpecialtyRequest request) {
        return service.update(specialtyId, request);
    }

    @DeleteMapping("/{specialtyId}")
    public SpecialtyResponse deleteSpecialty(@PathVariable Integer specialtyId) {
        return service.delete(specialtyId);
    }
}
