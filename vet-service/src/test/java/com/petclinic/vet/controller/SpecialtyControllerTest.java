package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.SpecialtyService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(SpecialtyController.class)
@Import(GlobalExceptionHandler.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SpecialtyService specialtyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listSpecialties_returnsOk() throws Exception {
        SpecialtyDto dto = new SpecialtyDto(1, "radiology");
        when(specialtyService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("radiology"));
    }

    @Test
    void getSpecialty_found() throws Exception {
        SpecialtyDto dto = new SpecialtyDto(1, "radiology");
        when(specialtyService.findById(1)).thenReturn(dto);

        mockMvc.perform(get("/specialties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void getSpecialty_notFound() throws Exception {
        when(specialtyService.findById(99))
                .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(get("/specialties/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void addSpecialty_valid() throws Exception {
        SpecialtyDto dto = new SpecialtyDto(1, "surgery");
        when(specialtyService.create(any())).thenReturn(dto);

        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"surgery\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("surgery"));
    }

    @Test
    void addSpecialty_invalidBlankName() throws Exception {
        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addSpecialty_missingName() throws Exception {
        mockMvc.perform(post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialty_valid() throws Exception {
        SpecialtyDto dto = new SpecialtyDto(1, "updated");
        when(specialtyService.update(eq(1), any())).thenReturn(dto);

        mockMvc.perform(put("/specialties/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated"));
    }

    @Test
    void updateSpecialty_notFound() throws Exception {
        when(specialtyService.update(eq(99), any()))
                .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(put("/specialties/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"updated\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_success() throws Exception {
        doNothing().when(specialtyService).delete(1);

        mockMvc.perform(delete("/specialties/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteSpecialty_notFound() throws Exception {
        doThrow(new ResourceNotFoundException("Specialty", 99))
                .when(specialtyService).delete(99);

        mockMvc.perform(delete("/specialties/99"))
                .andExpect(status().isNotFound());
    }
}
