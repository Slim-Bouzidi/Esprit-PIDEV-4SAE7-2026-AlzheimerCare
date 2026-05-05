package com.alzheimer.supportnetwork.config;

import com.alzheimer.supportnetwork.entity.Patient;
import com.alzheimer.supportnetwork.repository.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds demo patients at startup if the support-network patient table is empty.
 * IDs are explicit (not auto-generated) for stable demos and distance ranking.
 * Skipped under {@code test} so tests control their own fixtures.
 */
@Component
@Profile("!test")
public class PatientSeedRunner implements CommandLineRunner {

    private final PatientRepository patientRepository;

    public PatientSeedRunner(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public void run(String... args) {
        if (patientRepository.count() > 0) {
            return;
        }
        List<Patient> seed =
                List.of(
                        Patient.builder()
                                .id(101L)
                                .fullName("Mme Fatma Ben Salah")
                                .zone("Ariana")
                                .latitude(36.8665)
                                .longitude(10.1647)
                                .build(),
                        Patient.builder()
                                .id(102L)
                                .fullName("M. Mohamed Sassi")
                                .zone("Tunis")
                                .latitude(36.8065)
                                .longitude(10.1815)
                                .build(),
                        Patient.builder()
                                .id(103L)
                                .fullName("Mme Amel Gharbi")
                                .zone("Sousse")
                                .latitude(35.8256)
                                .longitude(10.6411)
                                .build());
        patientRepository.saveAll(seed);
    }
}
