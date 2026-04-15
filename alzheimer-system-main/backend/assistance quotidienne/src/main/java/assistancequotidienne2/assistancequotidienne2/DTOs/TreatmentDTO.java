package assistancequotidienne2.assistancequotidienne2.DTOs;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class TreatmentDTO {
    @NotBlank(message = "Treatment name is required")
    @Size(max = 200, message = "Treatment name must be at most 200 characters")
    private String treatmentName;
    
    @Size(max = 100, message = "Dosage must be at most 100 characters")
    private String dosage;
    
    @Size(max = 100, message = "Frequency must be at most 100 characters")
    private String frequency;
    
    private String startDate;
    private String endDate;
    
    @Size(max = 50, message = "Status must be at most 50 characters")
    private String status;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;

    public TreatmentDTO() {}

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
}
