package com.alzheimer.supportnetwork.api;

import com.alzheimer.supportnetwork.dto.PatientCreateDto;
import com.alzheimer.supportnetwork.entity.Patient;
import com.alzheimer.supportnetwork.SupportNetworkApplication;
import com.alzheimer.supportnetwork.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SupportNetworkApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Support Patient API - basic web behavior")
class SupportPatientApiWebTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private PatientService patientService;

    @Test
    @DisplayName("POST /api/support-patients creates patient")
    void createSupportPatient() throws Exception {
        Patient saved = new Patient();
        saved.setId(100L);
        saved.setFullName("Alice Martin");
        saved.setZone("Z1");
        when(patientService.create(any(PatientCreateDto.class))).thenReturn(saved);

        String body = """
                {
                  "id": 100,
                  "fullName": "Alice Martin",
                  "zone": "Z1"
                }
                """;

        mockMvc.perform(post("/api/support-patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.fullName").value("Alice Martin"))
                .andExpect(jsonPath("$.zone").value("Z1"));
    }

    @Test
    @DisplayName("GET /api/support-patients returns patient list")
    void getAllSupportPatients() throws Exception {
        Patient p = new Patient();
        p.setId(101L);
        p.setFullName("Bob");
        when(patientService.getAll()).thenReturn(List.of(p));

        mockMvc.perform(get("/api/support-patients")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(101))
                .andExpect(jsonPath("$[0].fullName").value("Bob"));
    }
}

