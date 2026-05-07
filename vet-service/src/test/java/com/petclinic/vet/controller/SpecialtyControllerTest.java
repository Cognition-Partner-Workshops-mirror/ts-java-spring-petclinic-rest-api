package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.service.SpecialtyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

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

@WebMvcTest(SpecialtyController.class)
@Import(GlobalExceptionHandler.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SpecialtyService specialtyService;

    @MockBean
    private SpecialtyMapper specialtyMapper;

    private Specialty radiology;
    private Specialty surgery;
    private SpecialtyResponseDto radiologyDto;
    private SpecialtyResponseDto surgeryDto;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        surgery = new Specialty();
        surgery.setId(2);
        surgery.setName("surgery");

        radiologyDto = new SpecialtyResponseDto(1, "radiology");
        surgeryDto = new SpecialtyResponseDto(2, "surgery");
    }

    @Test
    void listSpecialties_returns200WithArray() throws Exception {
        when(specialtyService.findAll()).thenReturn(Arrays.asList(radiology, surgery));
        when(specialtyMapper.toResponseDtos(any(List.class))).thenReturn(Arrays.asList(radiologyDto, surgeryDto));

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("radiology"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("surgery"));
    }

    @Test
    void getSpecialty_existingId_returns200() throws Exception {
        when(specialtyService.findById(1)).thenReturn(radiology);
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        mockMvc.perform(get("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void getSpecialty_nonExistingId_returns404() throws Exception {
        when(specialtyService.findById(99)).thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(get("/api/specialties/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void addSpecialty_validBody_returns201WithLocation() throws Exception {
        SpecialtyRequestDto requestDto = new SpecialtyRequestDto("oncology");
        Specialty created = new Specialty();
        created.setId(3);
        created.setName("oncology");
        SpecialtyResponseDto responseDto = new SpecialtyResponseDto(3, "oncology");

        when(specialtyService.create(any(SpecialtyRequestDto.class))).thenReturn(created);
        when(specialtyMapper.toResponseDto(created)).thenReturn(responseDto);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.name").value("oncology"));
    }

    @Test
    void addSpecialty_invalidBody_returns400() throws Exception {
        SpecialtyRequestDto requestDto = new SpecialtyRequestDto("");

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.schemaValidationErrors").isArray());
    }

    @Test
    void updateSpecialty_existingId_returns200() throws Exception {
        SpecialtyRequestDto requestDto = new SpecialtyRequestDto("updated-radiology");
        Specialty updated = new Specialty();
        updated.setId(1);
        updated.setName("updated-radiology");
        SpecialtyResponseDto responseDto = new SpecialtyResponseDto(1, "updated-radiology");

        when(specialtyService.update(eq(1), any(SpecialtyRequestDto.class))).thenReturn(updated);
        when(specialtyMapper.toResponseDto(updated)).thenReturn(responseDto);

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("updated-radiology"));
    }

    @Test
    void updateSpecialty_nonExistingId_returns404() throws Exception {
        SpecialtyRequestDto requestDto = new SpecialtyRequestDto("updated");
        when(specialtyService.update(eq(99), any(SpecialtyRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(put("/api/specialties/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deleteSpecialty_existingId_returns204() throws Exception {
        doNothing().when(specialtyService).delete(1);

        mockMvc.perform(delete("/api/specialties/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteSpecialty_nonExistingId_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Specialty", 99)).when(specialtyService).delete(99);

        mockMvc.perform(delete("/api/specialties/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }
}
