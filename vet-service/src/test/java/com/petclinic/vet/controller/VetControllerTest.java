package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.request.SpecialtyRequestDto;
import com.petclinic.vet.dto.request.VetRequestDto;
import com.petclinic.vet.dto.response.SpecialtyResponseDto;
import com.petclinic.vet.dto.response.VetResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VetController.class)
@Import(GlobalExceptionHandler.class)
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VetService vetService;

    @Test
    void listVets_returnsAllVets() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findAll()).thenReturn(List.of(vet));

        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("James")));
    }

    @Test
    void listVets_withSpecialtyFilter_returnsBySpecialty() throws Exception {
        VetResponseDto vet = new VetResponseDto(2, "Helen", "Leary",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.findBySpecialty("radiology")).thenReturn(List.of(vet));

        mockMvc.perform(get("/vets").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("Helen")));
    }

    @Test
    void listVets_withNameFilter_returnsByName() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.searchByName("jam")).thenReturn(List.of(vet));

        mockMvc.perform(get("/vets").param("name", "jam"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getVet_existing_returnsVet() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findById(1)).thenReturn(vet);

        mockMvc.perform(get("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void getVet_nonExisting_returns404() throws Exception {
        when(vetService.findById(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/vets/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("ResourceNotFoundException")));
    }

    @Test
    void addVet_valid_returnsCreated() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyRequestDto(null, "radiology")));
        VetResponseDto response = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.create(any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")))
            .andExpect(header().exists("Location"));
    }

    @Test
    void addVet_invalidBody_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("", "", null);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("MethodArgumentNotValidException")));
    }

    @Test
    void updateVet_valid_returnsOk() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Name", List.of());
        VetResponseDto response = new VetResponseDto(1, "Updated", "Name", List.of());
        when(vetService.update(eq(1), any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Updated")));
    }

    @Test
    void updateVet_notFound_returns404() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Name", List.of());
        when(vetService.update(eq(99), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(put("/vets/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_existing_returnsOk() throws Exception {
        VetResponseDto response = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.delete(1)).thenReturn(response);

        mockMvc.perform(delete("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteVet_notFound_returns404() throws Exception {
        when(vetService.delete(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(delete("/vets/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void listVets_emptySpecialtyParam_returnsAll() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findAll()).thenReturn(List.of(vet));

        mockMvc.perform(get("/vets").param("specialty", ""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_emptyNameParam_returnsAll() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findAll()).thenReturn(List.of(vet));

        mockMvc.perform(get("/vets").param("name", ""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }
}
