package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.service.SpecialtyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ProblemDetail;
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
@RequestMapping("/api/specialties")
@Tag(name = "specialty", description = "Endpoints related to vet specialties")
public class SpecialtyController {

    private final SpecialtyService service;

    public SpecialtyController(SpecialtyService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lists specialties",
        responses = {
            @ApiResponse(responseCode = "200", description = "Specialties found and returned.",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = SpecialtyResponseDto.class)))),
            @ApiResponse(responseCode = "500", description = "Server error.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
    public ResponseEntity<List<SpecialtyResponseDto>> listSpecialties() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/{specialtyId}")
    @Operation(summary = "Get a specialty by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Specialty found and returned.",
                content = @Content(schema = @Schema(implementation = SpecialtyResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Specialty not found.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
    public ResponseEntity<SpecialtyResponseDto> getSpecialty(
            @Parameter(description = "The ID of the specialty.", example = "1") @PathVariable Integer specialtyId) {
        return ResponseEntity.ok(service.getById(specialtyId));
    }

    @PostMapping
    @Operation(summary = "Create a specialty",
        responses = {
            @ApiResponse(responseCode = "200", description = "Specialty created successfully.",
                content = @Content(schema = @Schema(implementation = SpecialtyResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
    public ResponseEntity<SpecialtyResponseDto> addSpecialty(@Valid @RequestBody SpecialtyRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{specialtyId}")
    @Operation(summary = "Update a specialty by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Specialty updated successfully.",
                content = @Content(schema = @Schema(implementation = SpecialtyResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Specialty not found.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
    public ResponseEntity<SpecialtyResponseDto> updateSpecialty(
            @Parameter(description = "The ID of the specialty.", example = "1") @PathVariable Integer specialtyId,
            @Valid @RequestBody SpecialtyRequestDto dto) {
        return ResponseEntity.ok(service.update(specialtyId, dto));
    }

    @DeleteMapping("/{specialtyId}")
    @Operation(summary = "Delete a specialty by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Specialty deleted successfully.",
                content = @Content(schema = @Schema(implementation = SpecialtyResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Specialty not found.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
    public ResponseEntity<SpecialtyResponseDto> deleteSpecialty(
            @Parameter(description = "The ID of the specialty.", example = "1") @PathVariable Integer specialtyId) {
        return ResponseEntity.ok(service.delete(specialtyId));
    }
}
