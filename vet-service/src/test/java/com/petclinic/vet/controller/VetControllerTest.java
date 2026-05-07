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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void listVets_shouldReturnAllVets() throws Exception {
        List<VetResponseDto> vets = List.of(
            new VetResponseDto(1, "James", "Carter",
                List.of(new SpecialtyResponseDto(1, "radiology")))
        );
        when(vetService.getAllVets()).thenReturn(vets);

        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("James"))
            .andExpect(jsonPath("$[0].lastName").value("Carter"))
            .andExpect(jsonPath("$[0].specialties[0].name").value("radiology"));
    }

    @Test
    void listVets_withSpecialtyId_shouldFilterBySpecialtyId() throws Exception {
        List<VetResponseDto> vets = List.of(
            new VetResponseDto(1, "James", "Carter",
                List.of(new SpecialtyResponseDto(1, "radiology")))
        );
        when(vetService.findBySpecialtyId(1)).thenReturn(vets);

        mockMvc.perform(get("/vets").param("specialtyId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void listVets_withSpecialtyName_shouldFilterBySpecialtyName() throws Exception {
        List<VetResponseDto> vets = List.of(
            new VetResponseDto(1, "James", "Carter",
                List.of(new SpecialtyResponseDto(1, "radiology")))
        );
        when(vetService.findBySpecialtyName("radiology")).thenReturn(vets);

        mockMvc.perform(get("/vets").param("specialtyName", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void listVets_withName_shouldSearchByName() throws Exception {
        List<VetResponseDto> vets = List.of(
            new VetResponseDto(1, "James", "Carter",
                List.of(new SpecialtyResponseDto(1, "radiology")))
        );
        when(vetService.searchByName("james")).thenReturn(vets);

        mockMvc.perform(get("/vets").param("name", "james"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getVet_shouldReturnVetById() throws Exception {
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.getVetById(1)).thenReturn(dto);

        mockMvc.perform(get("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"))
            .andExpect(jsonPath("$.lastName").value("Carter"));
    }

    @Test
    void getVet_shouldReturn404WhenNotFound() throws Exception {
        when(vetService.getVetById(99))
            .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/vets/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("ResourceNotFoundException"));
    }

    @Test
    void addVet_shouldCreateAndReturnVet() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        VetResponseDto response = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.createVet(any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void addVet_shouldReturn400ForMissingFirstName() throws Exception {
        VetRequestDto request = new VetRequestDto(null, "Carter", List.of());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_shouldReturn400ForMissingLastName() throws Exception {
        VetRequestDto request = new VetRequestDto("James", null, List.of());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_shouldReturn400ForNullSpecialties() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_shouldReturn400ForInvalidFirstNamePattern() throws Exception {
        VetRequestDto request = new VetRequestDto("123", "Carter", List.of());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_shouldReturn400ForTooLongFirstName() throws Exception {
        VetRequestDto request = new VetRequestDto("A".repeat(31), "Carter", List.of());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_shouldUpdateAndReturnVet() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        VetResponseDto response = new VetResponseDto(1, "Updated", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.updateVet(eq(1), any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    void updateVet_shouldReturn404WhenNotFound() throws Exception {
        VetRequestDto request = new VetRequestDto("Test", "Test", List.of());
        when(vetService.updateVet(eq(99), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(put("/vets/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_shouldDeleteAndReturnVet() throws Exception {
        VetResponseDto response = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.deleteVet(1)).thenReturn(response);

        mockMvc.perform(delete("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteVet_shouldReturn404WhenNotFound() throws Exception {
        when(vetService.deleteVet(99))
            .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(delete("/vets/99"))
            .andExpect(status().isNotFound());
    }
}
