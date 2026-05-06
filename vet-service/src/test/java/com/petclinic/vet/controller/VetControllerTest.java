package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
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

@WebMvcTest(VetController.class)
@Import(GlobalExceptionHandler.class)
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VetService vetService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listVets_returnsOk() throws Exception {
        VetDto dto = new VetDto(1, "James", "Carter", List.of());
        when(vetService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/vets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("James"))
                .andExpect(jsonPath("$[0].lastName").value("Carter"));
    }

    @Test
    void getVet_found() throws Exception {
        VetDto dto = new VetDto(1, "James", "Carter", List.of());
        when(vetService.findById(1)).thenReturn(dto);

        mockMvc.perform(get("/vets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void getVet_notFound() throws Exception {
        when(vetService.findById(99))
                .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/vets/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void addVet_valid() throws Exception {
        SpecialtyDto specDto = new SpecialtyDto(1, "radiology");
        VetDto responseDto = new VetDto(1, "James", "Carter", List.of(specDto));
        when(vetService.create(any())).thenReturn(responseDto);

        String body = """
                {
                    "firstName": "James",
                    "lastName": "Carter",
                    "specialties": [{"id": 1, "name": "radiology"}]
                }
                """;

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("James"))
                .andExpect(jsonPath("$.specialties[0].name").value("radiology"));
    }

    @Test
    void addVet_invalidBlankFirstName() throws Exception {
        String body = """
                {
                    "firstName": "",
                    "lastName": "Carter",
                    "specialties": []
                }
                """;

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_missingLastName() throws Exception {
        String body = """
                {
                    "firstName": "James",
                    "specialties": []
                }
                """;

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_missingSpecialties() throws Exception {
        String body = """
                {
                    "firstName": "James",
                    "lastName": "Carter"
                }
                """;

        mockMvc.perform(post("/vets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_valid() throws Exception {
        VetDto responseDto = new VetDto(1, "Updated", "Name", List.of());
        when(vetService.update(eq(1), any())).thenReturn(responseDto);

        String body = """
                {
                    "firstName": "Updated",
                    "lastName": "Name",
                    "specialties": []
                }
                """;

        mockMvc.perform(put("/vets/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    void updateVet_notFound() throws Exception {
        when(vetService.update(eq(99), any()))
                .thenThrow(new ResourceNotFoundException("Vet", 99));

        String body = """
                {
                    "firstName": "Updated",
                    "lastName": "Name",
                    "specialties": []
                }
                """;

        mockMvc.perform(put("/vets/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_success() throws Exception {
        doNothing().when(vetService).delete(1);

        mockMvc.perform(delete("/vets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteVet_notFound() throws Exception {
        doThrow(new ResourceNotFoundException("Vet", 99))
                .when(vetService).delete(99);

        mockMvc.perform(delete("/vets/99"))
                .andExpect(status().isNotFound());
    }
}
