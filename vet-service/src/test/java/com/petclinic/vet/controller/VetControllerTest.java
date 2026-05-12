package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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

@WebMvcTest(VetController.class)
@Import(GlobalExceptionHandler.class)
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VetService service;

    private static final String VET_JSON = """
        {
            "firstName": "James",
            "lastName": "Carter",
            "specialties": [{"id": 1, "name": "radiology"}]
        }
        """;

    private VetResponseDto sampleResponse() {
        return new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
    }

    @Test
    void listVets_returnsOk() throws Exception {
        when(service.listAll()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].firstName").value("James"));
    }

    @Test
    void listVets_filterBySpecialty() throws Exception {
        when(service.filterBySpecialty("radiology")).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/vets").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void listVets_searchByName() throws Exception {
        when(service.searchByName("james")).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/vets").param("name", "james"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void listVets_specialtyTakesPrecedenceOverName() throws Exception {
        when(service.filterBySpecialty("surgery")).thenReturn(List.of());

        mockMvc.perform(get("/api/vets")
                .param("specialty", "surgery")
                .param("name", "james"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getVet_returnsOk() throws Exception {
        when(service.getById(1)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"))
            .andExpect(jsonPath("$.specialties[0].name").value("radiology"));
    }

    @Test
    void getVet_returns404() throws Exception {
        when(service.getById(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/api/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void addVet_returnsOk() throws Exception {
        when(service.create(any(VetRequestDto.class))).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VET_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void addVet_returns400ForMissingFirstName() throws Exception {
        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"lastName\": \"Carter\", \"specialties\": []}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_returns400ForMissingLastName() throws Exception {
        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\": \"James\", \"specialties\": []}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_returns400ForMissingSpecialties() throws Exception {
        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\": \"James\", \"lastName\": \"Carter\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_returns400ForInvalidFirstNamePattern() throws Exception {
        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\": \"123\", \"lastName\": \"Carter\", \"specialties\": []}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_returnsOk() throws Exception {
        when(service.update(eq(1), any(VetRequestDto.class))).thenReturn(sampleResponse());

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VET_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void updateVet_returns404() throws Exception {
        when(service.update(eq(999), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(put("/api/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VET_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_returnsOk() throws Exception {
        when(service.delete(1)).thenReturn(sampleResponse());

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteVet_returns404() throws Exception {
        when(service.delete(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(delete("/api/vets/999"))
            .andExpect(status().isNotFound());
    }
}
