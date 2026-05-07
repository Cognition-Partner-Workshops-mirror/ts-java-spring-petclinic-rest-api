package com.petclinic.vet.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class ValidationMessageDto {

    private String message;
    private Map<String, Object> additionalProperties = new LinkedHashMap<>();

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

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public ValidationMessageDto putAdditionalProperty(String key, Object value) {
        this.additionalProperties.put(key, value);
        return this;
    }
}
