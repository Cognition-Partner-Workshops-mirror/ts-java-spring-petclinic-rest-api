package com.petclinic.vet.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listVets_returnsSeededData() throws Exception {
        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(6))));
    }

    @Test
    void getVet_existingId_returnsVet() throws Exception {
        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("James")))
            .andExpect(jsonPath("$.lastName", is("Carter")));
    }

    @Test
    void getVet_nonExistingId_returns404() throws Exception {
        mockMvc.perform(get("/api/vets/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void crudVet_fullLifecycle() throws Exception {
        String createJson = """
            {
                "firstName": "Test",
                "lastName": "Vet",
                "specialties": [{"id": 1, "name": "radiology"}]
            }
            """;

        String response = mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Test")))
            .andExpect(jsonPath("$.specialties", hasSize(1)))
            .andReturn().getResponse().getContentAsString();

        int id = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
            .readTree(response).get("id").asInt();

        String updateJson = """
            {
                "firstName": "Updated",
                "lastName": "Vet",
                "specialties": []
            }
            """;

        mockMvc.perform(put("/api/vets/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Updated")));

        mockMvc.perform(delete("/api/vets/" + id))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/vets/" + id))
            .andExpect(status().isNotFound());
    }

    @Test
    void listSpecialties_returnsSeededData() throws Exception {
        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    void crudSpecialty_fullLifecycle() throws Exception {
        String createJson = """
            {"name": "oncology"}
            """;

        String response = mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("oncology")))
            .andReturn().getResponse().getContentAsString();

        int id = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
            .readTree(response).get("id").asInt();

        String updateJson = """
            {"name": "updated-oncology"}
            """;

        mockMvc.perform(put("/api/specialties/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("updated-oncology")));

        mockMvc.perform(delete("/api/specialties/" + id))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/specialties/" + id))
            .andExpect(status().isNotFound());
    }

    @Test
    void filterVets_byLastName() throws Exception {
        mockMvc.perform(get("/api/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("James")));
    }

    @Test
    void filterVets_bySpecialtyName() throws Exception {
        mockMvc.perform(get("/api/vets").param("specialtyName", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void filterVets_bySpecialtyId() throws Exception {
        mockMvc.perform(get("/api/vets").param("specialtyId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void filterSpecialties_byName() throws Exception {
        mockMvc.perform(get("/api/specialties").param("name", "radio"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void addVet_invalidData_returns400() throws Exception {
        String invalidJson = """
            {
                "firstName": "123",
                "lastName": "",
                "specialties": []
            }
            """;

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    void addVet_nonExistentSpecialty_returns404() throws Exception {
        String json = """
            {
                "firstName": "Test",
                "lastName": "Vet",
                "specialties": [{"id": 9999, "name": "fake"}]
            }
            """;

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNotFound());
    }
}
