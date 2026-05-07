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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpecialtyRestController.class)
class SpecialtyRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SpecialtyService specialtyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listSpecialties_shouldReturnAll() throws Exception {
        List<SpecialtyResponseDto> specialties = List.of(
                new SpecialtyResponseDto(1, "radiology"),
                new SpecialtyResponseDto(2, "surgery")
        );
        when(specialtyService.findAll()).thenReturn(specialties);

        mockMvc.perform(get("/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void listSpecialties_shouldFilterByName() throws Exception {
        List<SpecialtyResponseDto> specialties = List.of(
                new SpecialtyResponseDto(1, "radiology")
        );
        when(specialtyService.searchByName("rad")).thenReturn(specialties);

        mockMvc.perform(get("/specialties").param("name", "rad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listSpecialties_shouldReturnEmptyList() throws Exception {
        when(specialtyService.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getSpecialty_shouldReturnSpecialty() throws Exception {
        SpecialtyResponseDto specialty = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.findById(1)).thenReturn(specialty);

        mockMvc.perform(get("/specialties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void getSpecialty_shouldReturn404WhenNotFound() throws Exception {
        when(specialtyService.findById(999)).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(get("/specialties/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Not Found")));
    }

    @Test
    void addSpecialty_shouldCreateAndReturnSpecialty() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("cardiology");
        SpecialtyResponseDto created = new SpecialtyResponseDto(4, "cardiology");
        when(specialtyService.create(any(SpecialtyRequestDto.class))).thenReturn(created);

        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.name", is("cardiology")))
                .andExpect(header().exists("Location"));
    }

    @Test
    void addSpecialty_shouldReturn400ForInvalidRequest() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addSpecialty_shouldReturn400ForNullName() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialty_shouldUpdateAndReturn() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        SpecialtyResponseDto updated = new SpecialtyResponseDto(1, "updated");
        when(specialtyService.update(eq(1), any(SpecialtyRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put("/specialties/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("updated")));
    }

    @Test
    void updateSpecialty_shouldReturn404WhenNotFound() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("test");
        when(specialtyService.update(eq(999), any(SpecialtyRequestDto.class)))
                .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(put("/specialties/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateSpecialty_shouldReturn400ForInvalidRequest() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(put("/specialties/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSpecialty_shouldDeleteAndReturn() throws Exception {
        SpecialtyResponseDto deleted = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.delete(1)).thenReturn(deleted);

        mockMvc.perform(delete("/specialties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteSpecialty_shouldReturn404WhenNotFound() throws Exception {
        when(specialtyService.delete(999)).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(delete("/specialties/999"))
                .andExpect(status().isNotFound());
    }
}
