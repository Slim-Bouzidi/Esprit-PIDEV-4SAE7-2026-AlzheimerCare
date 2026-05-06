package com.alzheimer.supportnetwork.service;

import com.alzheimer.supportnetwork.client.PatientServiceClient;
import com.alzheimer.supportnetwork.client.PatientServicePatientDto;
import com.alzheimer.supportnetwork.dto.PatientCreateDto;
import com.alzheimer.supportnetwork.entity.Patient;
import com.alzheimer.supportnetwork.repository.PatientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatientService - patient-service name enrichment")
class PatientServiceTest {

    @Mock private PatientRepository patientRepository;
    @Mock private PatientServiceClient patientServiceClient;

    @InjectMocks private PatientService patientService;

    @Test
    @DisplayName("GIVEN no fullName WHEN create THEN enrich from patient-service via Feign")
    void createShouldEnrichNameFromPatientService() {
        PatientServicePatientDto external = new PatientServicePatientDto();
        external.setFirstName("Alice");
        external.setLastName("Martin");
        when(patientServiceClient.getPatientById(5L)).thenReturn(external);
        when(patientRepository.save(any(Patient.class))).thenAnswer(inv -> inv.getArgument(0));

        PatientCreateDto dto = new PatientCreateDto();
        dto.setId(5L);
        dto.setZone("A");

        Patient out = patientService.create(dto);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(captor.capture());
        assertThat(captor.getValue().getFullName()).isEqualTo("Alice Martin");
        assertThat(out.getFullName()).isEqualTo("Alice Martin");
    }

    @Test
    @DisplayName("GIVEN patient-service failure WHEN create THEN fallback to synthetic patient label")
    void createShouldFallbackWhenPatientServiceFails() {
        when(patientServiceClient.getPatientById(8L)).thenThrow(new RuntimeException("patient-service down"));
        when(patientRepository.save(any(Patient.class))).thenAnswer(inv -> inv.getArgument(0));

        PatientCreateDto dto = new PatientCreateDto();
        dto.setId(8L);
        dto.setZone("B");

        Patient out = patientService.create(dto);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(captor.capture());
        assertThat(captor.getValue().getFullName()).isEqualTo("Patient #8");
        assertThat(out.getFullName()).isEqualTo("Patient #8");
    }
}

