package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VetRestController.class)
class VetRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VetService vetService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listVets_shouldReturnAllVets() throws Exception {
        List<VetResponseDto> vets = List.of(
                new VetResponseDto(1, "James", "Carter", new ArrayList<>()),
                new VetResponseDto(2, "Helen", "Leary", List.of(new SpecialtyResponseDto(1, "radiology")))
        );
        when(vetService.findAll()).thenReturn(vets);

        mockMvc.perform(get("/vets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("James")))
                .andExpect(jsonPath("$[1].specialties", hasSize(1)));
    }

    @Test
    void listVets_shouldFilterBySpecialty() throws Exception {
        List<VetResponseDto> vets = List.of(
                new VetResponseDto(2, "Helen", "Leary", List.of(new SpecialtyResponseDto(1, "radiology")))
        );
        when(vetService.findBySpecialty("radiology")).thenReturn(vets);

        mockMvc.perform(get("/vets").param("specialty", "radiology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("Helen")));
    }

    @Test
    void listVets_shouldSearchByName() throws Exception {
        List<VetResponseDto> vets = List.of(
                new VetResponseDto(1, "James", "Carter", new ArrayList<>())
        );
        when(vetService.searchByName("James")).thenReturn(vets);

        mockMvc.perform(get("/vets").param("name", "James"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_shouldReturnEmptyList() throws Exception {
        when(vetService.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/vets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getVet_shouldReturnVet() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", new ArrayList<>());
        when(vetService.findById(1)).thenReturn(vet);

        mockMvc.perform(get("/vets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("James")))
                .andExpect(jsonPath("$.lastName", is("Carter")));
    }

    @Test
    void getVet_shouldReturn404WhenNotFound() throws Exception {
        when(vetService.findById(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/vets/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Not Found")));
    }

    @Test
    void addVet_shouldCreateAndReturnVet() throws Exception {
        VetRequestDto request = new VetRequestDto("New", "Vet", new ArrayList<>());
        VetResponseDto created = new VetResponseDto(7, "New", "Vet", new ArrayList<>());
        when(vetService.create(any(VetRequestDto.class))).thenReturn(created);

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(7)))
                .andExpect(jsonPath("$.firstName", is("New")))
                .andExpect(header().exists("Location"));
    }

    @Test
    void addVet_shouldReturn400ForInvalidRequest() throws Exception {
        VetRequestDto request = new VetRequestDto("", "", new ArrayList<>());

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    void addVet_shouldReturn400ForNullFields() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_shouldUpdateAndReturnVet() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Name", new ArrayList<>());
        VetResponseDto updated = new VetResponseDto(1, "Updated", "Name", new ArrayList<>());
        when(vetService.update(eq(1), any(VetRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put("/vets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Updated")));
    }

    @Test
    void updateVet_shouldReturn404WhenNotFound() throws Exception {
        VetRequestDto request = new VetRequestDto("Test", "Vet", new ArrayList<>());
        when(vetService.update(eq(999), any(VetRequestDto.class)))
                .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put("/vets/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateVet_shouldReturn400ForInvalidRequest() throws Exception {
        VetRequestDto request = new VetRequestDto("", "", new ArrayList<>());

        mockMvc.perform(put("/vets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteVet_shouldDeleteAndReturnVet() throws Exception {
        VetResponseDto deleted = new VetResponseDto(1, "James", "Carter", new ArrayList<>());
        when(vetService.delete(1)).thenReturn(deleted);

        mockMvc.perform(delete("/vets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteVet_shouldReturn404WhenNotFound() throws Exception {
        when(vetService.delete(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(delete("/vets/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addVet_shouldCreateVetWithSpecialties() throws Exception {
        List<SpecialtyResponseDto> specs = List.of(new SpecialtyResponseDto(1, "radiology"));
        VetRequestDto request = new VetRequestDto("Helen", "Leary", specs);
        VetResponseDto created = new VetResponseDto(2, "Helen", "Leary", specs);
        when(vetService.create(any(VetRequestDto.class))).thenReturn(created);

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.specialties", hasSize(1)))
                .andExpect(jsonPath("$.specialties[0].name", is("radiology")));
    }
}
