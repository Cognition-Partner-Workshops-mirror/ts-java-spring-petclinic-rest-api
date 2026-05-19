package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for VetController using @WebMvcTest.
 * Tests REST endpoint behavior, query-parameter filtering, validation, and error responses.
 */
@WebMvcTest(VetController.class)
@Import(GlobalExceptionHandler.class)
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VetService vetService;

    private VetResponseDto sampleVet() {
        return new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    @DisplayName("GET /api/vets returns 200 with list of vets")
    void listVets_returns200() throws Exception {
        when(vetService.getAllVets()).thenReturn(List.of(sampleVet()));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("James")))
            .andExpect(jsonPath("$[0].specialties", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/vets returns empty list when no vets")
    void listVets_empty() throws Exception {
        when(vetService.getAllVets()).thenReturn(List.of());

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/vets?lastName=Carter filters by last name")
    void listVets_filterByLastName() throws Exception {
        when(vetService.searchByLastName("Carter")).thenReturn(List.of(sampleVet()));

        mockMvc.perform(get("/api/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].lastName", is("Carter")));
    }

    @Test
    @DisplayName("GET /api/vets?specialty=radiology filters by specialty")
    void listVets_filterBySpecialty() throws Exception {
        when(vetService.filterBySpecialty("radiology")).thenReturn(List.of(sampleVet()));

        mockMvc.perform(get("/api/vets").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/vets?lastName= (blank) returns all vets")
    void listVets_blankLastName_returnsAll() throws Exception {
        when(vetService.getAllVets()).thenReturn(List.of(sampleVet()));

        mockMvc.perform(get("/api/vets").param("lastName", ""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/vets/{vetId} returns 200 when found")
    void getVet_returns200() throws Exception {
        when(vetService.getVetById(1)).thenReturn(sampleVet());

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    @DisplayName("GET /api/vets/{vetId} returns 404 when not found")
    void getVet_returns404() throws Exception {
        when(vetService.getVetById(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/api/vets/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Resource Not Found")));
    }

    @Test
    @DisplayName("POST /api/vets returns 201 on success")
    void addVet_returns201() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(1));
        when(vetService.createVet(any(VetRequestDto.class))).thenReturn(sampleVet());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    @DisplayName("POST /api/vets returns 400 for blank first name")
    void addVet_blankFirstName_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("", "Carter", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    @DisplayName("POST /api/vets returns 400 for blank last name")
    void addVet_blankLastName_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/vets returns 400 for invalid first name pattern")
    void addVet_invalidPattern_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("James123", "Carter", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/vets returns 404 when specialty not found")
    void addVet_specialtyNotFound_returns404() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(99));
        when(vetService.createVet(any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/vets/{vetId} returns 200 on success")
    void updateVet_returns200() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of(1));
        VetResponseDto updated = new VetResponseDto(1, "Updated", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.updateVet(eq(1), any(VetRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Updated")));
    }

    @Test
    @DisplayName("PUT /api/vets/{vetId} returns 404 when not found")
    void updateVet_returns404() throws Exception {
        VetRequestDto request = new VetRequestDto("Test", "Test", List.of());
        when(vetService.updateVet(eq(99), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(put("/api/vets/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/vets/{vetId} returns 400 for invalid body")
    void updateVet_invalidBody_returns400() throws Exception {
        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\": \"\", \"lastName\": \"\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/vets/{vetId} returns 204 on success")
    void deleteVet_returns204() throws Exception {
        doNothing().when(vetService).deleteVet(1);

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/vets/{vetId} returns 404 when not found")
    void deleteVet_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Vet", 99))
            .when(vetService).deleteVet(99);

        mockMvc.perform(delete("/api/vets/99"))
            .andExpect(status().isNotFound());
    }
}
