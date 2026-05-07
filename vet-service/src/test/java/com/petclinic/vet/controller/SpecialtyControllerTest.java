package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.request.SpecialtyRequest;
import com.petclinic.vet.dto.response.SpecialtyResponse;
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

    private final SpecialtyResponse radiologyResponse = new SpecialtyResponse(1, "radiology");

    @Test
    void listSpecialties_returnsAll() throws Exception {
        SpecialtyResponse surgery = new SpecialtyResponse(2, "surgery");
        when(specialtyService.listSpecialties()).thenReturn(List.of(radiologyResponse, surgery));

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name", is("radiology")))
            .andExpect(jsonPath("$[1].name", is("surgery")));
    }

    @Test
    void listSpecialties_filterByName() throws Exception {
        when(specialtyService.searchByName("rad")).thenReturn(List.of(radiologyResponse));

        mockMvc.perform(get("/api/specialties").param("name", "rad"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void getSpecialty_found() throws Exception {
        when(specialtyService.getSpecialty(1)).thenReturn(radiologyResponse);

        mockMvc.perform(get("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void getSpecialty_notFound() throws Exception {
        when(specialtyService.getSpecialty(99))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(get("/api/specialties/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Resource Not Found")));
    }

    @Test
    void addSpecialty_success() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("cardiology");
        SpecialtyResponse response = new SpecialtyResponse(4, "cardiology");
        when(specialtyService.createSpecialty(any(SpecialtyRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(4)))
            .andExpect(jsonPath("$.name", is("cardiology")));
    }

    @Test
    void addSpecialty_validationError_blankName() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("");

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Bad Request")))
            .andExpect(jsonPath("$.schemaValidationErrors").isArray());
    }

    @Test
    void addSpecialty_duplicate() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("radiology");
        when(specialtyService.createSpecialty(any(SpecialtyRequest.class)))
            .thenThrow(new DuplicateResourceException("Specialty with name 'radiology' already exists"));

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Duplicate Resource")));
    }

    @Test
    void updateSpecialty_success() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("updated-radiology");
        SpecialtyResponse response = new SpecialtyResponse(1, "updated-radiology");
        when(specialtyService.updateSpecialty(eq(1), any(SpecialtyRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("updated-radiology")));
    }

    @Test
    void updateSpecialty_notFound() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("updated");
        when(specialtyService.updateSpecialty(eq(99), any(SpecialtyRequest.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(put("/api/specialties/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_success() throws Exception {
        when(specialtyService.deleteSpecialty(1)).thenReturn(radiologyResponse);

        mockMvc.perform(delete("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteSpecialty_notFound() throws Exception {
        when(specialtyService.deleteSpecialty(99))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(delete("/api/specialties/99"))
            .andExpect(status().isNotFound());
    }
}
