package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.SpecialtyService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
@Import(GlobalExceptionHandler.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SpecialtyService specialtyService;

    @Test
    void listSpecialties_returnsAll() throws Exception {
        when(specialtyService.findAll()).thenReturn(List.of(
            new SpecialtyResponseDto(1, "radiology"),
            new SpecialtyResponseDto(2, "surgery")
        ));

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void listSpecialties_withNameFilter_returnsFiltered() throws Exception {
        when(specialtyService.searchByName("rad")).thenReturn(List.of(
            new SpecialtyResponseDto(1, "radiology")
        ));

        mockMvc.perform(get("/api/specialties").param("name", "rad"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void listSpecialties_withBlankNameFilter_returnsAll() throws Exception {
        when(specialtyService.findAll()).thenReturn(List.of(
            new SpecialtyResponseDto(1, "radiology")
        ));

        mockMvc.perform(get("/api/specialties").param("name", "  "))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getSpecialty_existingId_returnsSpecialty() throws Exception {
        when(specialtyService.findById(1)).thenReturn(
            new SpecialtyResponseDto(1, "radiology"));

        mockMvc.perform(get("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void getSpecialty_nonExistingId_returns404() throws Exception {
        when(specialtyService.findById(99))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(get("/api/specialties/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Not Found")));
    }

    @Test
    void addSpecialty_validRequest_returns201() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");
        when(specialtyService.create(any())).thenReturn(
            new SpecialtyResponseDto(3, "dentistry"));

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(3)))
            .andExpect(jsonPath("$.name", is("dentistry")));
    }

    @Test
    void addSpecialty_invalidRequest_returns400() throws Exception {
        String invalidJson = "{\"name\": \"\"}";

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    void addSpecialty_missingName_returns400() throws Exception {
        String invalidJson = "{}";

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialty_validRequest_returns200() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-radiology");
        when(specialtyService.update(eq(1), any())).thenReturn(
            new SpecialtyResponseDto(1, "updated-radiology"));

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("updated-radiology")));
    }

    @Test
    void updateSpecialty_nonExistingId_returns404() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(specialtyService.update(eq(99), any()))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(put("/api/specialties/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_existingId_returns200() throws Exception {
        when(specialtyService.delete(1)).thenReturn(
            new SpecialtyResponseDto(1, "radiology"));

        mockMvc.perform(delete("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteSpecialty_nonExistingId_returns404() throws Exception {
        when(specialtyService.delete(99))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(delete("/api/specialties/99"))
            .andExpect(status().isNotFound());
    }
}
