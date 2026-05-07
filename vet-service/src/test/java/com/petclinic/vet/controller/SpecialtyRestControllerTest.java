package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.service.VetService;
import org.junit.jupiter.api.BeforeEach;
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
    private VetService vetService;

    @MockitoBean
    private SpecialtyMapper specialtyMapper;

    private Specialty sampleSpecialty;
    private SpecialtyDto sampleSpecialtyDto;

    @BeforeEach
    void setUp() {
        sampleSpecialty = new Specialty();
        sampleSpecialty.setId(1);
        sampleSpecialty.setName("radiology");

        sampleSpecialtyDto = new SpecialtyDto(1, "radiology");
    }

    @Test
    void listSpecialtiesReturnsOk() throws Exception {
        when(vetService.findAllSpecialties()).thenReturn(List.of(sampleSpecialty));
        when(specialtyMapper.toDtoList(any())).thenReturn(List.of(sampleSpecialtyDto));

        mockMvc.perform(get("/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void getSpecialtyReturnsOk() throws Exception {
        when(vetService.findSpecialtyById(1)).thenReturn(sampleSpecialty);
        when(specialtyMapper.toDto(sampleSpecialty)).thenReturn(sampleSpecialtyDto);

        mockMvc.perform(get("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void getSpecialtyReturnsNotFound() throws Exception {
        when(vetService.findSpecialtyById(99))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(get("/specialties/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("ResourceNotFoundException")));
    }

    @Test
    void addSpecialtyReturnsCreated() throws Exception {
        SpecialtyDto request = new SpecialtyDto(null, "oncology");

        when(specialtyMapper.toEntity(any(SpecialtyDto.class))).thenReturn(sampleSpecialty);
        when(vetService.saveSpecialty(any(Specialty.class))).thenReturn(sampleSpecialty);
        when(specialtyMapper.toDto(any(Specialty.class))).thenReturn(sampleSpecialtyDto);

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void addSpecialtyWithBlankNameReturnsBadRequest() throws Exception {
        SpecialtyDto request = new SpecialtyDto(null, "");

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialtyReturnsOk() throws Exception {
        when(specialtyMapper.toEntity(any(SpecialtyDto.class))).thenReturn(sampleSpecialty);
        when(vetService.updateSpecialty(eq(1), any(Specialty.class))).thenReturn(sampleSpecialty);
        when(specialtyMapper.toDto(any(Specialty.class))).thenReturn(sampleSpecialtyDto);

        mockMvc.perform(put("/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleSpecialtyDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void updateSpecialtyReturnsNotFound() throws Exception {
        when(specialtyMapper.toEntity(any(SpecialtyDto.class))).thenReturn(sampleSpecialty);
        when(vetService.updateSpecialty(eq(99), any(Specialty.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(put("/specialties/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleSpecialtyDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialtyReturnsNoContent() throws Exception {
        doNothing().when(vetService).deleteSpecialty(1);

        mockMvc.perform(delete("/specialties/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteSpecialtyReturnsNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Specialty", 99))
            .when(vetService).deleteSpecialty(99);

        mockMvc.perform(delete("/specialties/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void addSpecialtyWithNameTooLong() throws Exception {
        SpecialtyDto request = new SpecialtyDto(null, "a".repeat(81));

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}
