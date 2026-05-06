package com.alzheimer.supportnetwork.service;

import com.alzheimer.supportnetwork.dto.LinkCreateDto;
import com.alzheimer.supportnetwork.entity.Patient;
import com.alzheimer.supportnetwork.entity.PatientSupportLink;
import com.alzheimer.supportnetwork.entity.SupportMember;
import com.alzheimer.supportnetwork.exception.ConflictException;
import com.alzheimer.supportnetwork.exception.NotFoundException;
import com.alzheimer.supportnetwork.repository.PatientRepository;
import com.alzheimer.supportnetwork.repository.PatientSupportLinkRepository;
import com.alzheimer.supportnetwork.repository.SupportMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatientSupportLinkService - create and duplicate protection")
class PatientSupportLinkServiceTest {

    @Mock private PatientSupportLinkRepository linkRepo;
    @Mock private PatientRepository patientRepo;
    @Mock private SupportMemberRepository memberRepo;

    @InjectMocks private PatientSupportLinkService patientSupportLinkService;

    @Test
    @DisplayName("GIVEN patient/member not already linked WHEN create THEN persist link")
    void createShouldPersistLinkWhenNotDuplicate() {
        Patient patient = new Patient();
        patient.setId(1L);
        SupportMember member = new SupportMember();
        member.setId(2L);

        when(patientRepo.findById(1L)).thenReturn(Optional.of(patient));
        when(memberRepo.findById(2L)).thenReturn(Optional.of(member));
        when(linkRepo.existsByPatient_IdAndMember_Id(1L, 2L)).thenReturn(false);
        when(linkRepo.save(any(PatientSupportLink.class))).thenAnswer(inv -> inv.getArgument(0));

        LinkCreateDto dto = new LinkCreateDto();
        dto.setPatientId(1L);
        dto.setMemberId(2L);
        dto.setRoleInNetwork("Family");
        dto.setTrustLevel("TRUSTED");
        dto.setPriorityRank(1);
        dto.setPermissions(Set.of("GPS_VIEW"));
        dto.setCanAccessHome(true);

        PatientSupportLink out = patientSupportLinkService.create(dto);

        ArgumentCaptor<PatientSupportLink> captor = ArgumentCaptor.forClass(PatientSupportLink.class);
        verify(linkRepo).save(captor.capture());
        assertThat(captor.getValue().getPatient().getId()).isEqualTo(1L);
        assertThat(captor.getValue().getMember().getId()).isEqualTo(2L);
        assertThat(captor.getValue().isCanAccessHome()).isTrue();
        assertThat(out.getTrustLevel()).isEqualTo("TRUSTED");
    }

    @Test
    @DisplayName("GIVEN duplicate patient/member WHEN create THEN throw ConflictException")
    void createShouldRejectDuplicateLink() {
        Patient patient = new Patient();
        patient.setId(1L);
        SupportMember member = new SupportMember();
        member.setId(2L);

        when(patientRepo.findById(1L)).thenReturn(Optional.of(patient));
        when(memberRepo.findById(2L)).thenReturn(Optional.of(member));
        when(linkRepo.existsByPatient_IdAndMember_Id(1L, 2L)).thenReturn(true);

        LinkCreateDto dto = new LinkCreateDto();
        dto.setPatientId(1L);
        dto.setMemberId(2L);

        assertThrows(ConflictException.class, () -> patientSupportLinkService.create(dto));
    }

    @Test
    @DisplayName("GIVEN existing link WHEN update keeps unique patient/member THEN save")
    void updateShouldPersistWhenNoDuplicateAmongOtherLinks() {
        Patient patient = new Patient();
        patient.setId(1L);
        SupportMember member = new SupportMember();
        member.setId(2L);

        PatientSupportLink existing = PatientSupportLink.builder()
                .id(10L)
                .patient(patient)
                .member(member)
                .trustLevel("LOW")
                .priorityRank(1)
                .build();

        when(linkRepo.findById(10L)).thenReturn(Optional.of(existing));
        when(patientRepo.findById(1L)).thenReturn(Optional.of(patient));
        when(memberRepo.findById(2L)).thenReturn(Optional.of(member));
        when(linkRepo.existsByPatient_IdAndMember_IdAndIdNot(1L, 2L, 10L)).thenReturn(false);
        when(linkRepo.save(any(PatientSupportLink.class))).thenAnswer(inv -> inv.getArgument(0));

        LinkCreateDto dto = new LinkCreateDto();
        dto.setPatientId(1L);
        dto.setMemberId(2L);
        dto.setRoleInNetwork("Caregiver");
        dto.setTrustLevel("TRUSTED");
        dto.setPriorityRank(3);
        dto.setPermissions(Set.of("GPS_VIEW"));
        dto.setCanAccessHome(true);

        PatientSupportLink out = patientSupportLinkService.update(10L, dto);

        assertThat(out.getTrustLevel()).isEqualTo("TRUSTED");
        assertThat(out.getPriorityRank()).isEqualTo(3);
        verify(linkRepo).save(existing);
    }

    @Test
    @DisplayName("GIVEN another link with same patient/member WHEN update THEN ConflictException")
    void updateShouldRejectWhenDuplicateExistsOnDifferentLink() {
        Patient patient = new Patient();
        patient.setId(1L);
        SupportMember memberA = new SupportMember();
        memberA.setId(2L);
        SupportMember memberB = new SupportMember();
        memberB.setId(3L);

        PatientSupportLink existing = PatientSupportLink.builder()
                .id(10L)
                .patient(patient)
                .member(memberA)
                .build();

        when(linkRepo.findById(10L)).thenReturn(Optional.of(existing));
        when(patientRepo.findById(1L)).thenReturn(Optional.of(patient));
        when(memberRepo.findById(3L)).thenReturn(Optional.of(memberB));
        when(linkRepo.existsByPatient_IdAndMember_IdAndIdNot(1L, 3L, 10L)).thenReturn(true);

        LinkCreateDto dto = new LinkCreateDto();
        dto.setPatientId(1L);
        dto.setMemberId(3L);

        assertThrows(ConflictException.class, () -> patientSupportLinkService.update(10L, dto));
    }

    @Test
    @DisplayName("GIVEN unknown link id WHEN update THEN NotFoundException")
    void updateShouldThrowWhenLinkMissing() {
        when(linkRepo.findById(999L)).thenReturn(Optional.empty());

        LinkCreateDto dto = new LinkCreateDto();
        dto.setPatientId(1L);
        dto.setMemberId(2L);

        assertThrows(NotFoundException.class, () -> patientSupportLinkService.update(999L, dto));
    }
}

