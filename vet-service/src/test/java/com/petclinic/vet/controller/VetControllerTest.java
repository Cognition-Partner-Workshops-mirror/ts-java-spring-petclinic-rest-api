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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VetController.class)
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VetService vetService;

    @Test
    void listVets_shouldReturnAllVets() throws Exception {
        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        VetResponseDto vetDto = new VetResponseDto(1, "James", "Carter", List.of(specialtyDto));
        when(vetService.findAll()).thenReturn(List.of(vetDto));

        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("James"))
            .andExpect(jsonPath("$[0].lastName").value("Carter"))
            .andExpect(jsonPath("$[0].specialties[0].name").value("radiology"));
    }

    @Test
    void listVets_withLastNameFilter_shouldReturnFilteredVets() throws Exception {
        VetResponseDto vetDto = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.searchByLastName("Carter")).thenReturn(List.of(vetDto));

        mockMvc.perform(get("/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].lastName").value("Carter"));
    }

    @Test
    void listVets_withSpecialtyFilter_shouldReturnFilteredVets() throws Exception {
        VetResponseDto vetDto = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findBySpecialty(1)).thenReturn(List.of(vetDto));

        mockMvc.perform(get("/vets").param("specialtyId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("James"));
    }

    @Test
    void getVet_shouldReturnVet() throws Exception {
        VetResponseDto vetDto = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findById(1)).thenReturn(vetDto);

        mockMvc.perform(get("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void getVet_notFound_shouldReturn404() throws Exception {
        when(vetService.findById(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/vets/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Vet not found"));
    }

    @Test
    void addVet_shouldCreateVet() throws Exception {
        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto requestDto = new VetRequestDto("James", "Carter", List.of(specialtyDto));
        VetResponseDto responseDto = new VetResponseDto(1, "James", "Carter", List.of(specialtyDto));
        when(vetService.create(any(VetRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void addVet_invalidInput_shouldReturn400() throws Exception {
        VetRequestDto invalidDto = new VetRequestDto("", "", null);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_shouldUpdateVet() throws Exception {
        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto requestDto = new VetRequestDto("James", "Carter", List.of(specialtyDto));
        VetResponseDto responseDto = new VetResponseDto(1, "James", "Carter", List.of(specialtyDto));
        when(vetService.update(eq(1), any(VetRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void updateVet_notFound_shouldReturn404() throws Exception {
        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto requestDto = new VetRequestDto("James", "Carter", List.of(specialtyDto));
        when(vetService.update(eq(99), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(put("/vets/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_shouldDeleteVet() throws Exception {
        VetResponseDto responseDto = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.delete(1)).thenReturn(responseDto);

        mockMvc.perform(delete("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteVet_notFound_shouldReturn404() throws Exception {
        when(vetService.delete(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(delete("/vets/99"))
            .andExpect(status().isNotFound());
    }
}
