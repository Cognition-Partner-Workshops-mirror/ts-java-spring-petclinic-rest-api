package com.petclinic.vet.exception;

import com.petclinic.vet.controller.VetController;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.service.VetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for GlobalExceptionHandler covering DataIntegrityViolation and general exceptions.
 */
@WebMvcTest(VetController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VetService vetService;

    @MockBean
    private VetMapper vetMapper;

    @Test
    void handleDataIntegrityViolation_returns409() throws Exception {
        when(vetService.findAll()).thenThrow(new DataIntegrityViolationException("Constraint violation"));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.title", is("DataIntegrityViolationException")))
            .andExpect(jsonPath("$.status", is(409)));
    }

    @Test
    void handleGeneralException_returns500() throws Exception {
        when(vetService.findAll()).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.title", is("RuntimeException")))
            .andExpect(jsonPath("$.status", is(500)));
    }
}
