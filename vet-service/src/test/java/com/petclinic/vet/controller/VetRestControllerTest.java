package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VetRestController.class)
@Import(GlobalExceptionHandler.class)
class VetRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VetService vetService;

    private final VetDto vetDto = new VetDto(1, "James", "Carter",
        List.of(new SpecialtyDto(1, "radiology")));

    @Test
    void listVets_shouldReturnAllVets() throws Exception {
        when(vetService.findAll()).thenReturn(List.of(vetDto));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("James")));
    }

    @Test
    void listVets_shouldReturnEmptyList() throws Exception {
        when(vetService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getVet_shouldReturnVet() throws Exception {
        when(vetService.findById(1)).thenReturn(vetDto);

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("James")))
            .andExpect(jsonPath("$.lastName", is("Carter")))
            .andExpect(jsonPath("$.specialties", hasSize(1)));
    }

    @Test
    void getVet_shouldReturn404WhenNotFound() throws Exception {
        when(vetService.findById(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/api/vets/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("ResourceNotFoundException")));
    }

    @Test
    void addVet_shouldCreateAndReturnVet() throws Exception {
        when(vetService.create(any(VetRequestDto.class))).thenReturn(vetDto);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "James",
                        "lastName": "Carter",
                        "specialties": [{"id": 1, "name": "radiology"}]
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    void addVet_shouldReturn400ForInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "",
                        "lastName": "",
                        "specialties": []
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").exists());
    }

    @Test
    void addVet_shouldReturn400ForMissingFields() throws Exception {
        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_shouldUpdateAndReturnVet() throws Exception {
        VetDto updated = new VetDto(1, "Helen", "Leary", List.of());
        when(vetService.update(eq(1), any(VetRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "Helen",
                        "lastName": "Leary",
                        "specialties": []
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Helen")));
    }

    @Test
    void updateVet_shouldReturn404WhenNotFound() throws Exception {
        when(vetService.update(eq(99), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(put("/api/vets/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "Helen",
                        "lastName": "Leary",
                        "specialties": []
                    }
                    """))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_shouldReturn204() throws Exception {
        doNothing().when(vetService).delete(1);

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteVet_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Vet", 99)).when(vetService).delete(99);

        mockMvc.perform(delete("/api/vets/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void searchVets_byLastName_shouldReturnResults() throws Exception {
        when(vetService.findByLastName("Carter")).thenReturn(List.of(vetDto));

        mockMvc.perform(get("/api/vets/search").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchVets_bySpecialty_shouldReturnResults() throws Exception {
        when(vetService.findBySpecialty("radiology")).thenReturn(List.of(vetDto));

        mockMvc.perform(get("/api/vets/search").param("specialty", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchVets_byName_shouldReturnResults() throws Exception {
        when(vetService.search("James")).thenReturn(List.of(vetDto));

        mockMvc.perform(get("/api/vets/search").param("name", "James"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchVets_noParams_shouldReturnAll() throws Exception {
        when(vetService.findAll()).thenReturn(List.of(vetDto));

        mockMvc.perform(get("/api/vets/search"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }
}
