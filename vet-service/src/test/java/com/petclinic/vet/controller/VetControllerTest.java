package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.service.VetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

    @MockBean
    private VetMapper vetMapper;

    private Vet vet;
    private VetResponseDto vetResponseDto;

    @BeforeEach
    void setUp() {
        Specialty radiology = new Specialty(1, "radiology");
        vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(Set.of(radiology));
        vet.setCreatedAt(LocalDateTime.now());
        vet.setUpdatedAt(LocalDateTime.now());

        SpecialtyDto specialtyDto = new SpecialtyDto(1, "radiology");
        vetResponseDto = new VetResponseDto(1, "James", "Carter",
            List.of(specialtyDto), vet.getCreatedAt(), vet.getUpdatedAt());
    }

    @Test
    void listVets_returnsVets() throws Exception {
        when(vetService.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtos(any())).thenReturn(List.of(vetResponseDto));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].firstName", is("James")))
            .andExpect(jsonPath("$[0].lastName", is("Carter")))
            .andExpect(jsonPath("$[0].specialties", hasSize(1)));
    }

    @Test
    void listVets_emptyList_returns404() throws Exception {
        when(vetService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("ResourceNotFoundException")))
            .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void addVet_validPayload_returns200() throws Exception {
        VetRequestDto requestDto = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));
        when(vetService.save(any(VetRequestDto.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(any(Vet.class))).thenReturn(vetResponseDto);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void addVet_invalidPayload_returns400() throws Exception {
        // Missing required fields
        VetRequestDto invalidDto = new VetRequestDto("", "", null);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("MethodArgumentNotValidException")))
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.schemaValidationErrors").isArray());
    }

    @Test
    void getVet_existingId_returns200() throws Exception {
        when(vetService.findById(1)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void getVet_nonExistingId_returns404() throws Exception {
        when(vetService.findById(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/api/vets/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("ResourceNotFoundException")))
            .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void updateVet_validPayload_returns200() throws Exception {
        VetRequestDto requestDto = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyDto(1, "radiology")));
        VetResponseDto updatedResponse = new VetResponseDto(1, "Helen", "Leary",
            List.of(new SpecialtyDto(1, "radiology")), LocalDateTime.now(), LocalDateTime.now());
        when(vetService.update(eq(1), any(VetRequestDto.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(any(Vet.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Helen")));
    }

    @Test
    void updateVet_nonExistingId_returns404() throws Exception {
        VetRequestDto requestDto = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyDto(1, "radiology")));
        when(vetService.update(eq(99), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(put("/api/vets/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateVet_invalidPayload_returns400() throws Exception {
        VetRequestDto invalidDto = new VetRequestDto("", "", null);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.schemaValidationErrors").isArray());
    }

    @Test
    void deleteVet_existingId_returns200() throws Exception {
        when(vetService.findById(1)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);
        doNothing().when(vetService).delete(1);

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteVet_nonExistingId_returns404() throws Exception {
        when(vetService.findById(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(delete("/api/vets/99"))
            .andExpect(status().isNotFound());
    }
}
