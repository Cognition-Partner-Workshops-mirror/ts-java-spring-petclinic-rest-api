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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link VetController} using @WebMvcTest.
 * Mocks the service layer to test controller behavior in isolation.
 */
@WebMvcTest(VetController.class)
@Import(GlobalExceptionHandler.class)
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VetService vetService;

    private VetResponseDto createJamesResponse() {
        return new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void listVets_shouldReturnAllVets() throws Exception {
        when(vetService.findAll()).thenReturn(List.of(
            createJamesResponse(),
            new VetResponseDto(2, "Helen", "Leary", List.of())));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].firstName", is("James")))
            .andExpect(jsonPath("$[1].firstName", is("Helen")));
    }

    @Test
    void listVets_shouldReturnEmptyList() throws Exception {
        when(vetService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void listVets_shouldFilterByLastName() throws Exception {
        when(vetService.searchByLastName("Cart")).thenReturn(List.of(createJamesResponse()));

        mockMvc.perform(get("/api/vets").param("lastName", "Cart"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].lastName", is("Carter")));
    }

    @Test
    void listVets_shouldFilterBySpecialty() throws Exception {
        when(vetService.findBySpecialtyName("radiology")).thenReturn(List.of(createJamesResponse()));

        mockMvc.perform(get("/api/vets").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getVet_shouldReturnVet_whenExists() throws Exception {
        when(vetService.findById(1)).thenReturn(createJamesResponse());

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")))
            .andExpect(jsonPath("$.lastName", is("Carter")))
            .andExpect(jsonPath("$.specialties", hasSize(1)))
            .andExpect(jsonPath("$.specialties[0].name", is("radiology")));
    }

    @Test
    void getVet_shouldReturn404_whenNotFound() throws Exception {
        when(vetService.findById(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/api/vets/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("ResourceNotFoundException")));
    }

    @Test
    void addVet_shouldCreateAndReturn201() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(1));
        when(vetService.create(any())).thenReturn(createJamesResponse());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void addVet_shouldReturn400_whenFirstNameBlank() throws Exception {
        VetRequestDto request = new VetRequestDto("", "Carter", null);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("MethodArgumentNotValidException")));
    }

    @Test
    void addVet_shouldReturn400_whenLastNameBlank() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "", null);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_shouldReturn400_whenFirstNameNull() throws Exception {
        VetRequestDto request = new VetRequestDto(null, "Carter", null);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_shouldReturn400_whenFirstNameTooLong() throws Exception {
        VetRequestDto request = new VetRequestDto("A".repeat(31), "Carter", null);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_shouldReturn404_whenSpecialtyNotFound() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(999));
        when(vetService.create(any())).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateVet_shouldReturnUpdated() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Updated", List.of(1));
        VetResponseDto updated = new VetResponseDto(1, "James", "Updated",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        when(vetService.update(eq(1), any())).thenReturn(updated);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lastName", is("Updated")));
    }

    @Test
    void updateVet_shouldReturn404_whenNotFound() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);
        when(vetService.update(eq(99), any())).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(put("/api/vets/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateVet_shouldReturn400_whenInvalid() throws Exception {
        VetRequestDto request = new VetRequestDto("", "", null);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteVet_shouldReturn204() throws Exception {
        doNothing().when(vetService).delete(1);

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteVet_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Vet", 99)).when(vetService).delete(99);

        mockMvc.perform(delete("/api/vets/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void addVet_shouldReturn400_whenFirstNameHasInvalidChars() throws Exception {
        VetRequestDto request = new VetRequestDto("James123", "Carter", null);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_shouldCreateWithoutSpecialties() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);
        VetResponseDto response = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.specialties", hasSize(0)));
    }
}
