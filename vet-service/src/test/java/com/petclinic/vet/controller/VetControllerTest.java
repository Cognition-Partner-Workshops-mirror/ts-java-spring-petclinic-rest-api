package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VetControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VetService vetService;

    @InjectMocks
    private VetController vetController;

    private final SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
    private final VetResponseDto vetDto = new VetResponseDto(1, "James", "Carter", List.of(specialtyDto));

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vetController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    void listVets_returnsOk() throws Exception {
        when(vetService.listVets()).thenReturn(List.of(vetDto));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("James"))
            .andExpect(jsonPath("$[0].specialties[0].name").value("radiology"));
    }

    @Test
    void listVets_filterByLastName() throws Exception {
        when(vetService.findByLastName("Carter")).thenReturn(List.of(vetDto));

        mockMvc.perform(get("/api/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].lastName").value("Carter"));
    }

    @Test
    void listVets_filterBySpecialtyId() throws Exception {
        when(vetService.findBySpecialtyId(1)).thenReturn(List.of(vetDto));

        mockMvc.perform(get("/api/vets").param("specialtyId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void listVets_filterBySpecialtyName() throws Exception {
        when(vetService.findBySpecialtyName("radiology")).thenReturn(List.of(vetDto));

        mockMvc.perform(get("/api/vets").param("specialtyName", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getVet_returnsOk() throws Exception {
        when(vetService.getVet(1)).thenReturn(vetDto);

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void getVet_returnsNotFound() throws Exception {
        when(vetService.getVet(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(get("/api/vets/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void addVet_returnsOk() throws Exception {
        when(vetService.addVet(any(VetRequestDto.class))).thenReturn(vetDto);

        String body = "{\"firstName\": \"James\", \"lastName\": \"Carter\", \"specialtyIds\": [1]}";
        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void addVet_returnsBadRequestForMissingFirstName() throws Exception {
        String body = "{\"lastName\": \"Carter\", \"specialtyIds\": []}";
        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_returnsBadRequestForMissingLastName() throws Exception {
        String body = "{\"firstName\": \"James\", \"specialtyIds\": []}";
        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_returnsBadRequestForMissingSpecialtyIds() throws Exception {
        String body = "{\"firstName\": \"James\", \"lastName\": \"Carter\"}";
        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_returnsBadRequestForInvalidFirstName() throws Exception {
        String body = "{\"firstName\": \"123\", \"lastName\": \"Carter\", \"specialtyIds\": []}";
        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_returnsOk() throws Exception {
        when(vetService.updateVet(eq(1), any(VetRequestDto.class))).thenReturn(vetDto);

        String body = "{\"firstName\": \"James\", \"lastName\": \"Carter\", \"specialtyIds\": [1]}";
        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void updateVet_returnsNotFound() throws Exception {
        when(vetService.updateVet(eq(999), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 999));

        String body = "{\"firstName\": \"James\", \"lastName\": \"Carter\", \"specialtyIds\": []}";
        mockMvc.perform(put("/api/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_returnsOk() throws Exception {
        when(vetService.deleteVet(1)).thenReturn(vetDto);

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteVet_returnsNotFound() throws Exception {
        when(vetService.deleteVet(999)).thenThrow(new ResourceNotFoundException("Vet", 999));

        mockMvc.perform(delete("/api/vets/999"))
            .andExpect(status().isNotFound());
    }
}
