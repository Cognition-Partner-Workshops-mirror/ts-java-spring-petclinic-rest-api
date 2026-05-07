package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    void listVets_returnsAllVets() throws Exception {
        when(vetService.findAll()).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("James"))
            .andExpect(jsonPath("$[0].lastName").value("Carter"))
            .andExpect(jsonPath("$[0].specialties[0].name").value("radiology"));
    }

    @Test
    void listVets_withNameParam_searchesByName() throws Exception {
        when(vetService.searchByName("James")).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/api/vets").param("name", "James"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("James"));
    }

    @Test
    void listVets_withSpecialtyParam_filtersBySpecialty() throws Exception {
        when(vetService.findBySpecialtyName("radiology")).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/api/vets").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].specialties[0].name").value("radiology"));
    }

    @Test
    void getVet_existingId_returnsVet() throws Exception {
        when(vetService.findById(1)).thenReturn(sampleVet);

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void getVet_nonExistingId_returns404() throws Exception {
        when(vetService.findById(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/api/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void addVet_validRequest_returnsCreated() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.create(any(VetRequestDto.class))).thenReturn(sampleVet);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void addVet_invalidRequest_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("", "", null);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    void updateVet_validRequest_returnsUpdated() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.update(eq(1), any(VetRequestDto.class))).thenReturn(sampleVet);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void updateVet_nonExistingId_returns404() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.update(eq(999), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put("/api/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_existingId_returnsNoContent() throws Exception {
        doNothing().when(vetService).delete(1);

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteVet_nonExistingId_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Vet", 999)).when(vetService).delete(999);

        mockMvc.perform(delete("/api/vets/999"))
            .andExpect(status().isNotFound());
    }
}
