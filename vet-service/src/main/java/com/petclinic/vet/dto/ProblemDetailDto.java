package com.petclinic.vet.dto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProblemDetailDto {

    private String type;
    private String title;
    private int status;
    private String detail;
    private OffsetDateTime timestamp;
    private List<ValidationMessageDto> schemaValidationErrors = new ArrayList<>();

    public ProblemDetailDto() {
    }

    public ProblemDetailDto(String type, String title, int status, String detail,
                            OffsetDateTime timestamp, List<ValidationMessageDto> schemaValidationErrors) {
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.timestamp = timestamp;
        this.schemaValidationErrors = schemaValidationErrors != null ? schemaValidationErrors : new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<ValidationMessageDto> getSchemaValidationErrors() {
        return schemaValidationErrors;
    }

    public void setSchemaValidationErrors(List<ValidationMessageDto> schemaValidationErrors) {
        this.schemaValidationErrors = schemaValidationErrors;
    }
}
