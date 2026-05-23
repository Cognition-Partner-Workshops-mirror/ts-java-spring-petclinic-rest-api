package com.petclinic.vet.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

/**
 * DTO for field-level validation error messages, matching the ValidationMessage schema.
 * Supports additional properties via a dynamic map (per OpenAPI additionalProperties: true).
 */
public class ValidationMessageDto {

    private String message;
    private Map<String, Object> additionalProperties = new HashMap<>();

    public ValidationMessageDto() {
    }

    public ValidationMessageDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void putAdditionalProperty(String key, Object value) {
        this.additionalProperties.put(key, value);
    }
}
