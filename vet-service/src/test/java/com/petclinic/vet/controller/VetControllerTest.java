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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
@Import({GlobalExceptionHandler.class, VetControllerTest.MockConfig.class})
class VetControllerTest {

    @TestConfiguration
    static class MockConfig {
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

    private static final String BASE_URL = "/petclinic/api/vets";

    private VetResponseDto sampleVet() {
        return new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void listVets_returnsOk() throws Exception {
        when(vetService.listVets()).thenReturn(List.of(sampleVet()));

        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("James"))
            .andExpect(jsonPath("$[0].specialties[0].name").value("radiology"));
    }

    @Test
    void listVets_withLastNameFilter() throws Exception {
        when(vetService.findByLastName("Carter")).thenReturn(List.of(sampleVet()));

        mockMvc.perform(get(BASE_URL).param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].lastName").value("Carter"));
    }

    @Test
    void listVets_withSpecialtyIdFilter() throws Exception {
        when(vetService.findBySpecialtyId(1)).thenReturn(List.of(sampleVet()));

        mockMvc.perform(get(BASE_URL).param("specialtyId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void listVets_withSpecialtyNameFilter() throws Exception {
        when(vetService.findBySpecialtyName("radiology")).thenReturn(List.of(sampleVet()));

        mockMvc.perform(get(BASE_URL).param("specialtyName", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getVet_found() throws Exception {
        when(vetService.getVet(1)).thenReturn(sampleVet());

        mockMvc.perform(get(BASE_URL + "/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void getVet_notFound() throws Exception {
        when(vetService.getVet(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get(BASE_URL + "/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("ResourceNotFoundException"));
    }

    @Test
    void addVet_validRequest() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyRequestDto("radiology")));
        when(vetService.addVet(any(VetRequestDto.class))).thenReturn(sampleVet());

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void addVet_invalidRequest_blankFirstName() throws Exception {
        VetRequestDto request = new VetRequestDto("", "Carter",
            List.of(new SpecialtyRequestDto("radiology")));

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("MethodArgumentNotValidException"));
    }

    @Test
    void addVet_invalidRequest_nullSpecialties() throws Exception {
        String json = "{\"firstName\":\"James\",\"lastName\":\"Carter\"}";

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_validRequest() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Updated",
            List.of(new SpecialtyRequestDto("surgery")));
        VetResponseDto updated = new VetResponseDto(1, "James", "Updated",
            List.of(new SpecialtyResponseDto(2, "surgery")));
        when(vetService.updateVet(eq(1), any(VetRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put(BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lastName").value("Updated"));
    }

    @Test
    void updateVet_notFound() throws Exception {
        VetRequestDto request = new VetRequestDto("X", "Y", List.of());
        when(vetService.updateVet(eq(999), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put(BASE_URL + "/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_found() throws Exception {
        when(vetService.deleteVet(1)).thenReturn(sampleVet());

        mockMvc.perform(delete(BASE_URL + "/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteVet_notFound() throws Exception {
        when(vetService.deleteVet(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(delete(BASE_URL + "/999"))
            .andExpect(status().isNotFound());
    }
}
