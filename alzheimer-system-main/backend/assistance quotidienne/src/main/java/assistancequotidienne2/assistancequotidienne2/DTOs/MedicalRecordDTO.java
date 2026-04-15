package assistancequotidienne2.assistancequotidienne2.DTOs;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class MedicalRecordDTO {
    @NotBlank(message = "Diagnosis is required")
    @Size(max = 500, message = "Diagnosis must be at most 500 characters")
    private String diagnosis;
    
    @Size(max = 100, message = "Disease stage must be at most 100 characters")
    private String diseaseStage;
    
    @Size(max = 2000, message = "Medical history must be at most 2000 characters")
    private String medicalHistory;
    
    @Size(max = 1000, message = "Allergies must be at most 1000 characters")
    private String allergies;
    
    private String recordDate;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;

    public MedicalRecordDTO() {}

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getDiseaseStage() { return diseaseStage; }
    public void setDiseaseStage(String diseaseStage) { this.diseaseStage = diseaseStage; }
    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public String getRecordDate() { return recordDate; }
    public void setRecordDate(String recordDate) { this.recordDate = recordDate; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
}
