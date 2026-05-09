package com.petclinic.vet.service;

import com.petclinic.vet.dto.AppointmentRequestDto;
import com.petclinic.vet.dto.AppointmentResponseDto;
import com.petclinic.vet.entity.Appointment;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.AppointmentMapper;
import com.petclinic.vet.repository.AppointmentRepository;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for appointment scheduling business logic.
 * Handles CRUD operations, status transitions, and filtering.
 */
@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              VetRepository vetRepository,
                              SpecialtyRepository specialtyRepository,
                              AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
        this.appointmentMapper = appointmentMapper;
    }

    /** Retrieve all appointments, ordered by the repository default */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findAll() {
        return appointmentRepository.findAll().stream()
            .map(appointmentMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    /** Retrieve a single appointment by ID */
    @Transactional(readOnly = true)
    public AppointmentResponseDto findById(Integer id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
        return appointmentMapper.toResponseDto(appointment);
    }

    /**
     * Create a new appointment.
     * Validates that the referenced vet exists and optionally the specialty.
     * Default status is SCHEDULED.
     */
    public AppointmentResponseDto create(AppointmentRequestDto dto) {
        // Resolve the vet - must exist
        Vet vet = vetRepository.findById(dto.getVetId())
            .orElseThrow(() -> new ResourceNotFoundException("Vet", dto.getVetId()));

        // Resolve specialty if provided (optional field)
        Specialty specialty = null;
        if (dto.getSpecialtyId() != null) {
            specialty = specialtyRepository.findById(dto.getSpecialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", dto.getSpecialtyId()));
        }

        // Build and persist the appointment entity
        Appointment appointment = new Appointment();
        appointment.setPetName(dto.getPetName());
        appointment.setPetOwnerName(dto.getPetOwnerName());
        appointment.setVet(vet);
        appointment.setSpecialty(specialty);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setAppointmentTime(dto.getAppointmentTime());
        appointment.setReason(dto.getReason());
        appointment.setStatus(Appointment.Status.SCHEDULED);

        Appointment saved = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDto(saved);
    }

    /**
     * Update an existing appointment's details.
     * Re-validates vet and specialty references on every update.
     */
    public AppointmentResponseDto update(Integer id, AppointmentRequestDto dto) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        Vet vet = vetRepository.findById(dto.getVetId())
            .orElseThrow(() -> new ResourceNotFoundException("Vet", dto.getVetId()));

        Specialty specialty = null;
        if (dto.getSpecialtyId() != null) {
            specialty = specialtyRepository.findById(dto.getSpecialtyId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", dto.getSpecialtyId()));
        }

        appointment.setPetName(dto.getPetName());
        appointment.setPetOwnerName(dto.getPetOwnerName());
        appointment.setVet(vet);
        appointment.setSpecialty(specialty);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setAppointmentTime(dto.getAppointmentTime());
        appointment.setReason(dto.getReason());

        Appointment updated = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDto(updated);
    }

    /** Cancel an appointment by setting its status to CANCELLED */
    public AppointmentResponseDto cancel(Integer id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
        appointment.setStatus(Appointment.Status.CANCELLED);
        Appointment updated = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDto(updated);
    }

    /** Delete an appointment permanently */
    public AppointmentResponseDto delete(Integer id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
        AppointmentResponseDto response = appointmentMapper.toResponseDto(appointment);
        appointmentRepository.delete(appointment);
        return response;
    }

    /** Find all appointments for a given vet */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findByVetId(Integer vetId) {
        return appointmentRepository.findByVetId(vetId).stream()
            .map(appointmentMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    /** Find appointments scheduled on a specific date */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date).stream()
            .map(appointmentMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    /** Find appointments by their current status */
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> findByStatus(Appointment.Status status) {
        return appointmentRepository.findByStatus(status).stream()
            .map(appointmentMapper::toResponseDto)
            .collect(Collectors.toList());
    }
}
