package com.petclinic.vet.controller;

import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VetRestController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VetService vetService;

    @Test
    void handleResourceNotFound_shouldReturn404WithProblemDetail() throws Exception {
        when(vetService.findById(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/api/vets/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("ResourceNotFoundException")))
            .andExpect(jsonPath("$.detail").value("Vet not found with id: 99"))
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.schemaValidationErrors").isArray());
    }

    @Test
    void handleDataIntegrityViolation_shouldReturn409() throws Exception {
        when(vetService.findAll()).thenThrow(new DataIntegrityViolationException("constraint violation"));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.title", is("DataIntegrityViolationException")));
    }

    @Test
    void handleGeneralException_shouldReturn500() throws Exception {
        when(vetService.findAll()).thenThrow(new RuntimeException("unexpected error"));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.title", is("RuntimeException")));
    }
}
