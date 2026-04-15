import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { FicheTransmission } from '../../../models/fiche-transmission.model';
import { FicheTransmissionApiService } from '../../../services/fiche-transmission-api.service';
import { TraitementApiService } from '../../../services/traitement-api.service';
import { PatientService } from '../../../services/patient.service';
import { RapportSuiviService } from '../../../services/rapport-suivi.service';
import { toutesDirectivesRapport } from '../../../models/rapport-suivi-structure.model';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { FormValidator, ValidationErrors, sanitizeInput } from '../../../shared/validation.utils';

@Component({
  selector: 'app-fiche-transmission-panel',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslateModule],
  templateUrl: './fiche-transmission-panel.component.html',
  styleUrls: ['./fiche-transmission-panel.component.css']
})
export class FicheTransmissionPanelComponent implements OnInit, OnChanges {
  @Input() patientId!: string | null;
  @Input() currentDate!: Date;
  @Output() closePanel = new EventEmitter<void>();

  loading = false;
  submissionSuccess = false;
  pdfReady = false;
  savedFicheId: number | null = null;
  error: string | null = null;

  // Send to Doctor state
  sendingToDoctor = false;
  sentToDoctor = false;
  sendDoctorError: string | null = null;

  /** Directives chargées depuis le rapport de suivi */
  mockDirectives: { id: string; libelle: string }[] = [];
  rapportSuiviLibelle = '';
  formErrors: ValidationErrors = {};

  fiche: FicheTransmission = this.createEmptyFiche();

  constructor(
    private ficheApi: FicheTransmissionApiService,
    private traitementApi: TraitementApiService,
    private patientService: PatientService,
    private rapportSuiviService: RapportSuiviService,
    private translate: TranslateService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    if (this.patientId) {
      this.loadPatientData();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['patientId'] && this.patientId) {
      this.submissionSuccess = false;
      this.error = null;
      this.loadPatientData();
    }
  }

  private createEmptyFiche(): FicheTransmission {
    return {
      id: '',
      patientId: '',
      soignantId: 'S001',
      dateCreation: new Date(),
      statut: 'brouillon',
      patientInfo: {
        nom: '',
        prenom: '',
        age: 0,
        dateDuJour: new Date(),
        heureSaisie: new Date()
      },
      soignantInfo: {
        nom: 'Dubois',
        prenom: 'Sophie',
        role: 'Aide-Soignante'
      },
      observanceMedicaments: {
        listeMedicaments: [],
        totalPris: 0,
        totalPrevus: 0
      },
      alimentation: {
        appetit: 'bon',
        hydratation: 'suffisante',
        repasPris: 3,
        repasPrevus: 3,
        details: ''
      },
      vieSociale: {
        activitesRealisees: [],
        interaction: 'normale',
        hygiene: 'autonome',
        sommeil: 'calme'
      },
      suiviDirectives: [],
      signatureSoignant: false,
      commentaireLibre: ''
    };
  }

  private loadPatientData(): void {
    if (!this.patientId) return;
    this.loading = true;
    this.fiche = this.createEmptyFiche();
    this.fiche.patientId = this.patientId;
    this.fiche.patientInfo.dateDuJour = this.currentDate || new Date();
    this.fiche.patientInfo.heureSaisie = new Date();

    const numId = parseInt(this.patientId, 10);

    // Load patient info
    if (!isNaN(numId)) {
      this.patientService.getById(numId).subscribe({
        next: (patient) => {
          const parts = (patient.nomComplet || '').split(' ');
          this.fiche.patientInfo.prenom = parts[0] || '';
          this.fiche.patientInfo.nom = parts.slice(1).join(' ') || '';
          if (patient.dateNaissance) {
            const birth = new Date(patient.dateNaissance);
            const ageDiff = Date.now() - birth.getTime();
            this.fiche.patientInfo.age = Math.floor(ageDiff / (365.25 * 24 * 60 * 60 * 1000));
          }
        },
        error: () => {
          // Fallback: keep empty
        }
      });

      // Load treatments for medication list
      this.traitementApi.getByPatient(numId).subscribe({
        next: (traitements) => {
          this.fiche.observanceMedicaments.listeMedicaments = [];
          traitements.forEach((t: any) => {
            const moments: ('matin' | 'midi' | 'soir')[] = [];
            if (t.momentMatin) moments.push('matin');
            if (t.momentMidi) moments.push('midi');
            if (t.momentSoir) moments.push('soir');
            // If no moment specified, default to matin
            if (moments.length === 0) moments.push('matin');
            moments.forEach(m => {
              this.fiche.observanceMedicaments.listeMedicaments.push({
                nom: t.nomMedicament || 'Médicament',
                dosage: t.dosage || '',
                moment: m,
                pris: false
              });
            });
          });
          this.fiche.observanceMedicaments.totalPrevus = this.fiche.observanceMedicaments.listeMedicaments.length;
          this.fiche.observanceMedicaments.totalPris = 0;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });
    } else {
      this.loading = false;
    }

    // Load directives from the rapport de suivi
    this.chargerDirectives();
  }

  private chargerDirectives(): void {
    const dateStr = (this.currentDate || new Date()).toISOString().slice(0, 10);
    const rapport = this.rapportSuiviService.getRapportPourPatientEtDate(this.fiche.patientId, dateStr)
      ?? this.rapportSuiviService.getDernierRapportPourPatient(this.fiche.patientId);
    if (rapport) {
      const dirs = toutesDirectivesRapport(rapport);
      this.mockDirectives = dirs.map(d => ({ id: d.id, libelle: d.libelle }));
      this.fiche.suiviDirectives = rapport.reponsesSoignant.map(r => ({
        directiveId: r.directiveId,
        reponse: r.commentaireSoignant || '',
        statut: r.statut
      }));
      const deb = rapport.dateDebut ? new Date(rapport.dateDebut) : null;
      const fin = rapport.dateFin ? new Date(rapport.dateFin) : null;
      if (deb && fin) {
        this.rapportSuiviLibelle = this.translate.instant('SOIGNANT.WEEKLY_REPORT_LABEL', {
          start: deb.toLocaleDateString('fr-FR'),
          end: fin.toLocaleDateString('fr-FR'),
          doctor: rapport.medecinNom
        });
      }
    } else {
      this.mockDirectives = [
        { id: 'D1', libelle: this.translate.instant('SOIGNANT.DIRECTIVE_HYDRATION') },
        { id: 'D2', libelle: this.translate.instant('SOIGNANT.DIRECTIVE_BLOOD_PRESSURE') }
      ];
      this.fiche.suiviDirectives = [
        { directiveId: 'D1', reponse: '', statut: 'en_cours' },
        { directiveId: 'D2', reponse: '', statut: 'en_cours' }
      ];
    }
  }

  getDirectiveLabel(id: string): string {
    return this.mockDirectives.find(d => d.id === id)?.libelle || this.translate.instant('SOIGNANT.UNKNOWN_DIRECTIVE');
  }

  updateMedicationCounts(): void {
    const meds = this.fiche.observanceMedicaments.listeMedicaments;
    this.fiche.observanceMedicaments.totalPris = meds.filter(m => m.pris).length;
    this.fiche.observanceMedicaments.totalPrevus = meds.length;
  }

  toggleMedication(index: number): void {
    this.fiche.observanceMedicaments.listeMedicaments[index].pris =
      !this.fiche.observanceMedicaments.listeMedicaments[index].pris;
    this.updateMedicationCounts();
  }

  submitFiche(): void {
    const v = new FormValidator();
    v.required('patientId', this.fiche.patientId, 'Aucun patient sélectionné.');
    v.custom('signatureSoignant', !this.fiche.signatureSoignant, 'Veuillez signer avant de soumettre.');
    v.maxLength('commentaireLibre', this.fiche.commentaireLibre || '', 2000, 'Le commentaire ne doit pas dépasser 2000 caractères.');

    this.formErrors = v.errors;
    if (v.hasErrors()) return;

    // Save as brouillon (draft) - not sent yet
    this.fiche.statut = 'brouillon';
    this.fiche.commentaireLibre = sanitizeInput(this.fiche.commentaireLibre || '');

    const patientIdNum = parseInt(this.fiche.patientId, 10);
    const apiPayload: any = {
      patient: patientIdNum ? { id: patientIdNum } : null,
      dateFiche: new Date().toISOString().slice(0, 10),
      statut: 'brouillon', // Save as draft
      patientInfoJson: JSON.stringify(this.fiche.patientInfo),
      soignantInfoJson: JSON.stringify(this.fiche.soignantInfo),
      observanceMedicamentsJson: JSON.stringify(this.fiche.observanceMedicaments),
      alimentationJson: JSON.stringify(this.fiche.alimentation),
      vieSocialeJson: JSON.stringify(this.fiche.vieSociale),
      suiviDirectivesJson: JSON.stringify(this.fiche.suiviDirectives),
      signatureSoignant: this.fiche.signatureSoignant,
      commentaireLibre: this.fiche.commentaireLibre
    };

    this.ficheApi.create(apiPayload).subscribe({
      next: (saved) => {
        console.log('Fiche sauvegardée en BDD, id:', saved.id);
        this.savedFicheId = saved.id;
        this.submissionSuccess = true;
        this.pdfReady = true;
        this.error = null;
      },
      error: (err) => {
        console.error('Erreur sauvegarde fiche:', err);
        this.error = 'Erreur lors de la sauvegarde. Vérifiez que le backend et MySQL sont démarrés.';
        this.submissionSuccess = false;
      }
    });
  }

  /** Download the auto-generated PDF */
  downloadPdf(): void {
    if (!this.savedFicheId) return;
    const url = `${environment.apiUrl}/fiches/${this.savedFicheId}/pdf`;
    this.http.get(url, { responseType: 'blob' }).subscribe({
      next: (blob) => {
        const blobUrl = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = blobUrl;
        a.download = `FT-${String(this.savedFicheId).padStart(5, '0')}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(blobUrl);
      },
      error: (err) => {
        console.error('Erreur téléchargement PDF:', err);
      }
    });
  }

  /** Send to Doctor */
  sendToDoctor(): void {
    if (!this.savedFicheId || this.sendingToDoctor) return;
    this.sendingToDoctor = true;
    this.sendDoctorError = null;
    this.ficheApi.marquerEnvoye(this.savedFicheId).subscribe({
      next: () => {
        this.sendingToDoctor = false;
        this.sentToDoctor = true;
      },
      error: (err: any) => {
        this.sendingToDoctor = false;
        if (err.status === 400) {
          this.sendDoctorError = 'La fiche doit être signée avant l\'envoi au médecin.';
        } else {
          this.sendDoctorError = 'Erreur lors de l\'envoi. Veuillez réessayer.';
        }
      }
    });
  }

  /** Start a new fiche for same patient */
  nouvelleFiche(): void {
    this.submissionSuccess = false;
    this.pdfReady = false;
    this.savedFicheId = null;
    this.error = null;
    this.sentToDoctor = false;
    this.sendDoctorError = null;
    this.loadPatientData();
  }
}
