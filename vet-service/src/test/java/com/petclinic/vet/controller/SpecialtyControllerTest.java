package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.SpecialtyService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

        mockMvc.perform(get("/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("radiology"));
    }

    @Test
    void getSpecialty_returnsOk() throws Exception {
        when(service.findById(1)).thenReturn(new SpecialtyResponse(1, "radiology"));

        mockMvc.perform(get("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void getSpecialty_returnsNotFound() throws Exception {
        when(service.findById(999)).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(get("/specialties/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void addSpecialty_returnsCreated() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("oncology");
        when(service.create(any())).thenReturn(new SpecialtyResponse(4, "oncology"));

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(4))
            .andExpect(jsonPath("$.name").value("oncology"));
    }

    @Test
    void addSpecialty_returnsBadRequestForBlankName() throws Exception {
        String body = """
            {"name": ""}
            """;

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addSpecialty_returnsBadRequestForMissingName() throws Exception {
        String body = "{}";

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialty_returnsOk() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("oncology");
        when(service.update(eq(1), any())).thenReturn(new SpecialtyResponse(1, "oncology"));

        mockMvc.perform(put("/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("oncology"));
    }

    @Test
    void updateSpecialty_returnsNotFound() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("oncology");
        when(service.update(eq(999), any()))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(put("/specialties/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_returnsOk() throws Exception {
        when(service.delete(1)).thenReturn(new SpecialtyResponse(1, "radiology"));

        mockMvc.perform(delete("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteSpecialty_returnsNotFound() throws Exception {
        when(service.delete(999)).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(delete("/specialties/999"))
            .andExpect(status().isNotFound());
    }
}
