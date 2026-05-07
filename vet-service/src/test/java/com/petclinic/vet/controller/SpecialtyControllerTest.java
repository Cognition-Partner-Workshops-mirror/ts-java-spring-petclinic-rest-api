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

    @MockBean
    private SpecialtyService specialtyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listSpecialties_returnsOk() throws Exception {
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.listSpecialties()).thenReturn(List.of(dto));

        mockMvc.perform(get("/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("radiology"));
    }

    @Test
    void getSpecialty_returnsOk() throws Exception {
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.getSpecialty(1)).thenReturn(dto);

        mockMvc.perform(get("/specialties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void getSpecialty_notFound() throws Exception {
        when(specialtyService.getSpecialty(99)).thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(get("/specialties/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Specialty Not Found"));
    }

    @Test
    void addSpecialty_returnsOk() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        SpecialtyResponseDto response = new SpecialtyResponseDto(2, "surgery");
        when(specialtyService.addSpecialty(any(SpecialtyRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("surgery"));
    }

    @Test
    void addSpecialty_validationError() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    void addSpecialty_nullName() throws Exception {
        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialty_returnsOk() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");
        SpecialtyResponseDto response = new SpecialtyResponseDto(1, "dentistry");
        when(specialtyService.updateSpecialty(eq(1), any(SpecialtyRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/specialties/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("dentistry"));
    }

    @Test
    void updateSpecialty_notFound() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");
        when(specialtyService.updateSpecialty(eq(99), any(SpecialtyRequestDto.class)))
                .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(put("/specialties/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_returnsOk() throws Exception {
        SpecialtyResponseDto response = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.deleteSpecialty(1)).thenReturn(response);

        mockMvc.perform(delete("/specialties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteSpecialty_notFound() throws Exception {
        when(specialtyService.deleteSpecialty(99)).thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(delete("/specialties/99"))
                .andExpect(status().isNotFound());
    }
}
