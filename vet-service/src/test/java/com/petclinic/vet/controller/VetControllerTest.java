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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    void listVets_noParams_returnsAll() throws Exception {
        when(vetService.listVets()).thenReturn(List.of(
            new VetResponseDto(1, "James", "Carter", List.of()),
            new VetResponseDto(2, "Helen", "Leary", List.of(new SpecialtyResponseDto(1, "radiology")))
        ));

        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].firstName").value("James"));
    }

    @Test
    void listVets_withSpecialtyFilter_returnsFiltered() throws Exception {
        when(vetService.findBySpecialty("radiology")).thenReturn(List.of(
            new VetResponseDto(2, "Helen", "Leary", List.of(new SpecialtyResponseDto(1, "radiology")))
        ));

        mockMvc.perform(get("/vets").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].firstName").value("Helen"));
    }

    @Test
    void listVets_withLastNameFilter_returnsFiltered() throws Exception {
        when(vetService.searchByLastName("Carter")).thenReturn(List.of(
            new VetResponseDto(1, "James", "Carter", List.of())
        ));

        mockMvc.perform(get("/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].lastName").value("Carter"));
    }

    @Test
    void listVets_withBlankSpecialty_returnsAll() throws Exception {
        when(vetService.listVets()).thenReturn(List.of(
            new VetResponseDto(1, "James", "Carter", List.of())
        ));

        mockMvc.perform(get("/vets").param("specialty", "  "))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getVet_existingId_returnsOk() throws Exception {
        when(vetService.getVet(1)).thenReturn(
            new VetResponseDto(1, "James", "Carter", List.of()));

        mockMvc.perform(get("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"))
            .andExpect(jsonPath("$.lastName").value("Carter"))
            .andExpect(jsonPath("$.specialties").isArray());
    }

    @Test
    void getVet_nonExistingId_returns404() throws Exception {
        when(vetService.getVet(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void addVet_validInput_returnsOk() throws Exception {
        VetRequestDto request = new VetRequestDto("John", "Doe",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        VetResponseDto response = new VetResponseDto(10, "John", "Doe",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        when(vetService.createVet(any())).thenReturn(response);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.specialties[0].name").value("radiology"));
    }

    @Test
    void addVet_emptyFirstName_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("", "Doe", List.of());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_nullLastName_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("John", null, List.of());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_nullSpecialties_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("John", "Doe", null);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_validInput_returnsOk() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of());
        when(vetService.updateVet(eq(1), any())).thenReturn(
            new VetResponseDto(1, "Updated", "Carter", List.of()));

        mockMvc.perform(put("/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    void updateVet_nonExistingId_returns404() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of());
        when(vetService.updateVet(eq(999), any()))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put("/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_existingId_returnsOk() throws Exception {
        when(vetService.deleteVet(1)).thenReturn(
            new VetResponseDto(1, "James", "Carter", List.of()));

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
