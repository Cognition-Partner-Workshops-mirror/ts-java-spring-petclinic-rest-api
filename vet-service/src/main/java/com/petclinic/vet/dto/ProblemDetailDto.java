package com.petclinic.vet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * DTO for RFC 7807 Problem Details error responses.
 * Matches the ProblemDetail schema from the PetClinic OpenAPI spec.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemDetailDto(
    String type,
    String title,
    int status,
    String detail,
    Instant timestamp,
    List<ValidationMessageDto> schemaValidationErrors
) {}
