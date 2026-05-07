package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.service.VetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VetControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private VetService vetService;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetController vetController;

    private Vet james;
    private Vet helen;
    private VetResponseDto jamesDto;
    private VetResponseDto helenDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vetController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();

        james = new Vet("James", "Carter");
        james.setId(1);

        helen = new Vet("Helen", "Leary");
        helen.setId(2);

        Specialty radiology = new Specialty("radiology");
        radiology.setId(1);
        helen.addSpecialty(radiology);

        jamesDto = new VetResponseDto(1, "James", "Carter", new ArrayList<>());
        helenDto = new VetResponseDto(2, "Helen", "Leary",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void listVets_returnsOk() throws Exception {
        given(vetService.findAll()).willReturn(List.of(james, helen));
        given(vetMapper.toResponseDtos(any())).willReturn(List.of(jamesDto, helenDto));

        mockMvc.perform(get("/api/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("James"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].firstName").value("Helen"));
    }

    @Test
    void getVet_existingId_returnsOk() throws Exception {
        given(vetService.findById(1)).willReturn(james);
        given(vetMapper.toResponseDto(james)).willReturn(jamesDto);

        mockMvc.perform(get("/api/vets/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"))
            .andExpect(jsonPath("$.lastName").value("Carter"));
    }

    @Test
    void getVet_nonExistingId_returnsNotFound() throws Exception {
        given(vetService.findById(999)).willThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/api/vets/999").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void addVet_validRequest_returnsCreated() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter", new ArrayList<>());
        Vet created = new Vet("James", "Carter");
        created.setId(1);
        VetResponseDto responseDto = new VetResponseDto(1, "James", "Carter", new ArrayList<>());

        given(vetService.create(any(VetRequestDto.class))).willReturn(created);
        given(vetMapper.toResponseDto(created)).willReturn(responseDto);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void addVet_invalidRequest_returnsBadRequest() throws Exception {
        VetRequestDto request = new VetRequestDto();
        request.setFirstName("");
        request.setLastName("");

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_nullFirstName_returnsBadRequest() throws Exception {
        VetRequestDto request = new VetRequestDto();
        request.setLastName("Carter");

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_existingId_returnsOk() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", new ArrayList<>());
        Vet updated = new Vet("Updated", "Carter");
        updated.setId(1);
        VetResponseDto responseDto = new VetResponseDto(1, "Updated", "Carter", new ArrayList<>());

        given(vetService.update(eq(1), any(VetRequestDto.class))).willReturn(updated);
        given(vetMapper.toResponseDto(updated)).willReturn(responseDto);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    void updateVet_nonExistingId_returnsNotFound() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", new ArrayList<>());

        given(vetService.update(eq(999), any(VetRequestDto.class)))
            .willThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put("/api/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateVet_invalidRequest_returnsBadRequest() throws Exception {
        VetRequestDto request = new VetRequestDto();
        request.setFirstName("");
        request.setLastName("");

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteVet_existingId_returnsNoContent() throws Exception {
        doNothing().when(vetService).delete(1);

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteVet_nonExistingId_returnsNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Vet", 999)).when(vetService).delete(999);

        mockMvc.perform(delete("/api/vets/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void searchVets_returnsMatchingVets() throws Exception {
        given(vetService.searchByName("James")).willReturn(List.of(james));
        given(vetMapper.toResponseDtos(any())).willReturn(List.of(jamesDto));

        mockMvc.perform(get("/api/vets/search").param("name", "James")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("James"));
    }

    @Test
    void findBySpecialty_returnsMatchingVets() throws Exception {
        given(vetService.findBySpecialty("radiology")).willReturn(List.of(helen));
        given(vetMapper.toResponseDtos(any())).willReturn(List.of(helenDto));

        mockMvc.perform(get("/api/vets/specialty/radiology")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("Helen"));
    }

    @Test
    void assignSpecialty_validIds_returnsOk() throws Exception {
        james.addSpecialty(new Specialty("radiology"));
        given(vetService.assignSpecialty(1, 1)).willReturn(james);
        given(vetMapper.toResponseDto(james)).willReturn(
            new VetResponseDto(1, "James", "Carter", List.of(new SpecialtyResponseDto(1, "radiology"))));

        mockMvc.perform(put("/api/vets/1/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.specialties[0].name").value("radiology"));
    }

    @Test
    void removeSpecialty_validIds_returnsNoContent() throws Exception {
        given(vetService.removeSpecialty(1, 1)).willReturn(james);

        mockMvc.perform(delete("/api/vets/1/specialties/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void listVets_emptyList_returnsOk() throws Exception {
        given(vetService.findAll()).willReturn(new ArrayList<>());
        given(vetMapper.toResponseDtos(any())).willReturn(new ArrayList<>());

        mockMvc.perform(get("/api/vets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }
}
