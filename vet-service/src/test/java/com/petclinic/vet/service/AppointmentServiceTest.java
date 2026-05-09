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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AppointmentService.
 * Uses Mockito to isolate service logic from repository and mapper dependencies.
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private VetRepository vetRepository;
    @Mock
    private SpecialtyRepository specialtyRepository;
    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentService appointmentService;

    private Vet vet;
    private Specialty specialty;
    private Appointment appointment;
    private AppointmentResponseDto responseDto;
    private AppointmentRequestDto requestDto;

    @BeforeEach
    void setUp() {
        // Set up shared test fixtures
        vet = new Vet(1, "James", "Carter");
        specialty = new Specialty(1, "Surgery");

        appointment = new Appointment();
        appointment.setId(1);
        appointment.setPetName("Buddy");
        appointment.setPetOwnerName("John Smith");
        appointment.setVet(vet);
        appointment.setSpecialty(specialty);
        appointment.setAppointmentDate(LocalDate.of(2026, 6, 15));
        appointment.setAppointmentTime(LocalTime.of(10, 0));
        appointment.setReason("Annual checkup");
        appointment.setStatus(Appointment.Status.SCHEDULED);

        responseDto = new AppointmentResponseDto();
        responseDto.setId(1);
        responseDto.setPetName("Buddy");
        responseDto.setPetOwnerName("John Smith");
        responseDto.setVetId(1);
        responseDto.setVetName("James Carter");
        responseDto.setSpecialtyId(1);
        responseDto.setSpecialtyName("Surgery");
        responseDto.setAppointmentDate(LocalDate.of(2026, 6, 15));
        responseDto.setAppointmentTime(LocalTime.of(10, 0));
        responseDto.setReason("Annual checkup");
        responseDto.setStatus("SCHEDULED");

        requestDto = new AppointmentRequestDto();
        requestDto.setPetName("Buddy");
        requestDto.setPetOwnerName("John Smith");
        requestDto.setVetId(1);
        requestDto.setSpecialtyId(1);
        requestDto.setAppointmentDate(LocalDate.of(2026, 6, 15));
        requestDto.setAppointmentTime(LocalTime.of(10, 0));
        requestDto.setReason("Annual checkup");
    }

    @Test
    void findAll_returnsAllAppointments() {
        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));
        when(appointmentMapper.toResponseDto(appointment)).thenReturn(responseDto);

        List<AppointmentResponseDto> result = appointmentService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPetName()).isEqualTo("Buddy");
    }

    @Test
    void findById_existingId_returnsAppointment() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toResponseDto(appointment)).thenReturn(responseDto);

        AppointmentResponseDto result = appointmentService.findById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getVetName()).isEqualTo("James Carter");
    }

    @Test
    void findById_nonExistingId_throwsResourceNotFoundException() {
        when(appointmentRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Appointment");
    }

    @Test
    void create_validRequest_createsAppointment() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponseDto(appointment)).thenReturn(responseDto);

        AppointmentResponseDto result = appointmentService.create(requestDto);

        assertThat(result.getPetName()).isEqualTo("Buddy");
        assertThat(result.getStatus()).isEqualTo("SCHEDULED");
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void create_invalidVetId_throwsResourceNotFoundException() {
        when(vetRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.create(requestDto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet");
    }

    @Test
    void create_invalidSpecialtyId_throwsResourceNotFoundException() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.create(requestDto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void create_withNullSpecialtyId_createsAppointmentWithoutSpecialty() {
        // Specialty is optional - test with null specialtyId
        requestDto.setSpecialtyId(null);
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponseDto(appointment)).thenReturn(responseDto);

        AppointmentResponseDto result = appointmentService.create(requestDto);

        assertThat(result).isNotNull();
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void update_validRequest_updatesAppointment() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponseDto(appointment)).thenReturn(responseDto);

        AppointmentResponseDto result = appointmentService.update(1, requestDto);

        assertThat(result.getPetName()).isEqualTo("Buddy");
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void update_nonExistingId_throwsResourceNotFoundException() {
        when(appointmentRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.update(99, requestDto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Appointment");
    }

    @Test
    void cancel_existingAppointment_setsStatusToCancelled() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponseDto(appointment)).thenReturn(responseDto);

        appointmentService.cancel(1);

        // Verify status was set to CANCELLED before save
        assertThat(appointment.getStatus()).isEqualTo(Appointment.Status.CANCELLED);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void cancel_nonExistingId_throwsResourceNotFoundException() {
        when(appointmentRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.cancel(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingAppointment_deletesAndReturnsResponse() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toResponseDto(appointment)).thenReturn(responseDto);

        AppointmentResponseDto result = appointmentService.delete(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(appointmentRepository).delete(appointment);
    }

    @Test
    void delete_nonExistingId_throwsResourceNotFoundException() {
        when(appointmentRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByVetId_returnsMatchingAppointments() {
        when(appointmentRepository.findByVetId(1)).thenReturn(List.of(appointment));
        when(appointmentMapper.toResponseDto(appointment)).thenReturn(responseDto);

        List<AppointmentResponseDto> result = appointmentService.findByVetId(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVetId()).isEqualTo(1);
    }

    @Test
    void findByDate_returnsMatchingAppointments() {
        LocalDate date = LocalDate.of(2026, 6, 15);
        when(appointmentRepository.findByAppointmentDate(date)).thenReturn(List.of(appointment));
        when(appointmentMapper.toResponseDto(appointment)).thenReturn(responseDto);

        List<AppointmentResponseDto> result = appointmentService.findByDate(date);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByStatus_returnsMatchingAppointments() {
        when(appointmentRepository.findByStatus(Appointment.Status.SCHEDULED)).thenReturn(List.of(appointment));
        when(appointmentMapper.toResponseDto(appointment)).thenReturn(responseDto);

        List<AppointmentResponseDto> result = appointmentService.findByStatus(Appointment.Status.SCHEDULED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("SCHEDULED");
    }
}
