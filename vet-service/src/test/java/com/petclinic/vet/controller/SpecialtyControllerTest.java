package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.SpecialtyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    @MockitoBean
    private SpecialtyService specialtyService;

    @Autowired
    private ObjectMapper objectMapper;

    private final SpecialtyResponse specialtyResponse = new SpecialtyResponse(1, "radiology");

    @Test
    void listSpecialties_returnsOk() throws Exception {
        when(specialtyService.findAll()).thenReturn(List.of(specialtyResponse));

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void getSpecialty_existing_returnsOk() throws Exception {
        when(specialtyService.findById(1)).thenReturn(specialtyResponse);

        mockMvc.perform(get("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void getSpecialty_nonExisting_returns404() throws Exception {
        when(specialtyService.findById(999)).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(get("/api/specialties/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Not Found")));
    }

    @Test
    void addSpecialty_validRequest_returnsOk() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("radiology");
        when(specialtyService.create(any(SpecialtyRequest.class))).thenReturn(specialtyResponse);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void addSpecialty_invalidRequest_returns400() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("");

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialty_validRequest_returnsOk() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("surgery");
        SpecialtyResponse updated = new SpecialtyResponse(1, "surgery");
        when(specialtyService.update(eq(1), any(SpecialtyRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("surgery")));
    }

    @Test
    void updateSpecialty_nonExisting_returns404() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("surgery");
        when(specialtyService.update(eq(999), any(SpecialtyRequest.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(put("/api/specialties/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_existing_returnsOk() throws Exception {
        when(specialtyService.delete(1)).thenReturn(specialtyResponse);

        mockMvc.perform(delete("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteSpecialty_nonExisting_returns404() throws Exception {
        when(specialtyService.delete(999)).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(delete("/api/specialties/999"))
            .andExpect(status().isNotFound());
    }
}
