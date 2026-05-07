package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.SpecialtyService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
    private SpecialtyService service;

    @Test
    void listSpecialties_returnsOk() throws Exception {
        when(service.findAll()).thenReturn(List.of(
            new SpecialtyResponse(1, "radiology"),
            new SpecialtyResponse(2, "surgery")
        ));

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("radiology"));
    }

    @Test
    void getSpecialty_returnsOk() throws Exception {
        when(service.findById(1)).thenReturn(new SpecialtyResponse(1, "radiology"));

        mockMvc.perform(get("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void getSpecialty_returns404() throws Exception {
        when(service.findById(99)).thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(get("/api/specialties/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void addSpecialty_returnsOk() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("radiology");
        when(service.create(any(SpecialtyRequest.class))).thenReturn(new SpecialtyResponse(1, "radiology"));

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void addSpecialty_returns400ForBlankName() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("");

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    void updateSpecialty_returnsOk() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("dentistry");
        when(service.update(eq(1), any(SpecialtyRequest.class))).thenReturn(new SpecialtyResponse(1, "dentistry"));

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("dentistry"));
    }

    @Test
    void updateSpecialty_returns404() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("dentistry");
        when(service.update(eq(99), any(SpecialtyRequest.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(put("/api/specialties/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_returnsOk() throws Exception {
        when(service.delete(1)).thenReturn(new SpecialtyResponse(1, "radiology"));

        mockMvc.perform(delete("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteSpecialty_returns404() throws Exception {
        when(service.delete(99)).thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(delete("/api/specialties/99"))
            .andExpect(status().isNotFound());
    }
}
