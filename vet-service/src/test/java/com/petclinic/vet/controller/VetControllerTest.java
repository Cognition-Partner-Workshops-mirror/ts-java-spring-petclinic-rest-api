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

/**
 * Integration tests for VetController using @WebMvcTest.
 * Verifies REST mappings, request validation, error handling, and filtering.
 */
@WebMvcTest(VetController.class)
@Import(GlobalExceptionHandler.class)
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VetService vetService;

    private VetResponseDto createJamesDto() {
        return new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void listVets_noFilter_returnsAll() throws Exception {
        when(vetService.listVets()).thenReturn(List.of(
            createJamesDto(),
            new VetResponseDto(2, "Helen", "Leary", List.of())));

        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].firstName", is("James")));
    }

    @Test
    void listVets_filterByLastName_returnsFiltered() throws Exception {
        when(vetService.searchByLastName("Cart")).thenReturn(List.of(createJamesDto()));

        mockMvc.perform(get("/vets").param("lastName", "Cart"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].lastName", is("Carter")));
    }

    @Test
    void listVets_filterBySpecialtyId_returnsFiltered() throws Exception {
        when(vetService.filterBySpecialtyId(1)).thenReturn(List.of(createJamesDto()));

        mockMvc.perform(get("/vets").param("specialtyId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_filterBySpecialtyName_returnsFiltered() throws Exception {
        when(vetService.filterBySpecialtyName("radio")).thenReturn(List.of(createJamesDto()));

        mockMvc.perform(get("/vets").param("specialtyName", "radio"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getVet_existingId_returnsOk() throws Exception {
        when(vetService.getVet(1)).thenReturn(createJamesDto());

        mockMvc.perform(get("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")))
            .andExpect(jsonPath("$.lastName", is("Carter")))
            .andExpect(jsonPath("$.specialties", hasSize(1)));
    }

    @Test
    void getVet_nonExistingId_returns404() throws Exception {
        when(vetService.getVet(999))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Resource Not Found")))
            .andExpect(jsonPath("$.detail").value("Vet not found with id: 999"));
    }

    @Test
    void addVet_validRequest_returnsOk() throws Exception {
        VetRequestDto request = new VetRequestDto("New", "Vet", List.of(1));
        when(vetService.addVet(any(VetRequestDto.class)))
            .thenReturn(new VetResponseDto(10, "New", "Vet",
                List.of(new SpecialtyResponseDto(1, "radiology"))));

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(10)))
            .andExpect(jsonPath("$.firstName", is("New")));
    }

    @Test
    void addVet_blankFirstName_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("", "Vet", List.of());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Bad Request")))
            .andExpect(jsonPath("$.schemaValidationErrors").isArray());
    }

    @Test
    void addVet_blankLastName_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("Valid", "", List.of());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_nullSpecialtyIds_returns400() throws Exception {
        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"Valid\",\"lastName\":\"Vet\",\"specialtyIds\":null}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_invalidSpecialtyId_returns404() throws Exception {
        VetRequestDto request = new VetRequestDto("New", "Vet", List.of(999));
        when(vetService.addVet(any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateVet_validRequest_returnsOk() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of(1));
        when(vetService.updateVet(eq(1), any(VetRequestDto.class)))
            .thenReturn(new VetResponseDto(1, "Updated", "Carter",
                List.of(new SpecialtyResponseDto(1, "radiology"))));

        mockMvc.perform(put("/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Updated")));
    }

    @Test
    void updateVet_nonExistingId_returns404() throws Exception {
        VetRequestDto request = new VetRequestDto("X", "Y", List.of());
        when(vetService.updateVet(eq(999), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put("/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_existingId_returnsOk() throws Exception {
        when(vetService.deleteVet(1)).thenReturn(createJamesDto());

        mockMvc.perform(delete("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteVet_nonExistingId_returns404() throws Exception {
        when(vetService.deleteVet(999))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(delete("/vets/999"))
            .andExpect(status().isNotFound());
    }
}
