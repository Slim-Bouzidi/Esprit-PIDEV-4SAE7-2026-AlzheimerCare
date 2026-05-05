package com.alzheimer.supportnetwork.service;

import com.alzheimer.supportnetwork.client.PatientServiceClient;
import com.alzheimer.supportnetwork.client.PatientServicePatientDto;
import com.alzheimer.supportnetwork.dto.PatientCreateDto;
import com.alzheimer.supportnetwork.entity.Patient;
import com.alzheimer.supportnetwork.exception.NotFoundException;
import com.alzheimer.supportnetwork.repository.PatientRepository;
import com.alzheimer.supportnetwork.util.GeoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository repo;
    private final PatientServiceClient patientServiceClient;

    public PatientService(PatientRepository repo, PatientServiceClient patientServiceClient) {
        this.repo = repo;
        this.patientServiceClient = patientServiceClient;
    }

    public Patient create(PatientCreateDto dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("Patient id is required");
        }
        GeoUtils.validateOptionalCoordinates(dto.getLatitude(), dto.getLongitude());
        String resolvedName = resolvePatientName(dto.getId(), dto.getFullName(), null);
        Patient p = Patient.builder()
                .id(dto.getId())
                .fullName(resolvedName)
                .zone(dto.getZone())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();
        return repo.save(p);
    }


    public List<Patient> getAll() {
        return repo.findAll();
    }

    public Patient getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient not found: " + id));
    }

    public Patient update(Long id, PatientCreateDto dto) {
        GeoUtils.validateOptionalCoordinates(dto.getLatitude(), dto.getLongitude());
        Patient p = getById(id);
        p.setFullName(resolvePatientName(id, dto.getFullName(), p.getFullName()));
        p.setZone(dto.getZone());
        p.setLatitude(dto.getLatitude());
        p.setLongitude(dto.getLongitude());
        return repo.save(p);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    /**
     * OpenFeign-backed patient name enrichment.
     * Uses patient-service as source of truth when available; otherwise falls back safely.
     */
    private String resolvePatientName(Long patientId, String requestedName, String currentName) {
        String requested = trimToNull(requestedName);
        if (requested != null) {
            return requested;
        }
        try {
            PatientServicePatientDto external = patientServiceClient.getPatientById(patientId);
            if (external != null) {
                String fromPatientService = trimToNull(
                        ((external.getFirstName() == null ? "" : external.getFirstName().trim()) + " " +
                                (external.getLastName() == null ? "" : external.getLastName().trim())).trim()
                );
                if (fromPatientService != null) {
                    return fromPatientService;
                }
            }
        } catch (Exception ex) {
            // Do not break existing support-network flow if patient-service is unavailable or protected.
            log.warn("OpenFeign lookup patient-service failed for patientId {}: {}", patientId, ex.getMessage());
        }
        String current = trimToNull(currentName);
        if (current != null) {
            return current;
        }
        return "Patient #" + patientId;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
