package com.petclinic.vet.controller;

import com.petclinic.vet.dto.AppointmentRequestDto;
import com.petclinic.vet.dto.AppointmentResponseDto;
import com.petclinic.vet.entity.Appointment;
import com.petclinic.vet.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing pet appointments.
 * Provides endpoints for CRUD operations and filtering by vet, date, and status.
 */
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * List all appointments with optional filtering.
     * Supports filtering by vetId, date, or status query parameters.
     */
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDto>> listAppointments(
        @RequestParam(required = false) Integer vetId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam(required = false) String status) {

        List<AppointmentResponseDto> appointments;
        if (vetId != null) {
            // Filter by veterinarian
            appointments = appointmentService.findByVetId(vetId);
        } else if (date != null) {
            // Filter by appointment date
            appointments = appointmentService.findByDate(date);
        } else if (status != null && !status.isBlank()) {
            // Filter by appointment status (SCHEDULED, CONFIRMED, CANCELLED, COMPLETED)
            appointments = appointmentService.findByStatus(Appointment.Status.valueOf(status.toUpperCase()));
        } else {
            // Return all appointments
            appointments = appointmentService.findAll();
        }
        return ResponseEntity.ok(appointments);
    }

    /** Get a single appointment by its ID */
    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponseDto> getAppointment(@PathVariable Integer appointmentId) {
        return ResponseEntity.ok(appointmentService.findById(appointmentId));
    }

    /** Schedule a new appointment - returns 201 Created */
    @PostMapping
    public ResponseEntity<AppointmentResponseDto> createAppointment(
        @Valid @RequestBody AppointmentRequestDto dto) {
        AppointmentResponseDto created = appointmentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Update an existing appointment's details */
    @PutMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponseDto> updateAppointment(
        @PathVariable Integer appointmentId,
        @Valid @RequestBody AppointmentRequestDto dto) {
        return ResponseEntity.ok(appointmentService.update(appointmentId, dto));
    }

    /** Cancel an appointment (sets status to CANCELLED) */
    @PostMapping("/{appointmentId}/cancel")
    public ResponseEntity<AppointmentResponseDto> cancelAppointment(@PathVariable Integer appointmentId) {
        return ResponseEntity.ok(appointmentService.cancel(appointmentId));
    }

    /** Delete an appointment permanently */
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponseDto> deleteAppointment(@PathVariable Integer appointmentId) {
        return ResponseEntity.ok(appointmentService.delete(appointmentId));
    }
}
