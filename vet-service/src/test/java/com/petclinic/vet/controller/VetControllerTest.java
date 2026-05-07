package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VetController.class)
@Import(GlobalExceptionHandler.class)
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VetService vetService;

    @Autowired
    private ObjectMapper objectMapper;

    private final VetResponseDto sampleVet = new VetResponseDto(
        1, "James", "Carter", List.of(new SpecialtyDto(1, "radiology")));

    @Test
    void listVets_returnsAll() throws Exception {
        when(vetService.findAll()).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("James")));
    }

    @Test
    void listVets_filterBySpecialty() throws Exception {
        when(vetService.findBySpecialty("radiology")).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/api/vets").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_searchByName() throws Exception {
        when(vetService.searchByName("james")).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/api/vets").param("name", "james"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getVet_returnsVet() throws Exception {
        when(vetService.findById(1)).thenReturn(sampleVet);

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("James")))
            .andExpect(jsonPath("$.lastName", is("Carter")));
    }

    @Test
    void getVet_notFound() throws Exception {
        when(vetService.findById(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/api/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("ResourceNotFoundException")));
    }

    @Test
    void addVet_createsAndReturns201() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));
        when(vetService.create(any(VetRequestDto.class))).thenReturn(sampleVet);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/vets/1"))
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void addVet_invalidRequest_returns400() throws Exception {
        VetRequestDto invalid = new VetRequestDto("", "", null);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("MethodArgumentNotValidException")));
    }

    @Test
    void updateVet_updatesAndReturns200() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));
        when(vetService.update(eq(1), any(VetRequestDto.class))).thenReturn(sampleVet);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void updateVet_notFound() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);
        when(vetService.update(eq(999), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put("/api/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_returns204() throws Exception {
        doNothing().when(vetService).delete(1);

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteVet_notFound() throws Exception {
        doThrow(new ResourceNotFoundException("Vet", 999)).when(vetService).delete(999);

        mockMvc.perform(delete("/api/vets/999"))
            .andExpect(status().isNotFound());
    }
}
