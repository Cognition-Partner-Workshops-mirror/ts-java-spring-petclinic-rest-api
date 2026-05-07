package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SpecialtyControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private SpecialtyService specialtyService;

    @Mock
    private SpecialtyMapper specialtyMapper;

    @InjectMocks
    private SpecialtyController specialtyController;

    private Specialty radiology;
    private Specialty surgery;
    private SpecialtyResponseDto radiologyDto;
    private SpecialtyResponseDto surgeryDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(specialtyController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();

        radiology = new Specialty("radiology");
        radiology.setId(1);
        surgery = new Specialty("surgery");
        surgery.setId(2);

        radiologyDto = new SpecialtyResponseDto(1, "radiology");
        surgeryDto = new SpecialtyResponseDto(2, "surgery");
    }

    @Test
    void listSpecialties_returnsOk() throws Exception {
        given(specialtyService.findAll()).willReturn(List.of(radiology, surgery));
        given(specialtyMapper.toResponseDtos(any())).willReturn(List.of(radiologyDto, surgeryDto));

        mockMvc.perform(get("/api/specialties").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("radiology"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("surgery"));
    }

    @Test
    void getSpecialty_existingId_returnsOk() throws Exception {
        given(specialtyService.findById(1)).willReturn(radiology);
        given(specialtyMapper.toResponseDto(radiology)).willReturn(radiologyDto);

        mockMvc.perform(get("/api/specialties/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void getSpecialty_nonExistingId_returnsNotFound() throws Exception {
        given(specialtyService.findById(999)).willThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(get("/api/specialties/999").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void addSpecialty_validRequest_returnsCreated() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");
        Specialty dentistry = new Specialty("dentistry");
        dentistry.setId(3);
        SpecialtyResponseDto dentistryDto = new SpecialtyResponseDto(3, "dentistry");

        given(specialtyService.create(any(SpecialtyRequestDto.class))).willReturn(dentistry);
        given(specialtyMapper.toResponseDto(dentistry)).willReturn(dentistryDto);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.name").value("dentistry"));
    }

    @Test
    void addSpecialty_invalidRequest_returnsBadRequest() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto();
        request.setName("");

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addSpecialty_nullName_returnsBadRequest() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto();

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialty_existingId_returnsOk() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-radiology");
        Specialty updated = new Specialty("updated-radiology");
        updated.setId(1);
        SpecialtyResponseDto updatedDto = new SpecialtyResponseDto(1, "updated-radiology");

        given(specialtyService.update(eq(1), any(SpecialtyRequestDto.class))).willReturn(updated);
        given(specialtyMapper.toResponseDto(updated)).willReturn(updatedDto);

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("updated-radiology"));
    }

    @Test
    void updateSpecialty_nonExistingId_returnsNotFound() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");

        given(specialtyService.update(eq(999), any(SpecialtyRequestDto.class)))
            .willThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(put("/api/specialties/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_existingId_returnsNoContent() throws Exception {
        doNothing().when(specialtyService).delete(1);

        mockMvc.perform(delete("/api/specialties/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteSpecialty_nonExistingId_returnsNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Specialty", 999)).when(specialtyService).delete(999);

        mockMvc.perform(delete("/api/specialties/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void listSpecialties_emptyList_returnsOk() throws Exception {
        given(specialtyService.findAll()).willReturn(new ArrayList<>());
        given(specialtyMapper.toResponseDtos(any())).willReturn(new ArrayList<>());

        mockMvc.perform(get("/api/specialties").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }
}
