package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.SpecialtyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    @MockitoBean
    private SpecialtyService specialtyService;

    private final SpecialtyResponse radiologyResponse = new SpecialtyResponse(1, "radiology");

    @Test
    void listSpecialties_returnsOk() throws Exception {
        when(specialtyService.listAll()).thenReturn(List.of(radiologyResponse));

        mockMvc.perform(get("/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void getSpecialty_existingId_returnsOk() throws Exception {
        when(specialtyService.getById(1)).thenReturn(radiologyResponse);

        mockMvc.perform(get("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void getSpecialty_nonExistingId_returns404() throws Exception {
        when(specialtyService.getById(999))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(get("/specialties/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Not Found")));
    }

    @Test
    void addSpecialty_validRequest_returnsOk() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("oncology");
        SpecialtyResponse response = new SpecialtyResponse(4, "oncology");
        when(specialtyService.create(any(SpecialtyRequest.class))).thenReturn(response);

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("oncology")));
    }

    @Test
    void addSpecialty_invalidRequest_returns400() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("");

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    void updateSpecialty_existingId_returnsOk() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("updated");
        SpecialtyResponse response = new SpecialtyResponse(1, "updated");
        when(specialtyService.update(eq(1), any(SpecialtyRequest.class))).thenReturn(response);

        mockMvc.perform(put("/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("updated")));
    }

    @Test
    void updateSpecialty_nonExistingId_returns404() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("updated");
        when(specialtyService.update(eq(999), any(SpecialtyRequest.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(put("/specialties/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_existingId_returnsOk() throws Exception {
        when(specialtyService.delete(1)).thenReturn(radiologyResponse);

        mockMvc.perform(delete("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteSpecialty_nonExistingId_returns404() throws Exception {
        when(specialtyService.delete(999))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(delete("/specialties/999"))
            .andExpect(status().isNotFound());
    }
}
