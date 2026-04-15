package assistancequotidienne2.assistancequotidienne2.Controllers;

import assistancequotidienne2.assistancequotidienne2.DTOs.MedicalRecordDTO;
import assistancequotidienne2.assistancequotidienne2.Entities.MedicalRecord;
import assistancequotidienne2.assistancequotidienne2.Entities.Patient;
import assistancequotidienne2.assistancequotidienne2.Repositories.MedicalRecordRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/medicalRecord")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private PatientRepository patientRepository;

    @PostMapping("/addMedicalRecord")
    public ResponseEntity<MedicalRecord> addMedicalRecord(@Valid @RequestBody MedicalRecordDTO dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient non trouvé avec l'ID: " + dto.getPatientId()));

        MedicalRecord record = new MedicalRecord();
        record.setDiagnosis(dto.getDiagnosis());
        record.setDiseaseStage(dto.getDiseaseStage());
        record.setMedicalHistory(dto.getMedicalHistory());
        record.setAllergies(dto.getAllergies());
        record.setPatient(patient);

        if (dto.getRecordDate() != null && !dto.getRecordDate().isEmpty()) {
            record.setRecordDate(LocalDate.parse(dto.getRecordDate()));
        } else {
            record.setRecordDate(LocalDate.now());
        }
        record.setLastUpdate(LocalDate.now());

        return ResponseEntity.ok(medicalRecordRepository.save(record));
    }

    @GetMapping("/allMedicalRecord")
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecords() {
        return ResponseEntity.ok(medicalRecordRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecord> getMedicalRecord(@PathVariable Long id) {
        return ResponseEntity.ok(medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dossier médical non trouvé avec l'ID: " + id)));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecord>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(medicalRecordRepository.findByPatientId(patientId));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable Long id) {
        medicalRecordRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
