package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VetController.class)
@Import({GlobalExceptionHandler.class, VetControllerTest.Config.class})
class VetControllerTest {

    @TestConfiguration
    static class Config {
        @Bean
        public VetService vetService() {
            return mock(VetService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VetService vetService;

    private VetDto sampleVet() {
        return new VetDto(1, "James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));
    }

    @Test
    void listVets_returnsAll() throws Exception {
        when(vetService.findAll()).thenReturn(List.of(sampleVet()));

        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("James"))
            .andExpect(jsonPath("$[0].specialties[0].name").value("radiology"));
    }

    @Test
    void listVets_filterByLastName() throws Exception {
        when(vetService.findByLastName("Carter")).thenReturn(List.of(sampleVet()));

        mockMvc.perform(get("/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].lastName").value("Carter"));
    }

    @Test
    void listVets_filterBySpecialtyName() throws Exception {
        when(vetService.findBySpecialtyName("radiology")).thenReturn(List.of(sampleVet()));

        mockMvc.perform(get("/vets").param("specialtyName", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("James"));
    }

    @Test
    void listVets_filterBySpecialtyId() throws Exception {
        when(vetService.findBySpecialtyId(1)).thenReturn(List.of(sampleVet()));

        mockMvc.perform(get("/vets").param("specialtyId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("James"));
    }

    @Test
    void getVet_returnsVet() throws Exception {
        when(vetService.findById(1)).thenReturn(sampleVet());

        mockMvc.perform(get("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void getVet_notFound() throws Exception {
        when(vetService.findById(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void addVet_createsVet() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));

        when(vetService.create(any())).thenReturn(sampleVet());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void addVet_validationError() throws Exception {
        VetRequestDto request = new VetRequestDto("", "", null);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    void updateVet_updatesVet() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyDto(1, "radiology")));
        VetDto updated = new VetDto(1, "Helen", "Leary",
            List.of(new SpecialtyDto(1, "radiology")));

        when(vetService.update(eq(1), any())).thenReturn(updated);

        mockMvc.perform(put("/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Helen"));
    }

    @Test
    void updateVet_notFound() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of());

        when(vetService.update(eq(999), any())).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put("/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_deletesVet() throws Exception {
        when(vetService.delete(1)).thenReturn(sampleVet());

        mockMvc.perform(delete("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void deleteVet_notFound() throws Exception {
        when(vetService.delete(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(delete("/vets/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getVet_badRequest_invalidId() throws Exception {
        mockMvc.perform(get("/vets/abc"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Bad Request"));
    }
}
