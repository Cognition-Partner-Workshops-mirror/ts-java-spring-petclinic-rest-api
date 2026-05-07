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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpecialtyRestController.class)
@Import(GlobalExceptionHandler.class)
class SpecialtyRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SpecialtyService specialtyService;

    @Test
    void listSpecialties_returnsAll() throws Exception {
        SpecialtyResponseDto spec = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.findAll()).thenReturn(List.of(spec));

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void listSpecialties_withNameFilter_returnsFiltered() throws Exception {
        SpecialtyResponseDto spec = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.searchByName("radio")).thenReturn(List.of(spec));

        mockMvc.perform(get("/api/specialties").param("name", "radio"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listSpecialties_emptyNameFilter_returnsAll() throws Exception {
        SpecialtyResponseDto spec = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.findAll()).thenReturn(List.of(spec));

        mockMvc.perform(get("/api/specialties").param("name", ""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getSpecialty_existingId_returnsSpecialty() throws Exception {
        SpecialtyResponseDto spec = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.findById(1)).thenReturn(spec);

        mockMvc.perform(get("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void getSpecialty_nonExistingId_returns404() throws Exception {
        when(specialtyService.findById(999)).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(get("/api/specialties/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("ResourceNotFoundException")));
    }

    @Test
    void addSpecialty_validRequest_returns201() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("oncology");
        SpecialtyResponseDto response = new SpecialtyResponseDto(4, "oncology");
        when(specialtyService.create(any(SpecialtyRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id", is(4)))
            .andExpect(jsonPath("$.name", is("oncology")));
    }

    @Test
    void addSpecialty_emptyName_returns400() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addSpecialty_nullName_returns400() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addSpecialty_nameTooLong_returns400() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("a".repeat(81));

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialty_validRequest_returns200() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        SpecialtyResponseDto response = new SpecialtyResponseDto(1, "updated");
        when(specialtyService.update(eq(1), any(SpecialtyRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("updated")));
    }

    @Test
    void updateSpecialty_nonExistingId_returns404() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(specialtyService.update(eq(999), any(SpecialtyRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(put("/api/specialties/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateSpecialty_invalidRequest_returns400() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSpecialty_existingId_returns204() throws Exception {
        doNothing().when(specialtyService).delete(1);

        mockMvc.perform(delete("/api/specialties/1"))
            .andExpect(status().isNoContent());

        verify(specialtyService).delete(1);
    }

    @Test
    void deleteSpecialty_nonExistingId_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Specialty", 999)).when(specialtyService).delete(999);

        mockMvc.perform(delete("/api/specialties/999"))
            .andExpect(status().isNotFound());
    }
}
