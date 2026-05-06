package com.alzheimer.supportnetwork.api;

import com.alzheimer.supportnetwork.SupportNetworkApplication;
import com.alzheimer.supportnetwork.dto.member.SupportMemberDto;
import com.alzheimer.supportnetwork.service.SupportMemberService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SupportNetworkApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("SupportMemberController — HTTP contract")
class SupportMemberControllerWebTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private SupportMemberService supportMemberService;

    @Test
    @DisplayName("POST /api/members returns 200 and persisted member JSON")
    void createReturnsMemberJson() throws Exception {
        SupportMemberDto saved = SupportMemberDto.builder()
                .id(42L)
                .fullName("Jean Dupont")
                .email("jean@example.org")
                .type("VOLUNTEER")
                .skills(List.of("NURSE"))
                .build();
        when(supportMemberService.create(any(SupportMemberDto.class))).thenReturn(saved);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Jean Dupont",
                                  "email": "jean@example.org",
                                  "type": "VOLUNTEER",
                                  "skills": ["NURSE"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.fullName").value("Jean Dupont"))
                .andExpect(jsonPath("$.email").value("jean@example.org"))
                .andExpect(jsonPath("$.type").value("VOLUNTEER"))
                .andExpect(jsonPath("$.skills[0]").value("NURSE"));
    }

    @Test
    @DisplayName("GET /api/members returns 200 and array of members")
    void getAllReturnsArray() throws Exception {
        SupportMemberDto m = SupportMemberDto.builder().id(1L).fullName("Alice").build();
        when(supportMemberService.findAllWithSkills()).thenReturn(List.of(m));

        mockMvc.perform(get("/api/members").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].fullName").value("Alice"));
    }

    @Test
    @DisplayName("GET /api/members/{id} returns 200 and member JSON")
    void getByIdReturnsMember() throws Exception {
        SupportMemberDto m = SupportMemberDto.builder().id(7L).fullName("Bob").phone("+33123456789").build();
        when(supportMemberService.getById(7L)).thenReturn(m);

        mockMvc.perform(get("/api/members/7").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.fullName").value("Bob"))
                .andExpect(jsonPath("$.phone").value("+33123456789"));
    }

    @Test
    @DisplayName("PUT /api/members/{id} returns 200 and updated member JSON")
    void updateReturnsMemberJson() throws Exception {
        SupportMemberDto updated = SupportMemberDto.builder().id(7L).fullName("Robert").build();
        when(supportMemberService.update(eq(7L), any(SupportMemberDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/members/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName": "Robert",
                                  "type": "DOCTOR"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.fullName").value("Robert"));
    }
}
