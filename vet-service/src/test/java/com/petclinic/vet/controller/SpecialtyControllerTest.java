package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.SpecialtyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpecialtyController.class)
@Import(GlobalExceptionHandler.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SpecialtyService specialtyService;

    private final SpecialtyResponseDto responseDto = new SpecialtyResponseDto(1, "radiology");

    @Test
    void listSpecialties_returnsAll() throws Exception {
        when(specialtyService.listAll()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void listSpecialties_filterByName() throws Exception {
        when(specialtyService.searchByName("rad")).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/specialties").param("name", "rad"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getSpecialty_found() throws Exception {
        when(specialtyService.getById(1)).thenReturn(responseDto);

        mockMvc.perform(get("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void getSpecialty_notFound() throws Exception {
        when(specialtyService.getById(99)).thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(get("/api/specialties/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Not Found")));
    }

    @Test
    void addSpecialty_valid() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        when(specialtyService.create(any(SpecialtyRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void addSpecialty_invalidBlankName() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    void addSpecialty_invalidNullName() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialty_valid() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        SpecialtyResponseDto updated = new SpecialtyResponseDto(1, "surgery");
        when(specialtyService.update(eq(1), any(SpecialtyRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("surgery")));
    }

    @Test
    void updateSpecialty_notFound() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        when(specialtyService.update(eq(99), any(SpecialtyRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(put("/api/specialties/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_found() throws Exception {
        when(specialtyService.delete(1)).thenReturn(responseDto);

        mockMvc.perform(delete("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteSpecialty_notFound() throws Exception {
        when(specialtyService.delete(99)).thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(delete("/api/specialties/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void addSpecialty_nameTooLong() throws Exception {
        String longName = "a".repeat(81);
        SpecialtyRequestDto request = new SpecialtyRequestDto(longName);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}
