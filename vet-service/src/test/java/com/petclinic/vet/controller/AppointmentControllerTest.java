package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.petclinic.vet.dto.AppointmentRequestDto;
import com.petclinic.vet.dto.AppointmentResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * WebMvcTest for AppointmentController.
 * Tests REST endpoint mappings, request validation, and response formats.
 */
@WebMvcTest(controllers = {AppointmentController.class, GlobalExceptionHandler.class})
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    private ObjectMapper objectMapper;
    private AppointmentResponseDto responseDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Shared response fixture for mock service calls
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
    }

    @Test
    void listAppointments_returnsOkWithList() throws Exception {
        when(appointmentService.findAll()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/appointments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].petName").value("Buddy"))
            .andExpect(jsonPath("$[0].vetName").value("James Carter"));
    }

    @Test
    void listAppointments_filterByVetId_returnsFiltered() throws Exception {
        when(appointmentService.findByVetId(1)).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/appointments").param("vetId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].vetId").value(1));
    }

    @Test
    void listAppointments_filterByDate_returnsFiltered() throws Exception {
        when(appointmentService.findByDate(LocalDate.of(2026, 6, 15))).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/appointments").param("date", "2026-06-15"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].appointmentDate").value("2026-06-15"));
    }

    @Test
    void listAppointments_filterByStatus_returnsFiltered() throws Exception {
        when(appointmentService.findByStatus(any())).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/appointments").param("status", "SCHEDULED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].status").value("SCHEDULED"));
    }

    @Test
    void getAppointment_existingId_returnsOk() throws Exception {
        when(appointmentService.findById(1)).thenReturn(responseDto);

        mockMvc.perform(get("/appointments/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.petName").value("Buddy"));
    }

    @Test
    void getAppointment_nonExistingId_returnsNotFound() throws Exception {
        when(appointmentService.findById(99)).thenThrow(new ResourceNotFoundException("Appointment", 99));

        mockMvc.perform(get("/appointments/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void createAppointment_validRequest_returnsCreated() throws Exception {
        when(appointmentService.create(any(AppointmentRequestDto.class))).thenReturn(responseDto);

        AppointmentRequestDto request = new AppointmentRequestDto();
        request.setPetName("Buddy");
        request.setPetOwnerName("John Smith");
        request.setVetId(1);
        request.setSpecialtyId(1);
        request.setAppointmentDate(LocalDate.of(2026, 6, 15));
        request.setAppointmentTime(LocalTime.of(10, 0));
        request.setReason("Annual checkup");

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.petName").value("Buddy"));
    }

    @Test
    void createAppointment_missingPetName_returnsBadRequest() throws Exception {
        // Missing required petName field - should fail validation
        AppointmentRequestDto request = new AppointmentRequestDto();
        request.setPetOwnerName("John Smith");
        request.setVetId(1);
        request.setAppointmentDate(LocalDate.of(2026, 6, 15));
        request.setAppointmentTime(LocalTime.of(10, 0));
        request.setReason("Checkup");

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createAppointment_missingVetId_returnsBadRequest() throws Exception {
        // Missing required vetId field - should fail validation
        AppointmentRequestDto request = new AppointmentRequestDto();
        request.setPetName("Buddy");
        request.setPetOwnerName("John Smith");
        request.setAppointmentDate(LocalDate.of(2026, 6, 15));
        request.setAppointmentTime(LocalTime.of(10, 0));
        request.setReason("Checkup");

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateAppointment_validRequest_returnsOk() throws Exception {
        when(appointmentService.update(eq(1), any(AppointmentRequestDto.class))).thenReturn(responseDto);

        AppointmentRequestDto request = new AppointmentRequestDto();
        request.setPetName("Buddy");
        request.setPetOwnerName("John Smith");
        request.setVetId(1);
        request.setAppointmentDate(LocalDate.of(2026, 6, 15));
        request.setAppointmentTime(LocalTime.of(10, 0));
        request.setReason("Annual checkup");

        mockMvc.perform(put("/appointments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void cancelAppointment_existingId_returnsOk() throws Exception {
        responseDto.setStatus("CANCELLED");
        when(appointmentService.cancel(1)).thenReturn(responseDto);

        mockMvc.perform(post("/appointments/1/cancel"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void deleteAppointment_existingId_returnsOk() throws Exception {
        when(appointmentService.delete(1)).thenReturn(responseDto);

        mockMvc.perform(delete("/appointments/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }
}
