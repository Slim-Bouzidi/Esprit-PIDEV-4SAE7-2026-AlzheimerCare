package org.example.alzheimerapp.services.implementing;

import org.example.alzheimerapp.entities.EmergencyContact;
import org.example.alzheimerapp.entities.MedicalRecord;
import org.example.alzheimerapp.entities.Patient;
import org.example.alzheimerapp.repositories.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientAlertService {

    private static final Logger log = LoggerFactory.getLogger(PatientAlertService.class);

    private final PatientRepository patientRepository;
    private final EmailService emailService;

    public PatientAlertService(PatientRepository patientRepository, EmailService emailService) {
        this.patientRepository = patientRepository;
        this.emailService = emailService;
    }

    /**
     * Executes every 60 seconds (60,000 milliseconds)
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void monitorCriticalPatients() {
        log.info("Running scheduled critical patient monitor...");
        List<Patient> allPatients = patientRepository.findAll();

        for (Patient patient : allPatients) {
            boolean critical = isCritical(patient);

            if (critical && !patient.isAlertSent()) {
                // Patient is critical and alert NOT sent yet -> Send it
                sendAlertsForPatient(patient);
                patient.setAlertSent(true);
                patientRepository.save(patient);
            } else if (!critical && patient.isAlertSent()) {
                // Patient is no longer critical, but alert was previously sent -> Reset flag
                log.info("Patient {} {} is no longer critical. Resetting alert flag.", patient.getFirstName(), patient.getLastName());
                patient.setAlertSent(false);
                patientRepository.save(patient);
            }
        }
    }

    private boolean isCritical(Patient patient) {
        // Condition 1: patient.status == "HIGH" (checking ignoring case to handle
        // "high-risk" or "high" variations)
        boolean hasHighStatus = patient.getStatus() != null &&
                (patient.getStatus().equalsIgnoreCase("HIGH") || patient.getStatus().equalsIgnoreCase("HIGH-RISK"));

        // Condition 2: patient.familyHistoryAlzheimer == true
        boolean hasFamilyHistory = patient.isFamilyHistoryAlzheimer();

        // Condition 3: patient.medicalRecord.diseaseStage == "ADVANCED"
        // Since a patient can have multiple medical records, we check if ANY of them
        // are advanced
        boolean hasAdvancedDisease = false;
        if (patient.getMedicalRecords() != null) {
            for (MedicalRecord record : patient.getMedicalRecords()) {
                if (record.getDiseaseStage() != null && record.getDiseaseStage().equalsIgnoreCase("ADVANCED")) {
                    hasAdvancedDisease = true;
                    break;
                }
            }
        }

        return hasHighStatus && hasFamilyHistory && hasAdvancedDisease;
    }

    private void sendAlertsForPatient(Patient patient) {
        log.info("CRITICAL PATIENT DETECTED: {} {}", patient.getFirstName(), patient.getLastName());

        List<EmergencyContact> contacts = patient.getEmergencyContacts();
        if (contacts == null || contacts.isEmpty()) {
            log.warn("No emergency contacts found for patient ID {}.", patient.getIdPatient());
            return;
        }

        String subject = "Critical Patient Alert - Immediate Attention Required";
        String body = String.format(
                "The patient %s %s is currently in a critical condition.\n\n" +
                        "Status: %s\n" +
                        "Disease Stage: ADVANCED\n\n" +
                        "Please contact the medical center immediately.",
                patient.getFirstName(),
                patient.getLastName(),
                patient.getStatus());

        for (EmergencyContact contact : contacts) {
            if (contact.getEmail() != null && !contact.getEmail().trim().isEmpty()) {
                emailService.sendAlertEmail(contact.getEmail(), subject, body);
            } else {
                log.warn("Contact {} has no email address. Skipping email.", contact.getFullName());
            }
        }
    }
}
