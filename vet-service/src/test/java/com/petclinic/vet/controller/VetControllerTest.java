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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for VetController using @WebMvcTest.
 * Tests REST endpoint mappings, request validation, and error handling.
 */
@WebMvcTest(VetController.class)
@Import(GlobalExceptionHandler.class)
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VetService vetService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listVets_shouldReturnAll() throws Exception {
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("James"))
            .andExpect(jsonPath("$[0].lastName").value("Carter"));
    }

    @Test
    void listVets_withSpecialtyFilter_shouldFilterBySpecialty() throws Exception {
        SpecialtyResponseDto spec = new SpecialtyResponseDto(1, "radiology");
        VetResponseDto dto = new VetResponseDto(2, "Helen", "Leary", List.of(spec));
        when(vetService.findBySpecialty("radiology")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/vets").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("Helen"))
            .andExpect(jsonPath("$[0].specialties[0].name").value("radiology"));
    }

    @Test
    void listVets_withNameSearch_shouldSearchByName() throws Exception {
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.searchByName("jam")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/vets").param("name", "jam"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("James"));
    }

    @Test
    void listVets_withBlankSpecialtyFilter_shouldReturnAll() throws Exception {
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/vets").param("specialty", "  "))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("James"));
    }

    @Test
    void listVets_withBlankNameFilter_shouldReturnAll() throws Exception {
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/vets").param("name", "  "))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("James"));
    }

    @Test
    void getVet_shouldReturnVet() throws Exception {
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findById(1)).thenReturn(dto);

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void getVet_notFound_shouldReturn404() throws Exception {
        when(vetService.findById(99)).thenThrow(new ResourceNotFoundException("Vet not found with id: 99"));

        mockMvc.perform(get("/api/vets/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("ResourceNotFoundException"))
            .andExpect(jsonPath("$.detail").value("Vet not found with id: 99"));
    }

    @Test
    void addVet_shouldCreateAndReturn201() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of());
        VetResponseDto response = new VetResponseDto(2, "Helen", "Leary", List.of());
        when(vetService.create(any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.firstName").value("Helen"));
    }

    @Test
    void addVet_withSpecialties_shouldCreateAndReturn201() throws Exception {
        SpecialtyResponseDto spec = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(spec));
        VetResponseDto response = new VetResponseDto(2, "Helen", "Leary", List.of(spec));
        when(vetService.create(any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.specialties[0].name").value("radiology"));
    }

    @Test
    void addVet_withEmptyFirstName_shouldReturn400() throws Exception {
        VetRequestDto request = new VetRequestDto("", "Carter", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_withEmptyLastName_shouldReturn400() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_withNullFirstName_shouldReturn400() throws Exception {
        VetRequestDto request = new VetRequestDto(null, "Carter", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_shouldUpdateAndReturn200() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Updated", List.of());
        VetResponseDto response = new VetResponseDto(1, "James", "Updated", List.of());
        when(vetService.update(eq(1), any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lastName").value("Updated"));
    }

    @Test
    void updateVet_notFound_shouldReturn404() throws Exception {
        VetRequestDto request = new VetRequestDto("Test", "Test", List.of());
        when(vetService.update(eq(99), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet not found with id: 99"));

        mockMvc.perform(put("/api/vets/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateVet_withInvalidInput_shouldReturn400() throws Exception {
        VetRequestDto request = new VetRequestDto("", "", List.of());

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteVet_shouldReturn204() throws Exception {
        doNothing().when(vetService).delete(1);

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteVet_notFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Vet not found with id: 99"))
            .when(vetService).delete(99);

        mockMvc.perform(delete("/api/vets/99"))
            .andExpect(status().isNotFound());
    }
}
