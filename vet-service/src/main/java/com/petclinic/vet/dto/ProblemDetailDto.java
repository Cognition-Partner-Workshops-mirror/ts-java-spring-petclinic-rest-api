package com.petclinic.vet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemDetailDto(
    URI type,
    String title,
    int status,
    String detail,
    Instant timestamp,
    List<ValidationMessageDto> schemaValidationErrors
) {
}
