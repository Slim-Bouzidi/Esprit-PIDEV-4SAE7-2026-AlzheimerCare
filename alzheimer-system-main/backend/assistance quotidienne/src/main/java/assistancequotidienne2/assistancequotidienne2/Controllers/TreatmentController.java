package assistancequotidienne2.assistancequotidienne2.Controllers;

import assistancequotidienne2.assistancequotidienne2.DTOs.TreatmentDTO;
import assistancequotidienne2.assistancequotidienne2.Entities.Treatment;
import assistancequotidienne2.assistancequotidienne2.Entities.Patient;
import assistancequotidienne2.assistancequotidienne2.Repositories.TreatmentRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/treatment")
public class TreatmentController {

    @Autowired
    private TreatmentRepository treatmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @PostMapping("/addTreatment")
    public ResponseEntity<Treatment> addTreatment(@Valid @RequestBody TreatmentDTO dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient non trouvé avec l'ID: " + dto.getPatientId()));

        Treatment treatment = new Treatment();
        treatment.setTreatmentName(dto.getTreatmentName());
        treatment.setDosage(dto.getDosage());
        treatment.setFrequency(dto.getFrequency());
        treatment.setStatus(dto.getStatus());
        treatment.setPatient(patient);

        if (dto.getStartDate() != null && !dto.getStartDate().isEmpty()) {
            treatment.setStartDate(LocalDate.parse(dto.getStartDate()));
        }
        if (dto.getEndDate() != null && !dto.getEndDate().isEmpty()) {
            treatment.setEndDate(LocalDate.parse(dto.getEndDate()));
        }

        return ResponseEntity.ok(treatmentRepository.save(treatment));
    }

    @GetMapping("/allTreatment")
    public ResponseEntity<List<Treatment>> getAllTreatments() {
        return ResponseEntity.ok(treatmentRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Treatment> getTreatment(@PathVariable Long id) {
        return ResponseEntity.ok(treatmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Treatment non trouvé avec l'ID: " + id)));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Treatment>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(treatmentRepository.findByPatientId(patientId));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTreatment(@PathVariable Long id) {
        treatmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
