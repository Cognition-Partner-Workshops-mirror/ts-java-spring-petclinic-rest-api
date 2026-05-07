package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VetService vetService;

    private final VetResponseDto sampleVet = new VetResponseDto(1, "James", "Carter",
        List.of(new SpecialtyResponseDto(1, "radiology")));

    @Test
    void listVets_noParams_returnsAll() throws Exception {
        when(vetService.listVets()).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("James"));
    }

    @Test
    void listVets_withSpecialtyNameParam_returnsFiltered() throws Exception {
        when(vetService.findBySpecialtyName("radiology")).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/vets").param("specialtyName", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void listVets_withNameParam_returnsFiltered() throws Exception {
        when(vetService.searchByName("James")).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/vets").param("name", "James"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getVet_existingId_returnsOk() throws Exception {
        when(vetService.getVet(1)).thenReturn(sampleVet);

        mockMvc.perform(get("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"))
            .andExpect(jsonPath("$.lastName").value("Carter"))
            .andExpect(jsonPath("$.specialties[0].name").value("radiology"));
    }

    @Test
    void getVet_nonExistingId_returns404() throws Exception {
        when(vetService.getVet(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Not Found"))
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void addVet_validRequest_returnsOk() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.createVet(any())).thenReturn(sampleVet);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void addVet_invalidRequest_missingFirstName_returns400() throws Exception {
        String invalidJson = "{\"lastName\": \"Carter\", \"specialties\": []}";

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Bad Request"))
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void addVet_invalidRequest_missingLastName_returns400() throws Exception {
        String invalidJson = "{\"firstName\": \"James\", \"specialties\": []}";

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_invalidRequest_missingSpecialties_returns400() throws Exception {
        String invalidJson = "{\"firstName\": \"James\", \"lastName\": \"Carter\"}";

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_existingId_returnsOk() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Vet",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        VetResponseDto updated = new VetResponseDto(1, "Updated", "Vet",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.updateVet(eq(1), any())).thenReturn(updated);

        mockMvc.perform(put("/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    void updateVet_nonExistingId_returns404() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Vet", List.of());
        when(vetService.updateVet(eq(999), any()))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put("/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_existingId_returnsOk() throws Exception {
        when(vetService.deleteVet(1)).thenReturn(sampleVet);

        mockMvc.perform(delete("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteVet_nonExistingId_returns404() throws Exception {
        when(vetService.deleteVet(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(delete("/vets/999"))
            .andExpect(status().isNotFound());
    }
}
