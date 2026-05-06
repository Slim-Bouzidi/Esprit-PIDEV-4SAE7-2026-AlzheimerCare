package com.alzheimer.supportnetwork.api;

import com.alzheimer.supportnetwork.SupportNetworkApplication;
import com.alzheimer.supportnetwork.dto.LinkCreateDto;
import com.alzheimer.supportnetwork.entity.Patient;
import com.alzheimer.supportnetwork.entity.PatientSupportLink;
import com.alzheimer.supportnetwork.entity.SupportMember;
import com.alzheimer.supportnetwork.exception.ConflictException;
import com.alzheimer.supportnetwork.service.PatientSupportLinkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SupportNetworkApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("PatientSupportLinkController — HTTP contract")
class PatientSupportLinkControllerWebTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private PatientSupportLinkService patientSupportLinkService;

    private static PatientSupportLink sampleLink(long linkId, long patientId, long memberId) {
        Patient p = new Patient();
        p.setId(patientId);
        p.setFullName("Pat");

        SupportMember m = new SupportMember();
        m.setId(memberId);
        m.setFullName("Member");

        PatientSupportLink link = new PatientSupportLink();
        link.setId(linkId);
        link.setPatient(p);
        link.setMember(m);
        link.setRoleInNetwork("Family");
        link.setTrustLevel("TRUSTED");
        link.setPriorityRank(1);
        link.setPermissions(Set.of("GPS_VIEW"));
        link.setCanAccessHome(true);
        return link;
    }

    @Test
    @DisplayName("POST /api/network/link returns 200 with nested patient/member ids")
    void createLinkReturnsJson() throws Exception {
        PatientSupportLink saved = sampleLink(15L, 100L, 200L);
        when(patientSupportLinkService.create(any(LinkCreateDto.class))).thenReturn(saved);

        mockMvc.perform(post("/api/network/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patientId": 100,
                                  "memberId": 200,
                                  "roleInNetwork": "Family",
                                  "trustLevel": "TRUSTED",
                                  "priorityRank": 1,
                                  "permissions": ["GPS_VIEW"],
                                  "canAccessHome": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(15))
                .andExpect(jsonPath("$.trustLevel").value("TRUSTED"))
                .andExpect(jsonPath("$.patient.id").value(100))
                .andExpect(jsonPath("$.member.id").value(200));
    }

    @Test
    @DisplayName("POST /api/network/link duplicate pair maps to 409 conflict body")
    void createLinkConflictReturns409() throws Exception {
        when(patientSupportLinkService.create(any(LinkCreateDto.class)))
                .thenThrow(new ConflictException("This member is already linked to this patient."));

        mockMvc.perform(post("/api/network/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"patientId": 1, "memberId": 2, "roleInNetwork": "x",
                                 "trustLevel": "TRUSTED", "priorityRank": 1,
                                 "permissions": [], "canAccessHome": false}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("This member is already linked to this patient."));
    }

    @Test
    @DisplayName("GET /api/network/patient/{id} returns link list")
    void getPatientNetworkReturnsArray() throws Exception {
        when(patientSupportLinkService.getNetworkByPatient(33L))
                .thenReturn(List.of(sampleLink(1L, 33L, 9L)));

        mockMvc.perform(get("/api/network/patient/33").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].patient.id").value(33))
                .andExpect(jsonPath("$[0].member.id").value(9));
    }

    @Test
    @DisplayName("PUT /api/network/{linkId} returns updated link JSON")
    void updateLinkReturnsJson() throws Exception {
        PatientSupportLink updated = sampleLink(5L, 10L, 20L);
        updated.setTrustLevel("HIGH");
        when(patientSupportLinkService.update(eq(5L), any(LinkCreateDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/network/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"patientId": 10, "memberId": 20, "roleInNetwork": "Friend",
                                 "trustLevel": "HIGH", "priorityRank": 2,
                                 "permissions": [], "canAccessHome": false}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.trustLevel").value("HIGH"));
    }

    @Test
    @DisplayName("PUT /api/network/{linkId} duplicate pair maps to 409")
    void updateLinkConflictReturns409() throws Exception {
        when(patientSupportLinkService.update(eq(8L), any(LinkCreateDto.class)))
                .thenThrow(new ConflictException("This member is already linked to this patient."));

        mockMvc.perform(put("/api/network/8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"patientId": 1, "memberId": 3, "roleInNetwork": "x",
                                 "trustLevel": "TRUSTED", "priorityRank": 1,
                                 "permissions": [], "canAccessHome": false}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("This member is already linked to this patient."));
    }
}
