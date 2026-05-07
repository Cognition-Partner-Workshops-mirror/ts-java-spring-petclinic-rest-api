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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    void listVets_returnsAllVets() throws Exception {
        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        VetResponseDto vetDto = new VetResponseDto(1, "James", "Carter", List.of(specialtyDto));
        when(vetService.listAll()).thenReturn(List.of(vetDto));

        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].firstName", is("James")))
            .andExpect(jsonPath("$[0].lastName", is("Carter")))
            .andExpect(jsonPath("$[0].specialties", hasSize(1)));
    }

    @Test
    void listVets_filterBySpecialtyId() throws Exception {
        VetResponseDto vetDto = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findBySpecialtyId(1)).thenReturn(List.of(vetDto));

        mockMvc.perform(get("/vets").param("specialtyId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_filterBySpecialtyName() throws Exception {
        VetResponseDto vetDto = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.findBySpecialtyName("radiology")).thenReturn(List.of(vetDto));

        mockMvc.perform(get("/vets").param("specialtyName", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_searchByName() throws Exception {
        VetResponseDto vetDto = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.searchByName("James")).thenReturn(List.of(vetDto));

        mockMvc.perform(get("/vets").param("name", "James"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getVet_existingId_returnsVet() throws Exception {
        VetResponseDto vetDto = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.getById(1)).thenReturn(vetDto);

        mockMvc.perform(get("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void getVet_nonExistingId_returns404() throws Exception {
        when(vetService.getById(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Not Found")))
            .andExpect(jsonPath("$.detail", is("Vet not found with id: 999")));
    }

    @Test
    void addVet_validRequest_returns201() throws Exception {
        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(specialtyDto));
        VetResponseDto response = new VetResponseDto(1, "James", "Carter", List.of(specialtyDto));
        when(vetService.create(any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void addVet_invalidRequest_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("", "", null);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    void addVet_missingFirstName_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto(null, "Carter", List.of());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_validRequest_returns200() throws Exception {
        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(specialtyDto));
        VetResponseDto response = new VetResponseDto(1, "James", "Carter", List.of(specialtyDto));
        when(vetService.update(eq(1), any(VetRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void updateVet_nonExistingId_returns404() throws Exception {
        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(specialtyDto));
        when(vetService.update(eq(999), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put("/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_existingId_returns200() throws Exception {
        VetResponseDto response = new VetResponseDto(1, "James", "Carter", List.of());
        when(vetService.delete(1)).thenReturn(response);

        mockMvc.perform(delete("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteVet_nonExistingId_returns404() throws Exception {
        when(vetService.delete(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(delete("/vets/999"))
            .andExpect(status().isNotFound());
    }
}
