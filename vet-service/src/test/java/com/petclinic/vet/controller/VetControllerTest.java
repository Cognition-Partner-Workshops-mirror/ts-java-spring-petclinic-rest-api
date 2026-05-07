package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.dto.VetRequest;
import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
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

@WebMvcTest(VetController.class)
@Import(GlobalExceptionHandler.class)
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VetService vetService;

    private final VetResponse sampleVet = new VetResponse(1, "James", "Carter",
        List.of(new SpecialtyResponse(1, "radiology")));

    @Test
    void listVets_returnsAll() throws Exception {
        when(vetService.listAll()).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName").value("James"));
    }

    @Test
    void listVets_filterByLastName() throws Exception {
        when(vetService.searchByLastName("Carter")).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/api/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_filterBySpecialtyId() throws Exception {
        when(vetService.findBySpecialty(1)).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/api/vets").param("specialtyId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_filterBySpecialtyName() throws Exception {
        when(vetService.findBySpecialtyName("radiology")).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/api/vets").param("specialtyName", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getVet_returnsVet() throws Exception {
        when(vetService.getById(1)).thenReturn(sampleVet);

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("James"))
            .andExpect(jsonPath("$.lastName").value("Carter"));
    }

    @Test
    void getVet_notFound() throws Exception {
        when(vetService.getById(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/api/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void addVet_createsVet() throws Exception {
        VetRequest request = new VetRequest("James", "Carter", List.of(1));
        when(vetService.create(any())).thenReturn(sampleVet);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void addVet_badRequest_blankFirstName() throws Exception {
        VetRequest request = new VetRequest("", "Carter", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_badRequest_nullLastName() throws Exception {
        String json = """
            {"firstName": "James", "specialtyIds": []}
            """;

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_badRequest_nullSpecialtyIds() throws Exception {
        String json = """
            {"firstName": "James", "lastName": "Carter"}
            """;

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_updatesVet() throws Exception {
        VetRequest request = new VetRequest("Updated", "Carter", List.of(1));
        VetResponse updated = new VetResponse(1, "Updated", "Carter",
            List.of(new SpecialtyResponse(1, "radiology")));
        when(vetService.update(eq(1), any())).thenReturn(updated);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    void updateVet_notFound() throws Exception {
        VetRequest request = new VetRequest("Updated", "Carter", List.of());
        when(vetService.update(eq(999), any())).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put("/api/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_deletesVet() throws Exception {
        when(vetService.delete(1)).thenReturn(sampleVet);

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void deleteVet_notFound() throws Exception {
        when(vetService.delete(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(delete("/api/vets/999"))
            .andExpect(status().isNotFound());
    }
}
