package com.petclinic.vet.controller;

import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.service.VetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VetRestController.class)
@Import(GlobalExceptionHandler.class)
class ExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VetService vetService;

    @MockitoBean
    private VetMapper vetMapper;

    @Test
    void handleDataIntegrityViolation() throws Exception {
        when(vetService.findVetById(1))
            .thenThrow(new DataIntegrityViolationException("duplicate key"));

        mockMvc.perform(get("/vets/1"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.title", is("DataIntegrityViolationException")));
    }

    @Test
    void handleGeneralException() throws Exception {
        when(vetService.findVetById(1))
            .thenThrow(new RuntimeException("unexpected"));

        mockMvc.perform(get("/vets/1"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.title", is("RuntimeException")));
    }

    @Test
    void handleResourceNotFound() throws Exception {
        when(vetService.findVetById(99))
            .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/vets/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail", is("Vet not found with id 99")))
            .andExpect(jsonPath("$.schemaValidationErrors").isArray())
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void problemDetailHasRequiredFields() throws Exception {
        when(vetService.findVetById(1))
            .thenThrow(new ResourceNotFoundException("Vet", 1));

        mockMvc.perform(get("/vets/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.type").exists())
            .andExpect(jsonPath("$.title").exists())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.detail").exists())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.schemaValidationErrors").isArray());
    }
}
