package org.example.alzheimerapp.controllers;

import org.example.alzheimerapp.entities.Patient;
import org.example.alzheimerapp.services.interfaces.IPatientService;
import org.example.alzheimerapp.services.implementing.PdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    private final IPatientService patientService;
    private final PdfService pdfService;

    public PatientController(IPatientService patientService, PdfService pdfService) {
        this.patientService = patientService;
        this.pdfService = pdfService;
    }

    // ➕ Add patient
    @PostMapping("/addPatient")
    public Patient addPatient(@RequestBody Patient patient) {
        log.info("Incoming request: POST /api/patient/addPatient");
        return patientService.addPatient(patient);
    }

    // 📋 Get all patients (admin use)
    @GetMapping("/allPatient")
    public List<Patient> getAllPatients() {
        log.info("Incoming request: GET /api/patient/allPatient");
        return patientService.getAllPatients();
    }

    // 🔍 Get one patient by ID
    @GetMapping("/{id}")
    public Patient getPatient(@PathVariable("id") Integer id) {
        log.info("Incoming request: GET /api/patient/{}", id);
        return patientService.getPatientById(id);
    }

    // ✏️ Update patient
    @PutMapping("/update")
    public Patient updatePatient(@RequestBody Patient patient) {
        log.info("Incoming request: PUT /api/patient/update");
        return patientService.updatePatient(patient);
    }

    // ❌ Delete patient
    @DeleteMapping("/delete/{id}")
    public void deletePatient(@PathVariable("id") Integer id) {
        log.info("Incoming request: DELETE /api/patient/delete/{}", id);
        patientService.deletePatient(id);
    }

    // 👩‍⚕️ NEW: Get patients assigned to a specific soignant
    @GetMapping("/soignant/{id}")
    public List<Patient> getPatientsBySoignant(@PathVariable("id") Long id) {
        log.info("Incoming request: GET /api/patient/soignant/{}", id);
        return patientService.getPatientsBySoignant(id);
    }

    // 🟢 NEW: Get patients sorted by status
    @GetMapping("/sortedByStatus")
    public List<Patient> getPatientsSortedByStatus() {
        log.info("Incoming request: GET /api/patient/sortedByStatus");
        return patientService.getPatientsSortedByStatus();
    }

    // 📄 NEW: Export patient treatments to PDF
    @GetMapping("/{id}/treatments/pdf")
    public ResponseEntity<byte[]> exportTreatmentsToPdf(@PathVariable("id") Integer id) {
        log.info("Incoming request: GET /api/patient/{}/treatments/pdf", id);
        Patient patient = patientService.getPatientById(id);

        byte[] pdfBytes = pdfService.generateTreatmentPdf(patient);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "treatments_" + id + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}