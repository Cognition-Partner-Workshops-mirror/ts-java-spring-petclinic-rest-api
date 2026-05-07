package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.service.VetService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vets")
@Tag(name = "vet", description = "Endpoints related to vets")
public class VetController {

    private final VetService service;

    public VetController(VetService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lists vets",
        description = "Returns an array of vets. Optionally filter by name or specialty.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Vets found and returned.",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = VetResponseDto.class)))),
            @ApiResponse(responseCode = "500", description = "Server error.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
    public ResponseEntity<List<VetResponseDto>> listVets(
            @Parameter(description = "Filter vets by first or last name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter vets by specialty name") @RequestParam(required = false) String specialty) {
        if (specialty != null && !specialty.isBlank()) {
            return ResponseEntity.ok(service.filterBySpecialty(specialty));
        }
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(service.searchByName(name));
        }
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/{vetId}")
    @Operation(summary = "Get a vet by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Vet found and returned.",
                content = @Content(schema = @Schema(implementation = VetResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Vet not found.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
    public ResponseEntity<VetResponseDto> getVet(
            @Parameter(description = "The ID of the vet.", example = "1") @PathVariable Integer vetId) {
        return ResponseEntity.ok(service.getById(vetId));
    }

    @PostMapping
    @Operation(summary = "Create a vet",
        responses = {
            @ApiResponse(responseCode = "200", description = "Vet created successfully.",
                content = @Content(schema = @Schema(implementation = VetResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
    public ResponseEntity<VetResponseDto> addVet(@Valid @RequestBody VetRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{vetId}")
    @Operation(summary = "Update a vet by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Vet updated successfully.",
                content = @Content(schema = @Schema(implementation = VetResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Vet not found.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
    public ResponseEntity<VetResponseDto> updateVet(
            @Parameter(description = "The ID of the vet.", example = "1") @PathVariable Integer vetId,
            @Valid @RequestBody VetRequestDto dto) {
        return ResponseEntity.ok(service.update(vetId, dto));
    }

    @DeleteMapping("/{vetId}")
    @Operation(summary = "Delete a vet by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Vet deleted successfully.",
                content = @Content(schema = @Schema(implementation = VetResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Vet not found.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
    public ResponseEntity<VetResponseDto> deleteVet(
            @Parameter(description = "The ID of the vet.", example = "1") @PathVariable Integer vetId) {
        return ResponseEntity.ok(service.delete(vetId));
    }
}
