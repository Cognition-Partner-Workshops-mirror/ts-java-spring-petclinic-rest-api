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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

@WebMvcTest(VetController.class)
@Import(GlobalExceptionHandler.class)
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VetService vetService;

    @MockBean
    private VetMapper vetMapper;

    private Vet james;
    private Vet helen;
    private VetResponseDto jamesDto;
    private VetResponseDto helenDto;

    @BeforeEach
    void setUp() {
        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        james = new Vet();
        james.setId(1);
        james.setFirstName("James");
        james.setLastName("Carter");

        helen = new Vet();
        helen.setId(2);
        helen.setFirstName("Helen");
        helen.setLastName("Leary");
        helen.getSpecialties().add(radiology);

        jamesDto = new VetResponseDto(1, "James", "Carter", Collections.emptyList());
        helenDto = new VetResponseDto(2, "Helen", "Leary",
            Arrays.asList(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void listVets_returns200WithArray() throws Exception {
        when(vetService.findAll()).thenReturn(Arrays.asList(james, helen));
        when(vetMapper.toResponseDtos(any(List.class))).thenReturn(Arrays.asList(jamesDto, helenDto));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("James"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].specialties[0].name").value("radiology"));
    }

    @Test
    void getVet_existingId_returns200() throws Exception {
        when(vetService.findById(1)).thenReturn(james);
        when(vetMapper.toResponseDto(james)).thenReturn(jamesDto);

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"))
            .andExpect(jsonPath("$.lastName").value("Carter"));
    }

    @Test
    void getVet_nonExistingId_returns404() throws Exception {
        when(vetService.findById(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/api/vets/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void addVet_validBody_returns201WithLocation() throws Exception {
        VetRequestDto requestDto = new VetRequestDto("New", "Vet", Arrays.asList(1));
        Vet created = new Vet();
        created.setId(3);
        created.setFirstName("New");
        created.setLastName("Vet");
        VetResponseDto responseDto = new VetResponseDto(3, "New", "Vet",
            Arrays.asList(new SpecialtyResponseDto(1, "radiology")));

        when(vetService.create(any(VetRequestDto.class))).thenReturn(created);
        when(vetMapper.toResponseDto(created)).thenReturn(responseDto);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.firstName").value("New"));
    }

    @Test
    void addVet_invalidBody_returns400() throws Exception {
        VetRequestDto requestDto = new VetRequestDto("", "", null);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.schemaValidationErrors").isArray());
    }

    @Test
    void updateVet_existingId_returns200() throws Exception {
        VetRequestDto requestDto = new VetRequestDto("Updated", "Carter", Collections.emptyList());
        Vet updated = new Vet();
        updated.setId(1);
        updated.setFirstName("Updated");
        updated.setLastName("Carter");
        VetResponseDto responseDto = new VetResponseDto(1, "Updated", "Carter", Collections.emptyList());

        when(vetService.update(eq(1), any(VetRequestDto.class))).thenReturn(updated);
        when(vetMapper.toResponseDto(updated)).thenReturn(responseDto);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    void updateVet_nonExistingId_returns404() throws Exception {
        VetRequestDto requestDto = new VetRequestDto("Updated", "Name", Collections.emptyList());
        when(vetService.update(eq(99), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(put("/api/vets/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deleteVet_existingId_returns204() throws Exception {
        doNothing().when(vetService).delete(1);

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteVet_nonExistingId_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Vet", 99)).when(vetService).delete(99);

        mockMvc.perform(delete("/api/vets/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }
}
