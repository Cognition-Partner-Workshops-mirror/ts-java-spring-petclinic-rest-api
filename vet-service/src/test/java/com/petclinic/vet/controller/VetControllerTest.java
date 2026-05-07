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

@WebMvcTest(VetController.class)
@Import(GlobalExceptionHandler.class)
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VetService vetService;

    private final VetResponseDto sampleVet = new VetResponseDto(1, "James", "Carter",
        List.of(new SpecialtyResponseDto(1, "radiology")));

    @Test
    void listVets_noParams_returnsAll() throws Exception {
        when(vetService.findAll()).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("James")));
    }

    @Test
    void listVets_withLastName_filtersResults() throws Exception {
        when(vetService.findByLastName("Carter")).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_withSpecialty_filtersResults() throws Exception {
        when(vetService.findBySpecialtyName("radiology")).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/vets").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_withBothParams_filtersResults() throws Exception {
        when(vetService.findByLastNameAndSpecialtyName("Carter", "radiology")).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/vets")
                .param("lastName", "Carter")
                .param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getVet_existingId_returnsOk() throws Exception {
        when(vetService.findById(1)).thenReturn(sampleVet);

        mockMvc.perform(get("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")))
            .andExpect(jsonPath("$.specialties", hasSize(1)));
    }

    @Test
    void getVet_nonExistingId_returns404() throws Exception {
        when(vetService.findById(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/vets/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Vet Not Found")));
    }

    @Test
    void addVet_validRequest_returnsCreated() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(1));
        when(vetService.create(any())).thenReturn(sampleVet);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void addVet_blankFirstName_returnsBadRequest() throws Exception {
        VetRequestDto request = new VetRequestDto("", "Carter", List.of());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_blankLastName_returnsBadRequest() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "", List.of());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_invalidSpecialtyId_returns404() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(99));
        when(vetService.create(any())).thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateVet_validRequest_returnsOk() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(1));
        VetResponseDto updated = new VetResponseDto(1, "Helen", "Leary",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.update(eq(1), any())).thenReturn(updated);

        mockMvc.perform(put("/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Helen")));
    }

    @Test
    void updateVet_nonExistingId_returns404() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of());
        when(vetService.update(eq(99), any())).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(put("/vets/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_existingId_returnsOk() throws Exception {
        when(vetService.delete(1)).thenReturn(sampleVet);

        mockMvc.perform(delete("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteVet_nonExistingId_returns404() throws Exception {
        when(vetService.delete(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(delete("/vets/99"))
            .andExpect(status().isNotFound());
    }
}
