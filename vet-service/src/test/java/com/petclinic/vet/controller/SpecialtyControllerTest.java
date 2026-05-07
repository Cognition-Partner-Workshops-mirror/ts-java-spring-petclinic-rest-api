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

import static org.hamcrest.Matchers.hasSize;
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

    private final SpecialtyResponse sampleSpecialty = new SpecialtyResponse(1, "radiology");

    @Test
    void listSpecialties_returnsAll() throws Exception {
        when(specialtyService.listAll()).thenReturn(List.of(sampleSpecialty));

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name").value("radiology"));
    }

    @Test
    void listSpecialties_filterByName() throws Exception {
        when(specialtyService.searchByName("rad")).thenReturn(List.of(sampleSpecialty));

        mockMvc.perform(get("/api/specialties").param("name", "rad"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getSpecialty_returnsSpecialty() throws Exception {
        when(specialtyService.getById(1)).thenReturn(sampleSpecialty);

        mockMvc.perform(get("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void getSpecialty_notFound() throws Exception {
        when(specialtyService.getById(999)).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(get("/api/specialties/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void addSpecialty_createsSpecialty() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("oncology");
        SpecialtyResponse response = new SpecialtyResponse(2, "oncology");
        when(specialtyService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("oncology"));
    }

    @Test
    void addSpecialty_badRequest_blankName() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("");

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addSpecialty_badRequest_nullName() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialty_updatesSpecialty() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("updated-radiology");
        SpecialtyResponse response = new SpecialtyResponse(1, "updated-radiology");
        when(specialtyService.update(eq(1), any())).thenReturn(response);

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("updated-radiology"));
    }

    @Test
    void updateSpecialty_notFound() throws Exception {
        SpecialtyRequest request = new SpecialtyRequest("updated");
        when(specialtyService.update(eq(999), any())).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(put("/api/specialties/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_deletesSpecialty() throws Exception {
        when(specialtyService.delete(1)).thenReturn(sampleSpecialty);

        mockMvc.perform(delete("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void deleteSpecialty_notFound() throws Exception {
        when(specialtyService.delete(999)).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(delete("/api/specialties/999"))
            .andExpect(status().isNotFound());
    }
}
