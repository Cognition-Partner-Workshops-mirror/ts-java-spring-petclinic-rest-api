package com.petclinic.vet.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for creating or updating an appointment.
 * Contains validation constraints to enforce required fields and data formats.
 */
public class AppointmentRequestDto {

    /** Name of the pet being seen - required, max 100 chars */
    @NotBlank(message = "Pet name is required")
    @Size(max = 100, message = "Pet name must not exceed 100 characters")
    private String petName;

    /** Name of the pet's owner - required, max 100 chars */
    @NotBlank(message = "Pet owner name is required")
    @Size(max = 100, message = "Pet owner name must not exceed 100 characters")
    private String petOwnerName;

    /** ID of the veterinarian for this appointment - required */
    @NotNull(message = "Vet ID is required")
    private Integer vetId;

    /** ID of the specialty (optional, nullable) */
    private Integer specialtyId;

    /** Date of the appointment - required, must be today or future */
    @NotNull(message = "Appointment date is required")
    @FutureOrPresent(message = "Appointment date must be today or in the future")
    private LocalDate appointmentDate;

    /** Time of the appointment - required */
    @NotNull(message = "Appointment time is required")
    private LocalTime appointmentTime;

    /** Reason for the visit - required, max 500 chars */
    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;

    public AppointmentRequestDto() {
    }

    // --- Getters and Setters ---

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetOwnerName() {
        return petOwnerName;
    }

    public void setPetOwnerName(String petOwnerName) {
        this.petOwnerName = petOwnerName;
    }

    public Integer getVetId() {
        return vetId;
    }

    public void setVetId(Integer vetId) {
        this.vetId = vetId;
    }

    public Integer getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(Integer specialtyId) {
        this.specialtyId = specialtyId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
