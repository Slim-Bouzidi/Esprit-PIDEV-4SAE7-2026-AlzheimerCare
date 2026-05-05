package assistancequotidienne2.assistancequotidienne2.Entities;

import javax.persistence.*;

@Entity
@Table(name = "memoire_assistee")
public class MemoireAssistee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private Patient patient;

    private String adresse;

    private String conjoint;

    @Lob
    @Column(name = "infos_cles", columnDefinition = "LONGTEXT")
    private String infosCles;

    @Lob
    @Column(name = "photos_json", columnDefinition = "LONGTEXT")
    private String photosJson;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getConjoint() { return conjoint; }
    public void setConjoint(String conjoint) { this.conjoint = conjoint; }

    public String getInfosCles() { return infosCles; }
    public void setInfosCles(String infosCles) { this.infosCles = infosCles; }

    public String getPhotosJson() { return photosJson; }
    public void setPhotosJson(String photosJson) { this.photosJson = photosJson; }
}
