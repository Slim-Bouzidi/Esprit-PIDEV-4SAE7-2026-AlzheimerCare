package assistancequotidienne2.assistancequotidienne2.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Traitement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    @JsonIgnoreProperties({"traitements", "rendezVous", "rappels", "rapports", "user"})
    private Patient patient;

    private String nomMedicament;
    private String dosage;
    private String frequence;

    private Boolean momentMatin = false;
    private Boolean momentMidi = false;
    private Boolean momentSoir = false;
    private Boolean momentCoucher = false;
    private LocalTime heurePersonnalisee;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Boolean actif = true;

    private Integer nbPrisesPrevues = 0;
    private Integer nbPrisesEffectuees = 0;

    public Traitement() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public String getNomMedicament() { return nomMedicament; }
    public void setNomMedicament(String nomMedicament) { this.nomMedicament = nomMedicament; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getFrequence() { return frequence; }
    public void setFrequence(String frequence) { this.frequence = frequence; }

    public Boolean getMomentMatin() { return momentMatin; }
    public void setMomentMatin(Boolean momentMatin) { this.momentMatin = momentMatin; }

    public Boolean getMomentMidi() { return momentMidi; }
    public void setMomentMidi(Boolean momentMidi) { this.momentMidi = momentMidi; }

    public Boolean getMomentSoir() { return momentSoir; }
    public void setMomentSoir(Boolean momentSoir) { this.momentSoir = momentSoir; }

    public Boolean getMomentCoucher() { return momentCoucher; }
    public void setMomentCoucher(Boolean momentCoucher) { this.momentCoucher = momentCoucher; }

    public LocalTime getHeurePersonnalisee() { return heurePersonnalisee; }
    public void setHeurePersonnalisee(LocalTime heurePersonnalisee) { this.heurePersonnalisee = heurePersonnalisee; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public Integer getNbPrisesPrevues() { return nbPrisesPrevues; }
    public void setNbPrisesPrevues(Integer nbPrisesPrevues) { this.nbPrisesPrevues = nbPrisesPrevues; }

    public Integer getNbPrisesEffectuees() { return nbPrisesEffectuees; }
    public void setNbPrisesEffectuees(Integer nbPrisesEffectuees) { this.nbPrisesEffectuees = nbPrisesEffectuees; }

    @Transient
    public Double getTauxObservance() {
        if (nbPrisesPrevues == null || nbPrisesPrevues == 0) {
            return 0.0;
        }
        return (nbPrisesEffectuees * 100.0) / nbPrisesPrevues;
    }

    public void enregistrerPrise() {
        this.nbPrisesEffectuees++;
    }
}
