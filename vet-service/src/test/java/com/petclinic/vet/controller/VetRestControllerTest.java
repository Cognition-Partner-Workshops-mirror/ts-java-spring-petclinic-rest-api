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

@WebMvcTest(VetRestController.class)
@Import(GlobalExceptionHandler.class)
class VetRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VetService vetService;

    @Test
    void listVets_returnsAll() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findAll()).thenReturn(List.of(vet));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("James")));
    }

    @Test
    void listVets_filterBySpecialty() throws Exception {
        SpecialtyResponseDto spec = new SpecialtyResponseDto(1, "radiology");
        VetResponseDto vet = new VetResponseDto(2, "Helen", "Leary", List.of(spec));
        when(vetService.findBySpecialtyName("radiology")).thenReturn(List.of(vet));

        mockMvc.perform(get("/api/vets").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("Helen")));
    }

    @Test
    void listVets_filterByLastName() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.searchByLastName("Carter")).thenReturn(List.of(vet));

        mockMvc.perform(get("/api/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].lastName", is("Carter")));
    }

    @Test
    void listVets_emptyResult() throws Exception {
        when(vetService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getVet_found() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findById(1)).thenReturn(vet);

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void getVet_notFound() throws Exception {
        when(vetService.findById(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/api/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("ResourceNotFoundException")));
    }

    @Test
    void addVet_success() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        VetResponseDto response = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.create(any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void addVet_validationError_blankFirstName() throws Exception {
        VetRequestDto request = new VetRequestDto("", "Carter", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("MethodArgumentNotValidException")));
    }

    @Test
    void addVet_validationError_blankLastName() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_validationError_nameTooLong() throws Exception {
        String longName = "A".repeat(31);
        VetRequestDto request = new VetRequestDto(longName, "Carter", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_success() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of());
        VetResponseDto response = new VetResponseDto(1, "Updated", "Carter", List.of());
        when(vetService.update(eq(1), any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Updated")));
    }

    @Test
    void updateVet_notFound() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of());
        when(vetService.update(eq(999), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put("/api/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateVet_validationError() throws Exception {
        VetRequestDto request = new VetRequestDto("", "", List.of());

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteVet_success() throws Exception {
        doNothing().when(vetService).delete(1);

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteVet_notFound() throws Exception {
        doThrow(new ResourceNotFoundException("Vet", 999)).when(vetService).delete(999);

        mockMvc.perform(delete("/api/vets/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void addVet_withSpecialties() throws Exception {
        SpecialtyResponseDto spec = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(spec));
        VetResponseDto response = new VetResponseDto(2, "Helen", "Leary", List.of(spec));
        when(vetService.create(any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.specialties", hasSize(1)))
            .andExpect(jsonPath("$.specialties[0].name", is("radiology")));
    }

    @Test
    void listVets_blankSpecialtyParam_fallsBackToAll() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findAll()).thenReturn(List.of(vet));

        mockMvc.perform(get("/api/vets").param("specialty", ""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_blankLastNameParam_fallsBackToAll() throws Exception {
        VetResponseDto vet = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findAll()).thenReturn(List.of(vet));

        mockMvc.perform(get("/api/vets").param("lastName", "   "))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }
}
