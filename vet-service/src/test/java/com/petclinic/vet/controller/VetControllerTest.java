package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.dto.VetRequest;
import com.petclinic.vet.dto.VetResponse;
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

    private final VetResponse jamesResponse = new VetResponse(1, "James", "Carter",
        List.of(new SpecialtyResponse(1, "radiology")));

    @Test
    void listVets_returnsOk() throws Exception {
        when(vetService.listAll()).thenReturn(List.of(jamesResponse));

        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("James")));
    }

    @Test
    void listVets_filterByLastName_returnsFiltered() throws Exception {
        when(vetService.searchByLastName("Carter")).thenReturn(List.of(jamesResponse));

        mockMvc.perform(get("/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_filterBySpecialty_returnsFiltered() throws Exception {
        when(vetService.filterBySpecialty("radiology")).thenReturn(List.of(jamesResponse));

        mockMvc.perform(get("/vets").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_filterByBoth_returnsFiltered() throws Exception {
        when(vetService.searchByLastNameAndSpecialty("Carter", "radiology"))
            .thenReturn(List.of(jamesResponse));

        mockMvc.perform(get("/vets")
                .param("lastName", "Carter")
                .param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getVet_existingId_returnsOk() throws Exception {
        when(vetService.getById(1)).thenReturn(jamesResponse);

        mockMvc.perform(get("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("James")))
            .andExpect(jsonPath("$.specialties", hasSize(1)));
    }

    @Test
    void getVet_nonExistingId_returns404() throws Exception {
        when(vetService.getById(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Not Found")));
    }

    @Test
    void addVet_validRequest_returnsOk() throws Exception {
        VetRequest request = new VetRequest("James", "Carter",
            List.of(new SpecialtyResponse(1, "radiology")));
        when(vetService.create(any(VetRequest.class))).thenReturn(jamesResponse);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void addVet_invalidRequest_returns400() throws Exception {
        VetRequest request = new VetRequest("", "", null);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    void updateVet_existingId_returnsOk() throws Exception {
        VetRequest request = new VetRequest("Updated", "Name",
            List.of(new SpecialtyResponse(1, "radiology")));
        VetResponse updated = new VetResponse(1, "Updated", "Name",
            List.of(new SpecialtyResponse(1, "radiology")));
        when(vetService.update(eq(1), any(VetRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Updated")));
    }

    @Test
    void updateVet_nonExistingId_returns404() throws Exception {
        VetRequest request = new VetRequest("Updated", "Name", List.of());
        when(vetService.update(eq(999), any(VetRequest.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put("/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_existingId_returnsOk() throws Exception {
        when(vetService.delete(1)).thenReturn(jamesResponse);

        mockMvc.perform(delete("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteVet_nonExistingId_returns404() throws Exception {
        when(vetService.delete(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(delete("/vets/999"))
            .andExpect(status().isNotFound());
    }
}
