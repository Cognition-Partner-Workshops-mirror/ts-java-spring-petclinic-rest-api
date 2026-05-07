package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

    @MockitoBean
    private VetService vetService;

    private final VetResponseDto carterDto = new VetResponseDto(1, "James", "Carter", List.of());
    private final VetResponseDto learyDto = new VetResponseDto(2, "Helen", "Leary",
        List.of(new SpecialtyResponseDto(1, "radiology")));

    @Test
    void listVets_returnsAll() throws Exception {
        when(vetService.findAll()).thenReturn(List.of(carterDto, learyDto));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].firstName", is("James")));
    }

    @Test
    void listVets_withLastNameFilter_returnsFiltered() throws Exception {
        when(vetService.searchByLastName("Carter")).thenReturn(List.of(carterDto));

        mockMvc.perform(get("/api/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].lastName", is("Carter")));
    }

    @Test
    void listVets_withSpecialtyIdFilter_returnsFiltered() throws Exception {
        when(vetService.findBySpecialtyId(1)).thenReturn(List.of(learyDto));

        mockMvc.perform(get("/api/vets").param("specialtyId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_withSpecialtyNameFilter_returnsFiltered() throws Exception {
        when(vetService.findBySpecialtyName("radiology")).thenReturn(List.of(learyDto));

        mockMvc.perform(get("/api/vets").param("specialtyName", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getVet_existingId_returnsVet() throws Exception {
        when(vetService.findById(1)).thenReturn(carterDto);

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")))
            .andExpect(jsonPath("$.lastName", is("Carter")));
    }

    @Test
    void getVet_nonExistingId_returns404() throws Exception {
        when(vetService.findById(999))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/api/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Vet not found")));
    }

    @Test
    void addVet_validRequest_returns201() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(1));
        when(vetService.create(any())).thenReturn(carterDto);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void addVet_blankFirstName_returns400() throws Exception {
        String json = """
            {"firstName": "", "lastName": "Carter", "specialtyIds": []}
            """;

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    void addVet_nullLastName_returns400() throws Exception {
        String json = """
            {"firstName": "James", "specialtyIds": []}
            """;

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_invalidPattern_returns400() throws Exception {
        String json = """
            {"firstName": "123", "lastName": "Carter", "specialtyIds": []}
            """;

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_nullSpecialtyIds_returns400() throws Exception {
        String json = """
            {"firstName": "James", "lastName": "Carter"}
            """;

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_validRequest_returnsUpdated() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of());
        VetResponseDto updated = new VetResponseDto(1, "Updated", "Carter", List.of());
        when(vetService.update(eq(1), any())).thenReturn(updated);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Updated")));
    }

    @Test
    void updateVet_nonExistingId_returns404() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Carter", List.of());
        when(vetService.update(eq(999), any()))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put("/api/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_existingId_returnsDeleted() throws Exception {
        when(vetService.delete(1)).thenReturn(carterDto);

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void deleteVet_nonExistingId_returns404() throws Exception {
        when(vetService.delete(999))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(delete("/api/vets/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getVet_withSpecialties_returnsSpecialtiesList() throws Exception {
        when(vetService.findById(2)).thenReturn(learyDto);

        mockMvc.perform(get("/api/vets/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.specialties", hasSize(1)))
            .andExpect(jsonPath("$.specialties[0].name", is("radiology")));
    }
}
