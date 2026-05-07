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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VetRestController.class)
@Import(GlobalExceptionHandler.class)
class VetRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VetService vetService;

    @Test
    void listVets_returnsAllVets() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findAll()).thenReturn(List.of(vet));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("James")));
    }

    @Test
    void listVets_withSpecialtyFilter_returnsFilteredVets() throws Exception {
        VetResponseDto vet = new VetResponseDto(2, "Helen", "Leary",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.findBySpecialty("radiology")).thenReturn(List.of(vet));

        mockMvc.perform(get("/api/vets").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("Helen")));
    }

    @Test
    void listVets_withLastNameFilter_returnsFilteredVets() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.searchByLastName("Carter")).thenReturn(List.of(vet));

        mockMvc.perform(get("/api/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].lastName", is("Carter")));
    }

    @Test
    void getVet_existingId_returnsVet() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findById(1)).thenReturn(vet);

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void getVet_nonExistingId_returns404() throws Exception {
        when(vetService.findById(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/api/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("ResourceNotFoundException")));
    }

    @Test
    void addVet_validRequest_returns201() throws Exception {
        VetRequestDto request = new VetRequestDto("John", "Doe", new ArrayList<>());
        VetResponseDto response = new VetResponseDto(7, "John", "Doe", List.of());
        when(vetService.create(any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id", is(7)))
            .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    void addVet_invalidRequest_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("", "", new ArrayList<>());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").exists());
    }

    @Test
    void addVet_missingFirstName_returns400() throws Exception {
        String json = """
            {"lastName": "Doe", "specialties": []}
            """;

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_invalidNamePattern_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("John123", "Doe", new ArrayList<>());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_validRequest_returns200() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Name", new ArrayList<>());
        VetResponseDto response = new VetResponseDto(1, "Updated", "Name", List.of());
        when(vetService.update(eq(1), any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Updated")));
    }

    @Test
    void updateVet_nonExistingId_returns404() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Name", new ArrayList<>());
        when(vetService.update(eq(999), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put("/api/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateVet_invalidRequest_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("", "", new ArrayList<>());

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteVet_existingId_returns204() throws Exception {
        doNothing().when(vetService).delete(1);

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isNoContent());

        verify(vetService).delete(1);
    }

    @Test
    void deleteVet_nonExistingId_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Vet", 999)).when(vetService).delete(999);

        mockMvc.perform(delete("/api/vets/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void listVets_emptySpecialtyParam_returnsAll() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findAll()).thenReturn(List.of(vet));

        mockMvc.perform(get("/api/vets").param("specialty", ""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_emptyLastNameParam_returnsAll() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findAll()).thenReturn(List.of(vet));

        mockMvc.perform(get("/api/vets").param("lastName", "  "))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void addVet_withSpecialties_returns201() throws Exception {
        SpecialtyResponseDto specDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto request = new VetRequestDto("Jane", "Smith", List.of(specDto));
        VetResponseDto response = new VetResponseDto(8, "Jane", "Smith", List.of(specDto));
        when(vetService.create(any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.specialties", hasSize(1)))
            .andExpect(jsonPath("$.specialties[0].name", is("radiology")));
    }
}
