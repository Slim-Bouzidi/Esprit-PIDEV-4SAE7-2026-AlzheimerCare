package assistancequotidienne2.assistancequotidienne2.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;

import java.time.LocalDateTime;

@Entity
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    @JsonIgnoreProperties({"traitements", "rendezVous", "rappels", "rapports", "user"})
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "soignant_id")
    @JsonIgnoreProperties({"patient", "rendezVous", "rapports"})
    private User soignant;

    private String titre;
    private String description;
    private LocalDateTime dateHeure;
    private Integer dureeMinutes = 30;

    @Enumerated(EnumType.STRING)
    private TypeRendezVous typeRdv;

    @Enumerated(EnumType.STRING)
    private StatutRendezVous statut = StatutRendezVous.PLANIFIE;

    private String lieu;
    private String motif;
    private String notes;

    private Boolean rappelEnvoye = false;
    private LocalDateTime rappelDate;
    private LocalDateTime createdAt;

    public RendezVous() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (dateHeure != null) {
            rappelDate = dateHeure.minusHours(24);
        }
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public User getSoignant() { return soignant; }
    public void setSoignant(User soignant) { this.soignant = soignant; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

    public Integer getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(Integer dureeMinutes) { this.dureeMinutes = dureeMinutes; }

    public TypeRendezVous getTypeRdv() { return typeRdv; }
    public void setTypeRdv(TypeRendezVous typeRdv) { this.typeRdv = typeRdv; }

    public StatutRendezVous getStatut() { return statut; }
    public void setStatut(StatutRendezVous statut) { this.statut = statut; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getRappelEnvoye() { return rappelEnvoye; }
    public void setRappelEnvoye(Boolean rappelEnvoye) { this.rappelEnvoye = rappelEnvoye; }

    public LocalDateTime getRappelDate() { return rappelDate; }
    public void setRappelDate(LocalDateTime rappelDate) { this.rappelDate = rappelDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Transient
    public boolean estPasse() {
        return dateHeure != null && LocalDateTime.now().isAfter(dateHeure);
    }

    @Transient
    public boolean estAujourdhui() {
        return dateHeure != null && dateHeure.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }
}
