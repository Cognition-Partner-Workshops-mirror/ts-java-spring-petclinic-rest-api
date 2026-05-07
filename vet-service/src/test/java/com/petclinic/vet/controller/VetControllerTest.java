package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
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

    private VetDto sampleVet() {
        return new VetDto(1, "James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));
    }

    @Test
    void listVets_noParams_returnsAll() throws Exception {
        when(vetService.findAll()).thenReturn(List.of(sampleVet()));

        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("James")));
    }

    @Test
    void listVets_withLastName_filtersResults() throws Exception {
        when(vetService.findByLastName("Carter")).thenReturn(List.of(sampleVet()));

        mockMvc.perform(get("/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_withSpecialty_filtersResults() throws Exception {
        when(vetService.findBySpecialtyName("radiology")).thenReturn(List.of(sampleVet()));

        mockMvc.perform(get("/vets").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_withBothParams_filtersResults() throws Exception {
        when(vetService.findByLastNameAndSpecialtyName("Carter", "radiology"))
            .thenReturn(List.of(sampleVet()));

        mockMvc.perform(get("/vets")
                .param("lastName", "Carter")
                .param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getVet_existing_returnsOk() throws Exception {
        when(vetService.findById(1)).thenReturn(sampleVet());

        mockMvc.perform(get("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")))
            .andExpect(jsonPath("$.lastName", is("Carter")))
            .andExpect(jsonPath("$.specialties", hasSize(1)));
    }

    @Test
    void getVet_nonExisting_returns404() throws Exception {
        when(vetService.findById(999))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void addVet_validBody_returnsOk() throws Exception {
        VetDto input = new VetDto(null, "James", "Carter", List.of());
        when(vetService.create(any())).thenReturn(sampleVet());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void addVet_missingFirstName_returns400() throws Exception {
        String body = "{\"lastName\": \"Carter\", \"specialties\": []}";

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    void addVet_missingLastName_returns400() throws Exception {
        String body = "{\"firstName\": \"James\", \"specialties\": []}";

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_invalidFirstNamePattern_returns400() throws Exception {
        String body = "{\"firstName\": \"123\", \"lastName\": \"Carter\", \"specialties\": []}";

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_firstNameTooLong_returns400() throws Exception {
        String longName = "A".repeat(31);
        String body = "{\"firstName\": \"" + longName + "\", \"lastName\": \"Carter\", \"specialties\": []}";

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_nullSpecialties_returns400() throws Exception {
        String body = "{\"firstName\": \"James\", \"lastName\": \"Carter\"}";

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_validBody_returnsOk() throws Exception {
        VetDto updated = new VetDto(1, "Helen", "Leary", List.of());
        when(vetService.update(eq(1), any())).thenReturn(updated);

        mockMvc.perform(put("/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Helen")));
    }

    @Test
    void updateVet_nonExisting_returns404() throws Exception {
        when(vetService.update(eq(999), any()))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        VetDto input = new VetDto(999, "James", "Carter", List.of());

        mockMvc.perform(put("/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_existing_returnsOk() throws Exception {
        when(vetService.delete(1)).thenReturn(sampleVet());

        mockMvc.perform(delete("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void deleteVet_nonExisting_returns404() throws Exception {
        when(vetService.delete(999))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(delete("/vets/999"))
            .andExpect(status().isNotFound());
    }
}
