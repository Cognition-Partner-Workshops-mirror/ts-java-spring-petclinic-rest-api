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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    private final SpecialtyResponseDto responseDto = new SpecialtyResponseDto(1, "radiology");

    @Test
    void listSpecialties_returns200() throws Exception {
        when(specialtyService.listSpecialties()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("radiology"));
    }

    @Test
    void getSpecialty_returns200() throws Exception {
        when(specialtyService.getSpecialty(1)).thenReturn(responseDto);

        mockMvc.perform(get("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void getSpecialty_returns404() throws Exception {
        when(specialtyService.getSpecialty(999)).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(get("/specialties/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("ResourceNotFoundException"));
    }

    @Test
    void addSpecialty_returns200() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        when(specialtyService.addSpecialty(any())).thenReturn(responseDto);

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void addSpecialty_returns400_invalidInput() throws Exception {
        SpecialtyRequestDto invalid = new SpecialtyRequestDto("");

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("MethodArgumentNotValidException"));
    }

    @Test
    void updateSpecialty_returns200() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        SpecialtyResponseDto updated = new SpecialtyResponseDto(1, "surgery");
        when(specialtyService.updateSpecialty(eq(1), any())).thenReturn(updated);

        mockMvc.perform(put("/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("surgery"));
    }

    @Test
    void updateSpecialty_returns404() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        when(specialtyService.updateSpecialty(eq(999), any())).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(put("/specialties/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_returns200() throws Exception {
        when(specialtyService.deleteSpecialty(1)).thenReturn(responseDto);

        mockMvc.perform(delete("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteSpecialty_returns404() throws Exception {
        when(specialtyService.deleteSpecialty(999)).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(delete("/specialties/999"))
            .andExpect(status().isNotFound());
    }
}
