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
 * Tests HTTP request/response mapping, validation, and error handling.
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

    private final SpecialtyResponseDto radiologyDto = new SpecialtyResponseDto(1, "radiology");

    @Test
    void listVets_returnsAllVets() throws Exception {
        List<VetResponseDto> vets = List.of(
            new VetResponseDto(1, "James", "Carter", List.of(radiologyDto)),
            new VetResponseDto(2, "Helen", "Leary", List.of())
        );
        when(vetService.getAllVets()).thenReturn(vets);

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].firstName", is("James")))
            .andExpect(jsonPath("$[1].firstName", is("Helen")));
    }

    @Test
    void listVets_withLastNameFilter_returnsFiltered() throws Exception {
        List<VetResponseDto> vets = List.of(
            new VetResponseDto(1, "James", "Carter", List.of())
        );
        when(vetService.searchByLastName("Carter")).thenReturn(vets);

        mockMvc.perform(get("/api/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].lastName", is("Carter")));
    }

    @Test
    void listVets_withSpecialtyFilter_returnsFiltered() throws Exception {
        List<VetResponseDto> vets = List.of(
            new VetResponseDto(1, "James", "Carter", List.of(radiologyDto))
        );
        when(vetService.findBySpecialty("radiology")).thenReturn(vets);

        mockMvc.perform(get("/api/vets").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getVet_existingId_returnsVet() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", List.of(radiologyDto));
        when(vetService.getVetById(1)).thenReturn(vet);

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")))
            .andExpect(jsonPath("$.lastName", is("Carter")))
            .andExpect(jsonPath("$.specialties", hasSize(1)));
    }

    @Test
    void getVet_nonExistingId_returns404() throws Exception {
        when(vetService.getVetById(999))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/api/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Resource Not Found")));
    }

    @Test
    void addVet_validRequest_returnsCreatedVet() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(radiologyDto));
        VetResponseDto response = new VetResponseDto(2, "Helen", "Leary", List.of(radiologyDto));
        when(vetService.createVet(any())).thenReturn(response);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(2)))
            .andExpect(jsonPath("$.firstName", is("Helen")));
    }

    @Test
    void addVet_blankFirstName_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("", "Leary", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Validation Error")));
    }

    @Test
    void addVet_blankLastName_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_nullSpecialties_returns400() throws Exception {
        // JSON with missing specialties field to trigger @NotNull validation
        String json = "{\"firstName\": \"Helen\", \"lastName\": \"Leary\"}";

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_invalidFirstNamePattern_returns400() throws Exception {
        // Numeric characters should fail the pattern validation
        VetRequestDto request = new VetRequestDto("James123", "Carter", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_validRequest_returnsUpdatedVet() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Updated", List.of());
        VetResponseDto response = new VetResponseDto(1, "James", "Updated", List.of());
        when(vetService.updateVet(eq(1), any())).thenReturn(response);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lastName", is("Updated")));
    }

    @Test
    void updateVet_nonExistingId_returns404() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        when(vetService.updateVet(eq(999), any()))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put("/api/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_existingId_returnsDeletedVet() throws Exception {
        VetResponseDto response = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.deleteVet(1)).thenReturn(response);

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteVet_nonExistingId_returns404() throws Exception {
        when(vetService.deleteVet(999))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(delete("/api/vets/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void addVet_firstNameTooLong_returns400() throws Exception {
        // First name exceeds the 30-character max
        String longName = "a".repeat(31);
        VetRequestDto request = new VetRequestDto(longName, "Carter", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}
