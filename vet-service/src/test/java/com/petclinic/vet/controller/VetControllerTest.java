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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link VetController} using @WebMvcTest.
 * The service layer is mocked to isolate controller/mapping/validation logic.
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

    /** Shared helper that builds a sample VetResponseDto for James Carter. */
    private VetResponseDto jamesCarterDto() {
        return new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    @DisplayName("GET /api/vets returns 200 with list of vets")
    void listVets_returnsOk() throws Exception {
        when(vetService.getAllVets()).thenReturn(List.of(jamesCarterDto()));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("James")));
    }

    @Test
    @DisplayName("GET /api/vets returns 200 with empty list")
    void listVets_returnsEmptyList() throws Exception {
        when(vetService.getAllVets()).thenReturn(List.of());

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/vets/{vetId} returns 200 when found")
    void getVet_found() throws Exception {
        when(vetService.getVetById(1)).thenReturn(jamesCarterDto());

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")))
            .andExpect(jsonPath("$.specialties", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/vets/{vetId} returns 404 when not found")
    void getVet_notFound() throws Exception {
        when(vetService.getVetById(999))
            .thenThrow(new ResourceNotFoundException("Vet not found with id: 999"));

        mockMvc.perform(get("/api/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Resource Not Found")));
    }

    @Test
    @DisplayName("POST /api/vets returns 201 with valid body")
    void addVet_createdSuccessfully() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(1));
        VetResponseDto response = new VetResponseDto(2, "Helen", "Leary",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.createVet(any())).thenReturn(response);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(2)))
            .andExpect(jsonPath("$.firstName", is("Helen")));
    }

    @Test
    @DisplayName("POST /api/vets returns 400 when firstName is blank")
    void addVet_blankFirstName_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("", "Leary", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    @DisplayName("POST /api/vets returns 400 when lastName is blank")
    void addVet_blankLastName_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/vets returns 400 when specialtyIds is null")
    void addVet_nullSpecialties_returns400() throws Exception {
        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"Helen\",\"lastName\":\"Leary\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/vets returns 400 when firstName has invalid characters")
    void addVet_invalidFirstName_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("James123", "Carter", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/vets/{vetId} returns 200 when updated")
    void updateVet_success() throws Exception {
        VetRequestDto request = new VetRequestDto("Jim", "Carter", List.of(1));
        VetResponseDto response = new VetResponseDto(1, "Jim", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.updateVet(eq(1), any())).thenReturn(response);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Jim")));
    }

    @Test
    @DisplayName("PUT /api/vets/{vetId} returns 404 when not found")
    void updateVet_notFound() throws Exception {
        VetRequestDto request = new VetRequestDto("Jim", "Carter", List.of());
        when(vetService.updateVet(eq(999), any()))
            .thenThrow(new ResourceNotFoundException("Vet not found with id: 999"));

        mockMvc.perform(put("/api/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/vets/{vetId} returns 200 when deleted")
    void deleteVet_success() throws Exception {
        when(vetService.deleteVet(1)).thenReturn(jamesCarterDto());

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("DELETE /api/vets/{vetId} returns 404 when not found")
    void deleteVet_notFound() throws Exception {
        when(vetService.deleteVet(999))
            .thenThrow(new ResourceNotFoundException("Vet not found with id: 999"));

        mockMvc.perform(delete("/api/vets/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/vets/search?name=Carter returns matching vets")
    void searchVets_returnsMatches() throws Exception {
        when(vetService.searchByName("Carter")).thenReturn(List.of(jamesCarterDto()));

        mockMvc.perform(get("/api/vets/search").param("name", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].lastName", is("Carter")));
    }

    @Test
    @DisplayName("GET /api/vets/specialty/{specialtyId} returns filtered vets")
    void findBySpecialty_returnsFiltered() throws Exception {
        when(vetService.findBySpecialtyId(1)).thenReturn(List.of(jamesCarterDto()));

        mockMvc.perform(get("/api/vets/specialty/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }
}
