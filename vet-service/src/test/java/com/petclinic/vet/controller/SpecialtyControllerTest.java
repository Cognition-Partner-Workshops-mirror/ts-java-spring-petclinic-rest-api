package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.exception.DuplicateResourceException;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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

    @MockBean
    private SpecialtyService specialtyService;

    @Test
    void listSpecialties_returnsOk() throws Exception {
        when(specialtyService.findAll()).thenReturn(List.of(
            new SpecialtyResponseDto(1, "radiology"),
            new SpecialtyResponseDto(2, "surgery")
        ));

        mockMvc.perform(get("/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void getSpecialty_existingId_returnsOk() throws Exception {
        when(specialtyService.findById(1)).thenReturn(new SpecialtyResponseDto(1, "radiology"));

        mockMvc.perform(get("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void getSpecialty_nonExistingId_returns404() throws Exception {
        when(specialtyService.findById(99)).thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(get("/specialties/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Specialty Not Found")));
    }

    @Test
    void addSpecialty_validRequest_returnsCreated() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");
        when(specialtyService.create(any())).thenReturn(new SpecialtyResponseDto(3, "dentistry"));

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name", is("dentistry")));
    }

    @Test
    void addSpecialty_blankName_returnsBadRequest() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    void addSpecialty_nullName_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": null}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addSpecialty_duplicateName_returnsConflict() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        when(specialtyService.create(any())).thenThrow(
            new DuplicateResourceException("Specialty", "name", "radiology"));

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.title", is("Duplicate Specialty")));
    }

    @Test
    void updateSpecialty_validRequest_returnsOk() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(specialtyService.update(eq(1), any())).thenReturn(new SpecialtyResponseDto(1, "updated"));

        mockMvc.perform(put("/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("updated")));
    }

    @Test
    void updateSpecialty_nonExistingId_returns404() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(specialtyService.update(eq(99), any())).thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(put("/specialties/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_existingId_returnsOk() throws Exception {
        when(specialtyService.delete(1)).thenReturn(new SpecialtyResponseDto(1, "radiology"));

        mockMvc.perform(delete("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteSpecialty_nonExistingId_returns404() throws Exception {
        when(specialtyService.delete(99)).thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(delete("/specialties/99"))
            .andExpect(status().isNotFound());
    }
}
