package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.SpecialtyService;
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

@WebMvcTest(SpecialtyController.class)
@Import({GlobalExceptionHandler.class, SpecialtyControllerTest.Config.class})
class SpecialtyControllerTest {

    @TestConfiguration
    static class Config {
        @Bean
        public SpecialtyService specialtyService() {
            return mock(SpecialtyService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpecialtyService specialtyService;

    @Test
    void listSpecialties_returnsAll() throws Exception {
        when(specialtyService.findAll()).thenReturn(
            List.of(new SpecialtyDto(1, "radiology"), new SpecialtyDto(2, "surgery")));

        mockMvc.perform(get("/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("radiology"))
            .andExpect(jsonPath("$[1].name").value("surgery"));
    }

    @Test
    void getSpecialty_returnsSpecialty() throws Exception {
        when(specialtyService.findById(1)).thenReturn(new SpecialtyDto(1, "radiology"));

        mockMvc.perform(get("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void getSpecialty_notFound() throws Exception {
        when(specialtyService.findById(999)).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(get("/specialties/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void addSpecialty_createsSpecialty() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");
        when(specialtyService.create(any())).thenReturn(new SpecialtyDto(3, "dentistry"));

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.name").value("dentistry"));
    }

    @Test
    void addSpecialty_validationError() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    void updateSpecialty_updatesSpecialty() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(specialtyService.update(eq(1), any())).thenReturn(new SpecialtyDto(1, "updated"));

        mockMvc.perform(put("/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("updated"));
    }

    @Test
    void updateSpecialty_notFound() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(specialtyService.update(eq(999), any())).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(put("/specialties/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_deletesSpecialty() throws Exception {
        when(specialtyService.delete(1)).thenReturn(new SpecialtyDto(1, "radiology"));

        mockMvc.perform(delete("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void deleteSpecialty_notFound() throws Exception {
        when(specialtyService.delete(999)).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(delete("/specialties/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getSpecialty_badRequest_invalidId() throws Exception {
        mockMvc.perform(get("/specialties/abc"))
            .andExpect(status().isBadRequest());
    }
}
