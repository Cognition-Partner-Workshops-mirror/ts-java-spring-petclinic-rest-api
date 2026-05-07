package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.SpecialtyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SpecialtyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SpecialtyService specialtyService;

    @InjectMocks
    private SpecialtyController specialtyController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(specialtyController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    void listSpecialties_returnsOk() throws Exception {
        when(specialtyService.listSpecialties())
            .thenReturn(List.of(new SpecialtyResponseDto(1, "radiology")));

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("radiology"));
    }

    @Test
    void getSpecialty_returnsOk() throws Exception {
        when(specialtyService.getSpecialty(1))
            .thenReturn(new SpecialtyResponseDto(1, "radiology"));

        mockMvc.perform(get("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void getSpecialty_returnsNotFound() throws Exception {
        when(specialtyService.getSpecialty(999))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(get("/api/specialties/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void addSpecialty_returnsOk() throws Exception {
        when(specialtyService.addSpecialty(any(SpecialtyRequestDto.class)))
            .thenReturn(new SpecialtyResponseDto(1, "radiology"));

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"radiology\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void addSpecialty_returnsBadRequestForEmptyName() throws Exception {
        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addSpecialty_returnsBadRequestForMissingName() throws Exception {
        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialty_returnsOk() throws Exception {
        when(specialtyService.updateSpecialty(eq(1), any(SpecialtyRequestDto.class)))
            .thenReturn(new SpecialtyResponseDto(1, "surgery"));

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"surgery\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("surgery"));
    }

    @Test
    void updateSpecialty_returnsNotFound() throws Exception {
        when(specialtyService.updateSpecialty(eq(999), any(SpecialtyRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(put("/api/specialties/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"surgery\"}"))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_returnsOk() throws Exception {
        when(specialtyService.deleteSpecialty(1))
            .thenReturn(new SpecialtyResponseDto(1, "radiology"));

        mockMvc.perform(delete("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteSpecialty_returnsNotFound() throws Exception {
        when(specialtyService.deleteSpecialty(999))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(delete("/api/specialties/999"))
            .andExpect(status().isNotFound());
    }
}
