package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.SpecialtyService;
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

@WebMvcTest(SpecialtyController.class)
@Import(GlobalExceptionHandler.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecialtyService specialtyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listSpecialties_shouldReturnAllSpecialties() throws Exception {
        List<SpecialtyResponseDto> specialties = List.of(
            new SpecialtyResponseDto(1, "radiology"),
            new SpecialtyResponseDto(2, "surgery")
        );
        when(specialtyService.getAllSpecialties()).thenReturn(specialties);

        mockMvc.perform(get("/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("radiology"));
    }

    @Test
    void listSpecialties_withNameParam_shouldSearchByName() throws Exception {
        List<SpecialtyResponseDto> specialties = List.of(
            new SpecialtyResponseDto(1, "radiology")
        );
        when(specialtyService.searchByName("radio")).thenReturn(specialties);

        mockMvc.perform(get("/specialties").param("name", "radio"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("radiology"));
    }

    @Test
    void getSpecialty_shouldReturnSpecialtyById() throws Exception {
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.getSpecialtyById(1)).thenReturn(dto);

        mockMvc.perform(get("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void getSpecialty_shouldReturn404WhenNotFound() throws Exception {
        when(specialtyService.getSpecialtyById(99))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(get("/specialties/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("ResourceNotFoundException"));
    }

    @Test
    void addSpecialty_shouldCreateAndReturnSpecialty() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        SpecialtyResponseDto response = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.createSpecialty(any(SpecialtyRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void addSpecialty_shouldReturn400ForInvalidRequest() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("MethodArgumentNotValidException"));
    }

    @Test
    void addSpecialty_shouldReturn400ForNullName() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto(null);

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialty_shouldUpdateAndReturnSpecialty() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated radiology");
        SpecialtyResponseDto response = new SpecialtyResponseDto(1, "updated radiology");
        when(specialtyService.updateSpecialty(eq(1), any(SpecialtyRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("updated radiology"));
    }

    @Test
    void updateSpecialty_shouldReturn404WhenNotFound() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("test");
        when(specialtyService.updateSpecialty(eq(99), any(SpecialtyRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(put("/specialties/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_shouldDeleteAndReturnSpecialty() throws Exception {
        SpecialtyResponseDto response = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.deleteSpecialty(1)).thenReturn(response);

        mockMvc.perform(delete("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteSpecialty_shouldReturn404WhenNotFound() throws Exception {
        when(specialtyService.deleteSpecialty(99))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(delete("/specialties/99"))
            .andExpect(status().isNotFound());
    }
}
