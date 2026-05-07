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

    @Test
    void listVets_returnsAll() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.getAllVets()).thenReturn(List.of(vet));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("James"))
            .andExpect(jsonPath("$[0].lastName").value("Carter"))
            .andExpect(jsonPath("$[0].specialties[0].name").value("radiology"));
    }

    @Test
    void listVets_filterByLastName() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findByLastName("Carter")).thenReturn(List.of(vet));

        mockMvc.perform(get("/api/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].lastName").value("Carter"));
    }

    @Test
    void listVets_filterBySpecialty() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.findBySpecialtyName("radiology")).thenReturn(List.of(vet));

        mockMvc.perform(get("/api/vets").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].specialties[0].name").value("radiology"));
    }

    @Test
    void getVet_found() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.getVetById(1)).thenReturn(vet);

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void getVet_notFound() throws Exception {
        when(vetService.getVetById(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/api/vets/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Not Found"))
            .andExpect(jsonPath("$.detail").value("Vet not found with id: 99"));
    }

    @Test
    void addVet_success() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        VetResponseDto response = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.createVet(any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void addVet_validationError_blankFirstName() throws Exception {
        VetRequestDto request = new VetRequestDto("", "Carter", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Bad Request"))
            .andExpect(jsonPath("$.schemaValidationErrors").isArray());
    }

    @Test
    void addVet_validationError_nullSpecialties() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_success() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of());
        VetResponseDto response = new VetResponseDto(1, "Helen", "Leary", List.of());
        when(vetService.updateVet(eq(1), any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Helen"));
    }

    @Test
    void updateVet_notFound() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of());
        when(vetService.updateVet(eq(99), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(put("/api/vets/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_success() throws Exception {
        VetResponseDto response = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.deleteVet(1)).thenReturn(response);

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteVet_notFound() throws Exception {
        when(vetService.deleteVet(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(delete("/api/vets/99"))
            .andExpect(status().isNotFound());
    }
}
