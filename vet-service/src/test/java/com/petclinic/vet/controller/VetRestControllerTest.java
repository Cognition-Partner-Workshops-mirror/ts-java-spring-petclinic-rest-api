package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.service.VetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

@WebMvcTest(VetRestController.class)
@Import(GlobalExceptionHandler.class)
class VetRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VetService vetService;

    @MockitoBean
    private VetMapper vetMapper;

    private Vet sampleVet;
    private VetDto sampleVetDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        sampleVet = new Vet();
        sampleVet.setId(1);
        sampleVet.setFirstName("James");
        sampleVet.setLastName("Carter");
        sampleVet.setSpecialties(new HashSet<>(Set.of(radiology)));

        sampleVetDto = new VetDto(1, "James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));
    }

    @Test
    void listVetsReturnsOk() throws Exception {
        when(vetService.findAllVets()).thenReturn(List.of(sampleVet));
        when(vetMapper.toDtoList(any())).thenReturn(List.of(sampleVetDto));

        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("James")));
    }

    @Test
    void listVetsWithNameFilter() throws Exception {
        when(vetService.searchVetsByName("James")).thenReturn(List.of(sampleVet));
        when(vetMapper.toDtoList(any())).thenReturn(List.of(sampleVetDto));

        mockMvc.perform(get("/vets").param("name", "James"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVetsWithSpecialtyFilter() throws Exception {
        when(vetService.findVetsBySpecialty("radiology")).thenReturn(List.of(sampleVet));
        when(vetMapper.toDtoList(any())).thenReturn(List.of(sampleVetDto));

        mockMvc.perform(get("/vets").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getVetReturnsOk() throws Exception {
        when(vetService.findVetById(1)).thenReturn(sampleVet);
        when(vetMapper.toDto(sampleVet)).thenReturn(sampleVetDto);

        mockMvc.perform(get("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("James")))
            .andExpect(jsonPath("$.lastName", is("Carter")))
            .andExpect(jsonPath("$.specialties", hasSize(1)));
    }

    @Test
    void getVetReturnsNotFound() throws Exception {
        when(vetService.findVetById(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/vets/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("ResourceNotFoundException")));
    }

    @Test
    void addVetReturnsCreated() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));

        when(vetMapper.toEntity(any(VetRequestDto.class))).thenReturn(sampleVet);
        when(vetService.saveVet(any(Vet.class))).thenReturn(sampleVet);
        when(vetMapper.toDto(any(Vet.class))).thenReturn(sampleVetDto);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void addVetWithInvalidDataReturnsBadRequest() throws Exception {
        VetRequestDto request = new VetRequestDto("", "", null);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVetReturnsOk() throws Exception {
        when(vetMapper.toEntity(any(VetDto.class))).thenReturn(sampleVet);
        when(vetService.updateVet(eq(1), any(Vet.class))).thenReturn(sampleVet);
        when(vetMapper.toDto(any(Vet.class))).thenReturn(sampleVetDto);

        mockMvc.perform(put("/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleVetDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void updateVetReturnsNotFound() throws Exception {
        when(vetMapper.toEntity(any(VetDto.class))).thenReturn(sampleVet);
        when(vetService.updateVet(eq(99), any(Vet.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(put("/vets/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleVetDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVetReturnsNoContent() throws Exception {
        doNothing().when(vetService).deleteVet(1);

        mockMvc.perform(delete("/vets/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteVetReturnsNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Vet", 99)).when(vetService).deleteVet(99);

        mockMvc.perform(delete("/vets/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void addVetWithInvalidFirstNamePattern() throws Exception {
        VetRequestDto request = new VetRequestDto("123", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void listVetsWithBlankNameParamReturnsAll() throws Exception {
        when(vetService.findAllVets()).thenReturn(List.of(sampleVet));
        when(vetMapper.toDtoList(any())).thenReturn(List.of(sampleVetDto));

        mockMvc.perform(get("/vets").param("name", "  "))
            .andExpect(status().isOk());
    }
}
