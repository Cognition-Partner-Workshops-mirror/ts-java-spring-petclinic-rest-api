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
import org.springframework.boot.test.mock.mockito.MockBean;
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
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VetService vetService;

    private VetResponseDto createVetResponse() {
        return new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void listVets_returnsAllVets() throws Exception {
        VetResponseDto vet1 = createVetResponse();
        VetResponseDto vet2 = new VetResponseDto(2, "Helen", "Leary", List.of());

        when(vetService.findAll()).thenReturn(List.of(vet1, vet2));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].firstName", is("James")))
            .andExpect(jsonPath("$[1].firstName", is("Helen")));
    }

    @Test
    void listVets_withNameParam_returnsFilteredVets() throws Exception {
        VetResponseDto vet = createVetResponse();
        when(vetService.searchByName("Carter")).thenReturn(List.of(vet));

        mockMvc.perform(get("/api/vets").param("name", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].lastName", is("Carter")));
    }

    @Test
    void listVets_withSpecialtyIdParam_returnsFilteredVets() throws Exception {
        VetResponseDto vet = createVetResponse();
        when(vetService.findBySpecialtyId(1)).thenReturn(List.of(vet));

        mockMvc.perform(get("/api/vets").param("specialtyId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].specialties[0].name", is("radiology")));
    }

    @Test
    void addVet_validRequest_returnsCreated() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyRequestDto("radiology")));
        VetResponseDto response = createVetResponse();

        when(vetService.create(any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")))
            .andExpect(jsonPath("$.lastName", is("Carter")))
            .andExpect(jsonPath("$.specialties", hasSize(1)));
    }

    @Test
    void addVet_invalidRequest_returnsBadRequest() throws Exception {
        VetRequestDto request = new VetRequestDto("", "", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.title", is("MethodArgumentNotValidException")));
    }

    @Test
    void addVet_firstNameTooLong_returnsBadRequest() throws Exception {
        VetRequestDto request = new VetRequestDto(
            "A".repeat(31), "Carter", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getVet_existingId_returnsVet() throws Exception {
        VetResponseDto response = createVetResponse();
        when(vetService.findById(1)).thenReturn(response);

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void getVet_nonExistingId_returnsNotFound() throws Exception {
        when(vetService.findById(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/api/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.detail").value("Vet not found with id: 999"));
    }

    @Test
    void updateVet_validRequest_returnsUpdated() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Updated", List.of());
        VetResponseDto response = new VetResponseDto(1, "James", "Updated", List.of());

        when(vetService.update(eq(1), any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lastName", is("Updated")));
    }

    @Test
    void updateVet_nonExistingId_returnsNotFound() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        when(vetService.update(eq(999), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put("/api/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateVet_invalidRequest_returnsBadRequest() throws Exception {
        VetRequestDto request = new VetRequestDto("", "", List.of());

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteVet_existingId_returnsDeleted() throws Exception {
        VetResponseDto response = createVetResponse();
        when(vetService.delete(1)).thenReturn(response);

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteVet_nonExistingId_returnsNotFound() throws Exception {
        when(vetService.delete(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(delete("/api/vets/999"))
            .andExpect(status().isNotFound());
    }
}
