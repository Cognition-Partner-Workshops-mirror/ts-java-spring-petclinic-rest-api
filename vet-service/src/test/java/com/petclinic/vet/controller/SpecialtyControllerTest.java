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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpecialtyController.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SpecialtyService specialtyService;

    @Test
    void listSpecialties_returnsAll() throws Exception {
        SpecialtyResponseDto s1 = new SpecialtyResponseDto(1, "radiology");
        SpecialtyResponseDto s2 = new SpecialtyResponseDto(2, "surgery");

        when(specialtyService.findAll()).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name", is("radiology")))
            .andExpect(jsonPath("$[1].name", is("surgery")));
    }

    @Test
    void listSpecialties_withNameParam_returnsFiltered() throws Exception {
        SpecialtyResponseDto s1 = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.searchByName("rad")).thenReturn(List.of(s1));

        mockMvc.perform(get("/api/specialties").param("name", "rad"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void addSpecialty_validRequest_returnsCreated() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        SpecialtyResponseDto response = new SpecialtyResponseDto(1, "radiology");

        when(specialtyService.create(any(SpecialtyRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void addSpecialty_emptyName_returnsBadRequest() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)));
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
    void addSpecialty_nameTooLong_returnsBadRequest() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("A".repeat(81));

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getSpecialty_existingId_returnsSpecialty() throws Exception {
        SpecialtyResponseDto response = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.findById(1)).thenReturn(response);

        mockMvc.perform(get("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void getSpecialty_nonExistingId_returnsNotFound() throws Exception {
        when(specialtyService.findById(999))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(get("/api/specialties/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void updateSpecialty_validRequest_returnsUpdated() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("oncology");
        SpecialtyResponseDto response = new SpecialtyResponseDto(1, "oncology");

        when(specialtyService.update(eq(1), any(SpecialtyRequestDto.class)))
            .thenReturn(response);

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("oncology")));
    }

    @Test
    void updateSpecialty_nonExistingId_returnsNotFound() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("oncology");
        when(specialtyService.update(eq(999), any(SpecialtyRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(put("/api/specialties/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateSpecialty_invalidRequest_returnsBadRequest() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSpecialty_existingId_returnsDeleted() throws Exception {
        SpecialtyResponseDto response = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.delete(1)).thenReturn(response);

        mockMvc.perform(delete("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteSpecialty_nonExistingId_returnsNotFound() throws Exception {
        when(specialtyService.delete(999))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(delete("/api/specialties/999"))
            .andExpect(status().isNotFound());
    }
}
