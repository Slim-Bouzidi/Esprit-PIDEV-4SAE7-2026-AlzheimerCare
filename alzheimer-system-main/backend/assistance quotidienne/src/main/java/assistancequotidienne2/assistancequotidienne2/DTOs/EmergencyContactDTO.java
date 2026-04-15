package assistancequotidienne2.assistancequotidienne2.DTOs;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class EmergencyContactDTO {
    @NotBlank(message = "Full name is required")
    @Size(max = 200, message = "Full name must be at most 200 characters")
    private String fullName;
    
    @NotBlank(message = "Relationship is required")
    @Size(max = 100, message = "Relationship must be at most 100 characters")
    private String relationship;
    
    @Size(max = 20, message = "Phone must be at most 20 characters")
    private String phone;
    
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;

    public EmergencyContactDTO() {}

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
}
