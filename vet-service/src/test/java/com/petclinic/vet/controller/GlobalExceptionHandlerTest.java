package com.petclinic.vet.controller;

import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @RestController
    @RequestMapping("/test")
    static class TestController {
        @GetMapping("/not-found")
        public void notFound() {
            throw new ResourceNotFoundException("Entity", 42);
        }

        @GetMapping("/server-error")
        public void serverError() {
            throw new RuntimeException("something broke");
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    void resourceNotFound_returns404WithProblemDetail() throws Exception {
        mockMvc.perform(get("/test/not-found").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("ResourceNotFoundException"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail").value("Entity not found with id: 42"));
    }

    @Test
    void unexpectedError_returns500WithProblemDetail() throws Exception {
        mockMvc.perform(get("/test/server-error").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.title").value("RuntimeException"))
            .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    void resourceNotFoundMessage_constructor() {
        ResourceNotFoundException ex = new ResourceNotFoundException("custom message");
        org.assertj.core.api.Assertions.assertThat(ex.getMessage()).isEqualTo("custom message");
    }
}
