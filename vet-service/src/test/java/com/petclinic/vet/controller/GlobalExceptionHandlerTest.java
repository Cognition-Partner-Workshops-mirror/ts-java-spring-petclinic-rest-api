package com.petclinic.vet.controller;

import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
    void handleResourceNotFoundException_returns404WithProblemDetail() throws Exception {
        when(vetService.findById(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/api/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("ResourceNotFoundException")))
            .andExpect(jsonPath("$.detail", is("Vet not found with id: 999")))
            .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void handleDataIntegrityViolation_returns409() throws Exception {
        when(vetService.findById(1)).thenThrow(new DataIntegrityViolationException("constraint violation"));

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.title", is("DataIntegrityViolationException")));
    }

    @Test
    void handleGeneralException_returns500() throws Exception {
        when(vetService.findById(1)).thenThrow(new RuntimeException("unexpected error"));

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.title", is("RuntimeException")));
    }
}
