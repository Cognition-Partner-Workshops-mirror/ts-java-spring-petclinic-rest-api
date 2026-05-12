package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link VetController} using @WebMvcTest.
 * Tests HTTP-level request handling, validation, filtering, and error responses.
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

    private VetResponseDto sampleVetDto() {
        return new VetResponseDto(1, "James", "Carter",
                List.of(new SpecialtyResponseDto(1, "radiology")),
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-01T00:00:00Z"));
    }

    @Test
    void listVets_shouldReturnAllVets() throws Exception {
        when(vetService.getAllVets()).thenReturn(List.of(sampleVetDto()));

        mockMvc.perform(get("/api/vets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("James")));
    }

    @Test
    void listVets_shouldFilterByLastName() throws Exception {
        when(vetService.searchByLastName("Car")).thenReturn(List.of(sampleVetDto()));

        mockMvc.perform(get("/api/vets").param("lastName", "Car"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(vetService).searchByLastName("Car");
    }

    @Test
    void listVets_shouldFilterBySpecialtyId() throws Exception {
        when(vetService.filterBySpecialty(1)).thenReturn(List.of(sampleVetDto()));

        mockMvc.perform(get("/api/vets").param("specialtyId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(vetService).filterBySpecialty(1);
    }

    @Test
    void listVets_shouldFilterByLastNameAndSpecialty() throws Exception {
        when(vetService.filterByLastNameAndSpecialty("Car", 1))
                .thenReturn(List.of(sampleVetDto()));

        mockMvc.perform(get("/api/vets")
                        .param("lastName", "Car")
                        .param("specialtyId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(vetService).filterByLastNameAndSpecialty("Car", 1);
    }

    @Test
    void listVets_shouldReturnEmptyList() throws Exception {
        when(vetService.getAllVets()).thenReturn(List.of());

        mockMvc.perform(get("/api/vets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getVet_shouldReturnVet() throws Exception {
        when(vetService.getVetById(1)).thenReturn(sampleVetDto());

        mockMvc.perform(get("/api/vets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("James")))
                .andExpect(jsonPath("$.lastName", is("Carter")))
                .andExpect(jsonPath("$.specialties", hasSize(1)));
    }

    @Test
    void getVet_shouldReturn404WhenNotFound() throws Exception {
        when(vetService.getVetById(99))
                .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/api/vets/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Resource Not Found")));
    }

    @Test
    void addVet_shouldCreateVet() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(1));
        when(vetService.createVet(any())).thenReturn(sampleVetDto());

        mockMvc.perform(post("/api/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void addVet_shouldReturn400WhenFirstNameBlank() throws Exception {
        VetRequestDto request = new VetRequestDto("", "Carter", List.of());

        mockMvc.perform(post("/api/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    void addVet_shouldReturn400WhenLastNameBlank() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "", List.of());

        mockMvc.perform(post("/api/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_shouldReturn400WhenFirstNameTooLong() throws Exception {
        VetRequestDto request = new VetRequestDto("a".repeat(31), "Carter", List.of());

        mockMvc.perform(post("/api/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_shouldReturn400WhenFirstNameInvalid() throws Exception {
        VetRequestDto request = new VetRequestDto("James123", "Carter", List.of());

        mockMvc.perform(post("/api/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_shouldReturn400WhenLastNameInvalid() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter@!", List.of());

        mockMvc.perform(post("/api/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_shouldUpdateVet() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(1));
        VetResponseDto updated = new VetResponseDto(1, "Helen", "Leary",
                List.of(new SpecialtyResponseDto(1, "radiology")),
                Instant.now(), Instant.now());
        when(vetService.updateVet(eq(1), any())).thenReturn(updated);

        mockMvc.perform(put("/api/vets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Helen")));
    }

    @Test
    void updateVet_shouldReturn404WhenNotFound() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of());
        when(vetService.updateVet(eq(99), any()))
                .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(put("/api/vets/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateVet_shouldReturn400WhenInvalid() throws Exception {
        VetRequestDto request = new VetRequestDto("", "", List.of());

        mockMvc.perform(put("/api/vets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteVet_shouldReturn204() throws Exception {
        doNothing().when(vetService).deleteVet(1);

        mockMvc.perform(delete("/api/vets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteVet_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Vet", 99))
                .when(vetService).deleteVet(99);

        mockMvc.perform(delete("/api/vets/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addVet_shouldAcceptValidHyphenatedName() throws Exception {
        VetRequestDto request = new VetRequestDto("Jean-Pierre", "O'Brien", List.of());
        when(vetService.createVet(any())).thenReturn(
                new VetResponseDto(2, "Jean-Pierre", "O'Brien", List.of(), Instant.now(), Instant.now()));

        mockMvc.perform(post("/api/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("Jean-Pierre")));
    }

    @Test
    void addVet_shouldReturn400WhenBodyMissing() throws Exception {
        mockMvc.perform(post("/api/vets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
