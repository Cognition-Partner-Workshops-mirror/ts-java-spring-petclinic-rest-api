package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.dto.VetRequest;
import com.petclinic.vet.dto.VetResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VetController.class)
@Import(GlobalExceptionHandler.class)
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VetService service;

    private final VetResponse sampleVet = new VetResponse(1, "James", "Carter",
        List.of(new SpecialtyResponse(1, "radiology")));

    @Test
    void listVets_noParams_returnsAll() throws Exception {
        when(service.findAll()).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("James")));
    }

    @Test
    void listVets_filterByLastName() throws Exception {
        when(service.findByLastName("Carter")).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_filterBySpecialty() throws Exception {
        when(service.findBySpecialtyName("radiology")).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/vets").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_blankLastName_returnsAll() throws Exception {
        when(service.findAll()).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/vets").param("lastName", "  "))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getVet_found_returnsOk() throws Exception {
        when(service.findById(1)).thenReturn(sampleVet);

        mockMvc.perform(get("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")))
            .andExpect(jsonPath("$.specialties", hasSize(1)));
    }

    @Test
    void getVet_notFound_returns404() throws Exception {
        when(service.findById(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/vets/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Vet not found")));
    }

    @Test
    void addVet_valid_returnsOk() throws Exception {
        VetRequest request = new VetRequest("James", "Carter",
            List.of(new SpecialtyRequest("radiology")));
        when(service.create(any(VetRequest.class))).thenReturn(sampleVet);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void addVet_invalidFirstName_returns400() throws Exception {
        String body = """
            {"firstName": "", "lastName": "Carter", "specialties": []}
            """;

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    void addVet_nullSpecialties_returns400() throws Exception {
        String body = """
            {"firstName": "James", "lastName": "Carter"}
            """;

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_valid_returnsOk() throws Exception {
        VetRequest request = new VetRequest("James", "Updated",
            List.of(new SpecialtyRequest("surgery")));
        VetResponse updated = new VetResponse(1, "James", "Updated",
            List.of(new SpecialtyResponse(2, "surgery")));
        when(service.update(eq(1), any(VetRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lastName", is("Updated")));
    }

    @Test
    void updateVet_notFound_returns404() throws Exception {
        VetRequest request = new VetRequest("James", "Carter",
            List.of(new SpecialtyRequest("radiology")));
        when(service.update(eq(99), any(VetRequest.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(put("/vets/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_found_returnsOk() throws Exception {
        when(service.delete(1)).thenReturn(sampleVet);

        mockMvc.perform(delete("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteVet_notFound_returns404() throws Exception {
        when(service.delete(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(delete("/vets/99"))
            .andExpect(status().isNotFound());
    }
}
