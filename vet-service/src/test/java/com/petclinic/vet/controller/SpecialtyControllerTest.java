package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.service.SpecialtyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for SpecialtyController using @WebMvcTest.
 */
@WebMvcTest(SpecialtyController.class)
@Import(GlobalExceptionHandler.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SpecialtyService specialtyService;

    @MockBean
    private SpecialtyMapper specialtyMapper;

    private Specialty radiology;
    private SpecialtyDto radiologyDto;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        radiologyDto = new SpecialtyDto(1, "radiology");
    }

    @Test
    void listSpecialties_returnsSpecialties() throws Exception {
        Specialty surgery = new Specialty(2, "surgery");
        SpecialtyDto surgeryDto = new SpecialtyDto(2, "surgery");
        when(specialtyService.findAll()).thenReturn(List.of(radiology, surgery));
        when(specialtyMapper.toDtos(any())).thenReturn(List.of(radiologyDto, surgeryDto));

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void listSpecialties_emptyList_returns200() throws Exception {
        when(specialtyService.findAll()).thenReturn(Collections.emptyList());
        when(specialtyMapper.toDtos(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void addSpecialty_validPayload_returns200() throws Exception {
        SpecialtyDto inputDto = new SpecialtyDto(null, "radiology");
        when(specialtyService.save(any(SpecialtyDto.class))).thenReturn(radiology);
        when(specialtyMapper.toDto(radiology)).thenReturn(radiologyDto);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void addSpecialty_invalidPayload_returns400() throws Exception {
        SpecialtyDto invalidDto = new SpecialtyDto(null, "");

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("MethodArgumentNotValidException")))
            .andExpect(jsonPath("$.schemaValidationErrors").isArray());
    }

    @Test
    void getSpecialty_existingId_returns200() throws Exception {
        when(specialtyService.findById(1)).thenReturn(radiology);
        when(specialtyMapper.toDto(radiology)).thenReturn(radiologyDto);

        mockMvc.perform(get("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void getSpecialty_nonExistingId_returns404() throws Exception {
        when(specialtyService.findById(99)).thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(get("/api/specialties/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("ResourceNotFoundException")))
            .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void updateSpecialty_validPayload_returns200() throws Exception {
        SpecialtyDto inputDto = new SpecialtyDto(null, "oncology");
        Specialty updated = new Specialty(1, "oncology");
        SpecialtyDto updatedDto = new SpecialtyDto(1, "oncology");
        when(specialtyService.update(eq(1), any(SpecialtyDto.class))).thenReturn(updated);
        when(specialtyMapper.toDto(updated)).thenReturn(updatedDto);

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("oncology")));
    }

    @Test
    void updateSpecialty_nonExistingId_returns404() throws Exception {
        SpecialtyDto inputDto = new SpecialtyDto(null, "oncology");
        when(specialtyService.update(eq(99), any(SpecialtyDto.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(put("/api/specialties/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateSpecialty_invalidPayload_returns400() throws Exception {
        SpecialtyDto invalidDto = new SpecialtyDto(null, "");

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSpecialty_existingId_returns200() throws Exception {
        when(specialtyService.findById(1)).thenReturn(radiology);
        when(specialtyMapper.toDto(radiology)).thenReturn(radiologyDto);
        doNothing().when(specialtyService).delete(1);

        mockMvc.perform(delete("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteSpecialty_nonExistingId_returns404() throws Exception {
        when(specialtyService.findById(99)).thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(delete("/api/specialties/99"))
            .andExpect(status().isNotFound());
    }
}
