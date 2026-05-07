package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.SpecialtyService;
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

@WebMvcTest(SpecialtyController.class)
@Import(GlobalExceptionHandler.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SpecialtyService specialtyService;

    private final SpecialtyResponseDto sampleSpecialty = new SpecialtyResponseDto(1, "radiology");

    @Test
    void listSpecialties_shouldReturnAll() throws Exception {
        when(specialtyService.listSpecialties()).thenReturn(List.of(sampleSpecialty));

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("radiology"));
    }

    @Test
    void getSpecialty_shouldReturnSpecialty() throws Exception {
        when(specialtyService.getSpecialty(1)).thenReturn(sampleSpecialty);

        mockMvc.perform(get("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void getSpecialty_shouldReturn404WhenNotFound() throws Exception {
        when(specialtyService.getSpecialty(999))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(get("/api/specialties/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Not Found"))
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void addSpecialty_shouldCreateSpecialty() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("oncology");
        SpecialtyResponseDto created = new SpecialtyResponseDto(4, "oncology");

        when(specialtyService.createSpecialty(any(SpecialtyRequestDto.class))).thenReturn(created);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(4))
            .andExpect(jsonPath("$.name").value("oncology"));
    }

    @Test
    void addSpecialty_shouldReturn400ForInvalidInput() throws Exception {
        String invalidJson = "{\"name\":\"\"}";

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    void updateSpecialty_shouldUpdateSpecialty() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        SpecialtyResponseDto updated = new SpecialtyResponseDto(1, "updated");

        when(specialtyService.updateSpecialty(eq(1), any(SpecialtyRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("updated"));
    }

    @Test
    void updateSpecialty_shouldReturn404WhenNotFound() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(specialtyService.updateSpecialty(eq(999), any(SpecialtyRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(put("/api/specialties/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_shouldDeleteSpecialty() throws Exception {
        when(specialtyService.deleteSpecialty(1)).thenReturn(sampleSpecialty);

        mockMvc.perform(delete("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void deleteSpecialty_shouldReturn404WhenNotFound() throws Exception {
        when(specialtyService.deleteSpecialty(999))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(delete("/api/specialties/999"))
            .andExpect(status().isNotFound());
    }
}
