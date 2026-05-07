package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
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

import java.util.Collections;
import java.util.List;

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

    @MockitoBean
    private VetService vetService;

    private VetResponseDto sampleVet() {
        return new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void listVets_returnsOk() throws Exception {
        when(vetService.listVets()).thenReturn(List.of(sampleVet()));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("James"))
            .andExpect(jsonPath("$[0].specialties[0].name").value("radiology"));
    }

    @Test
    void listVets_filterBySpecialty() throws Exception {
        when(vetService.findBySpecialty("radiology")).thenReturn(List.of(sampleVet()));

        mockMvc.perform(get("/api/vets").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("James"));
    }

    @Test
    void listVets_searchByName() throws Exception {
        when(vetService.searchByName("Carter")).thenReturn(List.of(sampleVet()));

        mockMvc.perform(get("/api/vets").param("name", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].lastName").value("Carter"));
    }

    @Test
    void getVet_found() throws Exception {
        when(vetService.getVet(1)).thenReturn(sampleVet());

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void getVet_notFound() throws Exception {
        when(vetService.getVet(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/api/vets/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Vet not found"));
    }

    @Test
    void addVet_created() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyRequestDto("radiology")));
        when(vetService.createVet(any())).thenReturn(sampleVet());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void addVet_invalidFirstName() throws Exception {
        VetRequestDto request = new VetRequestDto("", "Leary", Collections.emptyList());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_nullSpecialties() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", null);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_success() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Name",
            Collections.emptyList());
        VetResponseDto response = new VetResponseDto(1, "Updated", "Name", Collections.emptyList());
        when(vetService.updateVet(eq(1), any())).thenReturn(response);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    void updateVet_notFound() throws Exception {
        when(vetService.updateVet(eq(99), any()))
            .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(put("/api/vets/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new VetRequestDto("A", "B", Collections.emptyList()))))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_success() throws Exception {
        when(vetService.deleteVet(1)).thenReturn(sampleVet());

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteVet_notFound() throws Exception {
        when(vetService.deleteVet(99))
            .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(delete("/api/vets/99"))
            .andExpect(status().isNotFound());
    }
}
