package com.alzheimer.supportnetwork.service;

import com.alzheimer.supportnetwork.client.UserServiceClient;
import com.alzheimer.supportnetwork.client.UserServiceUserDto;
import com.alzheimer.supportnetwork.dto.member.SupportMemberDto;
import com.alzheimer.supportnetwork.entity.SupportMember;
import com.alzheimer.supportnetwork.repository.AvailabilitySlotRepository;
import com.alzheimer.supportnetwork.repository.InterventionReportRepository;
import com.alzheimer.supportnetwork.repository.MemberSkillRepository;
import com.alzheimer.supportnetwork.repository.MissionRepository;
import com.alzheimer.supportnetwork.repository.PatientSupportLinkRepository;
import com.alzheimer.supportnetwork.repository.SkillRepository;
import com.alzheimer.supportnetwork.repository.SupportMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SupportMemberService - user-service email canonicalization")
class SupportMemberServiceTest {

    @Mock private SupportMemberRepository supportMemberRepository;
    @Mock private MemberSkillRepository memberSkillRepository;
    @Mock private SkillRepository skillRepository;
    @Mock private AvailabilitySlotRepository availabilitySlotRepository;
    @Mock private PatientSupportLinkRepository patientSupportLinkRepository;
    @Mock private MissionRepository missionRepository;
    @Mock private InterventionReportRepository interventionReportRepository;
    @Mock private UserServiceClient userServiceClient;

    @InjectMocks private SupportMemberService supportMemberService;

    @Test
    @DisplayName("GIVEN matching email in user-service WHEN create THEN save canonical email from user-service")
    void createShouldCanonicalizeEmailFromUserService() {
        UserServiceUserDto platformUser = new UserServiceUserDto();
        platformUser.setEmail("john.doe@site.com");
        when(userServiceClient.getAllUsers()).thenReturn(List.of(platformUser));
        when(supportMemberRepository.save(any(SupportMember.class))).thenAnswer(invocation -> {
            SupportMember member = invocation.getArgument(0);
            member.setId(10L);
            return member;
        });
        when(supportMemberRepository.findById(10L))
                .thenReturn(Optional.of(savedMember(10L, "John", "john.doe@site.com")));
        when(memberSkillRepository.findByMember_Id(10L)).thenReturn(List.of());

        SupportMemberDto dto = SupportMemberDto.builder()
                .fullName("John")
                .type("FAMILY")
                .email("John.Doe@Site.com")
                .build();

        SupportMemberDto out = supportMemberService.create(dto);

        ArgumentCaptor<SupportMember> captor = ArgumentCaptor.forClass(SupportMember.class);
        verify(supportMemberRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("john.doe@site.com");
        assertThat(out.getEmail()).isEqualTo("john.doe@site.com");
    }

    @Test
    @DisplayName("GIVEN user-service unavailable WHEN create THEN keep original email (safe fallback)")
    void createShouldFallbackToInputEmailWhenFeignFails() {
        when(userServiceClient.getAllUsers()).thenThrow(new RuntimeException("user-service down"));
        when(supportMemberRepository.save(any(SupportMember.class))).thenAnswer(invocation -> {
            SupportMember member = invocation.getArgument(0);
            member.setId(11L);
            return member;
        });
        when(supportMemberRepository.findById(11L))
                .thenReturn(Optional.of(savedMember(11L, "Fallback", "fallback@site.com")));
        when(memberSkillRepository.findByMember_Id(11L)).thenReturn(List.of());

        SupportMemberDto dto = SupportMemberDto.builder()
                .fullName("Fallback")
                .type("FAMILY")
                .email("fallback@site.com")
                .build();

        SupportMemberDto out = supportMemberService.create(dto);

        ArgumentCaptor<SupportMember> captor = ArgumentCaptor.forClass(SupportMember.class);
        verify(supportMemberRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("fallback@site.com");
        assertThat(out.getEmail()).isEqualTo("fallback@site.com");
    }

    private static SupportMember savedMember(Long id, String fullName, String email) {
        SupportMember member = new SupportMember();
        member.setId(id);
        member.setFullName(fullName);
        member.setType("FAMILY");
        member.setLocationZone("Z");
        member.setEmail(email);
        return member;
    }
}

