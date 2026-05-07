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
import java.util.ArrayList;
import java.util.List;

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

    @MockBean
    private VetService vetService;

    @Autowired
    private ObjectMapper objectMapper;

    private VetResponseDto sampleResponse() {
        SpecialtyResponseDto spec = new SpecialtyResponseDto(1, "radiology");
        return new VetResponseDto(1, "James", "Carter", List.of(spec));
    }

    private VetRequestDto sampleRequest() {
        SpecialtyResponseDto spec = new SpecialtyResponseDto(1, "radiology");
        return new VetRequestDto("James", "Carter", List.of(spec));
    }

    @Test
    void listVets_returnsOk() throws Exception {
        when(vetService.listVets()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/vets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("James"))
                .andExpect(jsonPath("$[0].specialties[0].name").value("radiology"));
    }

    @Test
    void getVet_returnsOk() throws Exception {
        when(vetService.getVet(1)).thenReturn(sampleResponse());

        mockMvc.perform(get("/vets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("James"))
                .andExpect(jsonPath("$.lastName").value("Carter"));
    }

    @Test
    void getVet_notFound() throws Exception {
        when(vetService.getVet(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/vets/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Vet Not Found"));
    }

    @Test
    void addVet_returnsOk() throws Exception {
        when(vetService.addVet(any(VetRequestDto.class))).thenReturn(sampleResponse());

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void addVet_validationError_blankFirstName() throws Exception {
        VetRequestDto bad = new VetRequestDto("", "Carter", new ArrayList<>());

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    void addVet_validationError_blankLastName() throws Exception {
        VetRequestDto bad = new VetRequestDto("James", "", new ArrayList<>());

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_validationError_nullSpecialties() throws Exception {
        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"James\",\"lastName\":\"Carter\",\"specialties\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_returnsOk() throws Exception {
        when(vetService.updateVet(eq(1), any(VetRequestDto.class))).thenReturn(sampleResponse());

        mockMvc.perform(put("/vets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void updateVet_notFound() throws Exception {
        when(vetService.updateVet(eq(99), any(VetRequestDto.class)))
                .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(put("/vets/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_returnsOk() throws Exception {
        when(vetService.deleteVet(1)).thenReturn(sampleResponse());

        mockMvc.perform(delete("/vets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteVet_notFound() throws Exception {
        when(vetService.deleteVet(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(delete("/vets/99"))
                .andExpect(status().isNotFound());
    }
}
