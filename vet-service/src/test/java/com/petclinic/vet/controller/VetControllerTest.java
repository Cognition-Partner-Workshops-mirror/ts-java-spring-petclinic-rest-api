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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for VetController using @WebMvcTest.
 * Validates REST mappings, request validation, filtering, and error handling.
 */
@WebMvcTest(VetController.class)
@Import({VetControllerTest.MockConfig.class, GlobalExceptionHandler.class})
class VetControllerTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        public VetService vetService() {
            return mock(VetService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VetService vetService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listVets_returnsOkWithList() throws Exception {
        List<VetResponseDto> vets = List.of(
            new VetResponseDto(1, "James", "Carter", Collections.emptyList()),
            new VetResponseDto(2, "Helen", "Leary", List.of(new SpecialtyResponseDto(1, "radiology")))
        );
        when(vetService.getAllVets()).thenReturn(vets);

        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].firstName").value("James"));
    }

    @Test
    void listVets_filterByLastName_returnsFiltered() throws Exception {
        List<VetResponseDto> filtered = List.of(
            new VetResponseDto(1, "James", "Carter", Collections.emptyList())
        );
        when(vetService.findByLastName("Carter")).thenReturn(filtered);

        mockMvc.perform(get("/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].lastName").value("Carter"));
    }

    @Test
    void listVets_filterBySpecialtyName_returnsFiltered() throws Exception {
        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        List<VetResponseDto> filtered = List.of(
            new VetResponseDto(2, "Helen", "Leary", List.of(specialtyDto))
        );
        when(vetService.findBySpecialtyName("radiology")).thenReturn(filtered);

        mockMvc.perform(get("/vets").param("specialtyName", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].specialties[0].name").value("radiology"));
    }

    @Test
    void getVet_found_returnsOk() throws Exception {
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", Collections.emptyList());
        when(vetService.getVetById(1)).thenReturn(dto);

        mockMvc.perform(get("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"))
            .andExpect(jsonPath("$.lastName").value("Carter"));
    }

    @Test
    void getVet_notFound_returns404() throws Exception {
        when(vetService.getVetById(999))
            .thenThrow(new ResourceNotFoundException("Vet not found with id: 999"));

        mockMvc.perform(get("/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Resource Not Found"))
            .andExpect(jsonPath("$.detail").value("Vet not found with id: 999"));
    }

    @Test
    void addVet_validRequest_returnsOk() throws Exception {
        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(specialtyDto));
        VetResponseDto response = new VetResponseDto(7, "Helen", "Leary", List.of(specialtyDto));
        when(vetService.createVet(any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(7))
            .andExpect(jsonPath("$.firstName").value("Helen"));
    }

    @Test
    void addVet_blankFirstName_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("", "Leary", Collections.emptyList());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    void addVet_blankLastName_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "", Collections.emptyList());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_nullSpecialties_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", null);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_invalidFirstNamePattern_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("123Invalid", "Leary", Collections.emptyList());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_validRequest_returnsOk() throws Exception {
        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of(specialtyDto));
        VetResponseDto response = new VetResponseDto(1, "Updated", "Carter", List.of(specialtyDto));
        when(vetService.updateVet(eq(1), any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    void updateVet_notFound_returns404() throws Exception {
        VetRequestDto request = new VetRequestDto("X", "Y", Collections.emptyList());
        when(vetService.updateVet(eq(999), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet not found with id: 999"));

        mockMvc.perform(put("/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_found_returnsOk() throws Exception {
        VetResponseDto response = new VetResponseDto(1, "James", "Carter", Collections.emptyList());
        when(vetService.deleteVet(1)).thenReturn(response);

        mockMvc.perform(delete("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void deleteVet_notFound_returns404() throws Exception {
        when(vetService.deleteVet(999))
            .thenThrow(new ResourceNotFoundException("Vet not found with id: 999"));

        mockMvc.perform(delete("/vets/999"))
            .andExpect(status().isNotFound());
    }
}
