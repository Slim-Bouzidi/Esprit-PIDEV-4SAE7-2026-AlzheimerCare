import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PatientService, Patient } from '../../../services/patient.service';
import { InsightService } from '../../../services/insight.service';

interface MemoireData {
  adresse: string;
  conjoint: string;
  infosCles: string;
  photos: string[];
}

interface ConfiguredMemoire extends MemoireData {
  patientId: number;
  patientName: string;
}

@Component({
  selector: 'app-aidant-memoire-assistee',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="premium-wrapper">
      <div class="premium-header">
        <div class="header-titles">
          <h1 class="main-title">
            <span class="icon-bulb">💡</span> Mémoire Assistée
          </h1>
          <p class="subtitle">Consultez ou enrichissez les souvenirs et informations précieuses de vos proches.</p>
        </div>
      </div>

      <!-- VUE LISTE (TABLEAU) -->
      <div *ngIf="mode === 'list'" class="list-view fade-in">
        <div class="action-bar">
          <h3>Mes fiches de Mémoire Assistée</h3>
          <button class="btn-primary" (click)="goToCreate()">+ Nouvelle Fiche</button>
        </div>

        <div class="dashboard-card no-padding">
          <div class="table-responsive" *ngIf="configuredMemoires.length > 0; else noMemoires">
            <table class="premium-table">
              <thead>
                <tr>
                  <th>Proche</th>
                  <th>Conjoint(e)</th>
                  <th>Photos</th>
                  <th>Dernière Adresse</th>
                  <th class="actions-col">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let mem of configuredMemoires">
                  <td class="font-bold">{{ mem.patientName }}</td>
                  <td>{{ mem.conjoint || '—' }}</td>
                  <td><span class="badge-count">{{ mem.photos.length }} image(s)</span></td>
                  <td class="truncate" [title]="mem.adresse">{{ mem.adresse || '—' }}</td>
                  <td class="actions-col">
                    <button class="btn-action view" (click)="editMemoire(mem.patientId)" title="Consulter / Modifier">
                      ✏️ Consulter
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <ng-template #noMemoires>
            <div class="empty-state">
              <div class="empty-icon">📭</div>
              <p>Aucune mémoire assistée n'a été créée. <br>Cliquez sur "Nouvelle Fiche" pour commencer.</p>
            </div>
          </ng-template>
        </div>
      </div>

      <!-- VUE EDITION / CONSULTATION -->
      <div *ngIf="mode === 'edit'" class="edit-view fade-in">
        <div class="back-bar">
          <button class="btn-back" (click)="goToList()">← Retour au tableau</button>
        </div>

        <div class="patient-selector form-card" *ngIf="patients.length > 0">
          <label class="selector-label">Proche concerné :</label>
          <div class="select-wrapper">
            <select [(ngModel)]="selectedPatientId" (change)="onPatientChange()" class="modern-select">
              <option [ngValue]="null">Sélectionnez un profil...</option>
              <option *ngFor="let p of patients" [ngValue]="p.id">{{ p.nomComplet }}</option>
            </select>
          </div>
        </div>

        <div class="no-patient-state" *ngIf="!selectedPatientId">
          <div class="empty-state-illustration">👨‍👩‍👧‍👦</div>
          <h3>Sélectionnez un proche</h3>
          <p>Choisissez le patient dans le menu ci-dessus pour configurer sa mémoire assistée.</p>
        </div>

        <div *ngIf="selectedPatientId" class="dashboard-grid mt-4">
          
          <!-- Section: Album Photos -->
          <div class="dashboard-card photo-card-section">
            <div class="card-header">
              <h3>📸 Album Photos</h3>
            </div>
            
            <div class="album-grid">
              <div class="photo-preview-box" *ngFor="let photo of formData.photos; let i = index">
                <img [src]="photo" alt="Souvenir" class="photo-img" />
                <div class="photo-overlay">
                  <button class="delete-btn" (click)="removePhoto(i)" title="Supprimer la photo">
                    <span class="cross">×</span>
                  </button>
                </div>
              </div>
              
              <div class="photo-preview-box file-upload-box" title="Ajouter plusieurs photos">
                <input type="file" multiple accept="image/*" (change)="onFilesSelected($event)" id="file-input" class="hidden-input"/>
                <label for="file-input" class="upload-label">
                  <span class="upload-icon">📁</span>
                  <span class="upload-text">Parcourir mes photos <br>(Vous pouvez en sélectionner plusieurs)</span>
                </label>
              </div>
            </div>
          </div>

          <div class="side-column">
            <!-- Section: Infos Clés -->
            <div class="dashboard-card info-card">
              <div class="card-header">
                <h3>⭐ Informations Clés</h3>
              </div>
              <div class="card-body">
                <textarea [(ngModel)]="formData.infosCles" rows="5" class="modern-textarea" placeholder="Anecdotes marquantes, routines..."></textarea>
              </div>
            </div>

            <!-- Section: Informations Personnelles -->
            <div class="dashboard-card personal-card">
              <div class="card-header">
                <h3>🏠 Détails Personnels</h3>
              </div>
              <div class="card-body">
                <div class="input-group">
                  <label>Lieu de résidence</label>
                  <input type="text" [(ngModel)]="formData.adresse" class="modern-input" placeholder="Adresse complète" />
                </div>
                <div class="input-group mt-3">
                  <label>Nom du conjoint(e) / Confiance</label>
                  <input type="text" [(ngModel)]="formData.conjoint" class="modern-input" placeholder="Ex: Jean Dupont" />
                </div>
              </div>
            </div>
          </div>

        </div>

        <div class="floating-action-bar" *ngIf="selectedPatientId">
          <span class="save-status" [class.show]="saveSuccess">✅ Action enregistrée !</span>
          <button class="btn-outline-danger" (click)="signalMemoryDifficulty()" style="margin-right: 15px; border: 1px solid #ef4444; color: #ef4444; padding: 12px 24px; border-radius: 40px; cursor: pointer; font-weight: 600;">
            ⚠️ Signaler une difficulté
          </button>
          <button class="btn-primary" (click)="saveData()">Enregistrer la fiche</button>
        </div>

      </div>
    </div>
  `,
  styles: [`
    :host {
      display: block;
      min-height: 100vh;
      background: linear-gradient(135deg, #f0f4fd 0%, #ffffff 100%);
    }
  
    .premium-wrapper {
      max-width: 1100px;
      margin: 0 auto;
      padding: 40px;
      font-family: 'Inter', 'Segoe UI', sans-serif;
    }
    
    .fade-in {
      animation: fadeInUp 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards;
      opacity: 0;
      transform: translateY(20px);
    }

    @keyframes fadeInUp {
      to { opacity: 1; transform: translateY(0); }
    }

    .premium-header {
      background: rgba(255, 255, 255, 0.7);
      backdrop-filter: blur(20px);
      -webkit-backdrop-filter: blur(20px);
      padding: 24px 32px;
      border-radius: 20px;
      box-shadow: 0 8px 32px rgba(31, 38, 135, 0.05);
      border: 1px solid rgba(255, 255, 255, 0.6);
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 40px;
      position: relative;
      overflow: hidden;
    }
    
    .premium-header::before {
      content: '';
      position: absolute;
      top: 0; left: 0; right: 0; height: 4px;
      background: linear-gradient(90deg, #3b82f6, #8b5cf6, #ec4899);
    }

    .main-title {
      font-size: 2.2rem;
      font-weight: 800;
      margin: 0 0 8px 0;
      background: linear-gradient(45deg, #1e3a8a, #3b82f6);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      line-height: 1.2;
    }

    .icon-bulb { display: inline-block; -webkit-text-fill-color: initial; margin-right: 8px; }

    .subtitle { color: #64748b; font-size: 1.05rem; margin: 0; font-weight: 500; }

    /* TABLE LAYOUT */
    .action-bar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
    }
    
    .action-bar h3 {
      font-size: 1.4rem;
      color: #0f172a;
      font-weight: 700;
    }

    .dashboard-card {
      background: #ffffff;
      border-radius: 24px;
      padding: 30px;
      box-shadow: 0 10px 40px -10px rgba(0,0,0,0.08);
      border: 1px solid rgba(226, 232, 240, 0.8);
      transition: transform 0.3s ease, box-shadow 0.3s ease;
    }
    
    .dashboard-card:hover {
      box-shadow: 0 20px 40px -10px rgba(0,0,0,0.12);
      transform: translateY(-2px);
    }
    
    .dashboard-card.no-padding { padding: 0; overflow: hidden; }

    .premium-table { width: 100%; border-collapse: collapse; text-align: left; }
    .premium-table th {
      background: #f8fafc;
      color: #64748b;
      font-weight: 600;
      padding: 16px 24px;
      border-bottom: 2px solid #e2e8f0;
      text-transform: uppercase;
      font-size: 0.8rem;
      letter-spacing: 0.5px;
    }

    .premium-table td {
      padding: 20px 24px;
      border-bottom: 1px solid #f1f5f9;
      vertical-align: middle;
      color: #334155;
    }
    
    .premium-table tbody tr { transition: background 0.3s ease; }
    .premium-table tbody tr:hover { background: #f0fdfa; }

    .font-bold { font-weight: 700; color: #0f172a; }

    .badge-count {
      background: linear-gradient(135deg, #bfdbfe, #e0e7ff);
      color: #1e40af;
      padding: 6px 12px;
      border-radius: 20px;
      font-size: 0.85rem;
      font-weight: 700;
    }

    .truncate { max-width: 250px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .actions-col { text-align: right; }

    .btn-action {
      background: white;
      border: 1px solid #cbd5e1;
      padding: 10px 18px;
      border-radius: 12px;
      cursor: pointer;
      font-weight: 600;
      font-size: 0.9rem;
      color: #475569;
      transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
      box-shadow: 0 2px 4px rgba(0,0,0,0.02);
    }
    .btn-action:hover {
      background: linear-gradient(45deg, #3b82f6, #2563eb);
      color: white;
      border-color: transparent;
      transform: scale(1.05);
      box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
    }

    .empty-state { text-align: center; padding: 80px 20px; }
    .empty-icon { font-size: 4rem; margin-bottom: 20px; filter: drop-shadow(0 10px 10px rgba(0,0,0,0.1)); }

    /* EDIT VIEW */
    .back-bar { margin-bottom: 24px; }
    .btn-back {
      background: none; border: none; color: #64748b; font-weight: 600;
      font-size: 1.05rem; cursor: pointer; padding: 0; transition: color 0.2s;
    }
    .btn-back:hover { color: #2563eb; }

    .form-card {
      background: white; padding: 20px 28px; border-radius: 16px;
      box-shadow: 0 10px 25px -5px rgba(0,0,0,0.05); border: 1px solid #f1f5f9;
      display: inline-block; margin-bottom: 30px;
    }

    .selector-label { font-weight: 700; color: #334155; margin-right: 16px; font-size: 1.05rem; }

    .modern-select {
      background: #f8fafc; border: 2px solid #e2e8f0; border-radius: 12px;
      padding: 12px 20px; font-size: 1.05rem; outline: none; min-width: 320px;
      font-weight: 600; color: #0f172a; transition: all 0.3s;
    }
    .modern-select:focus { border-color: #3b82f6; box-shadow: 0 0 0 4px rgba(59,130,246,0.1); background: white; }

    .dashboard-grid { display: grid; grid-template-columns: 1fr 420px; gap: 32px; margin-bottom: 100px; }
    
    @media (max-width: 900px) { .dashboard-grid { grid-template-columns: 1fr; } }
    
    .mt-4 { margin-top: 1.5rem; }

    /* Forms & Media */
    .card-header h3 {
      font-size: 1.3rem; color: #0f172a; margin-top: 0; font-weight: 700;
      border-bottom: 2px solid #f1f5f9; padding-bottom: 16px;
    }

    .album-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(140px, 1fr)); gap: 20px; }

    .photo-preview-box {
      width: 100%; aspect-ratio: 1; border-radius: 16px; overflow: hidden;
      position: relative; background: #f1f5f9; border: 1px solid #e2e8f0;
      box-shadow: 0 4px 6px rgba(0,0,0,0.05);
    }
    
    .photo-img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s ease; }
    .photo-preview-box:hover .photo-img { transform: scale(1.1); }

    .photo-overlay {
      position: absolute; inset: 0; background: rgba(15, 23, 42, 0.4);
      display: flex; align-items: center; justify-content: center;
      opacity: 0; transition: opacity 0.3s, backdrop-filter 0.3s;
      backdrop-filter: blur(2px);
    }
    .photo-preview-box:hover .photo-overlay { opacity: 1; }

    .delete-btn {
      background: #ef4444; color: white; border: none; width: 44px; height: 44px;
      border-radius: 50%; font-size: 24px; cursor: pointer; transition: transform 0.2s, background 0.2s;
    }
    .delete-btn:hover { background: #dc2626; transform: scale(1.15); }

    .file-upload-box {
      border: 3px dashed #cbd5e1; background: #f8fafc; display: flex;
      align-items: center; justify-content: center; cursor: pointer; transition: all 0.3s;
    }
    .file-upload-box:hover { border-color: #3b82f6; background: #eff6ff; }
    .hidden-input { display: none; }
    .upload-label {
      display: flex; flex-direction: column; align-items: center; justify-content: center;
      width: 100%; height: 100%; cursor: pointer; text-align: center; padding: 12px;
    }
    .upload-icon { font-size: 2.5rem; margin-bottom: 12px; color: #3b82f6; }
    .upload-text { font-size: 0.85rem; color: #64748b; font-weight: 600; line-height: 1.4; }

    .modern-input, .modern-textarea {
      width: 100%; padding: 16px 20px; border-radius: 12px;
      border: 2px solid #e2e8f0; background: #f8fafc; transition: all 0.3s;
      box-sizing: border-box; font-size: 1rem; color: #0f172a;
    }
    .modern-input:focus, .modern-textarea:focus {
      outline: none; border-color: #3b82f6; background: white;
      box-shadow: 0 0 0 4px rgba(59,130,246,0.1);
    }
    .modern-textarea { min-height: 140px; resize: vertical; line-height: 1.5; }

    .input-group label {
      display: block; font-weight: 700; color: #475569; margin-bottom: 8px; font-size: 0.95rem;
    }
    
    .no-patient-state {
      text-align: center; padding: 60px; border: 3px dashed #cbd5e1;
      border-radius: 20px; background: rgba(255,255,255,0.5); margin-top: 30px;
    }

    .floating-action-bar {
      position: fixed; bottom: 40px; left: 50%; transform: translateX(-30%);
      background: rgba(255, 255, 255, 0.85); backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px);
      padding: 16px 32px; border-radius: 50px;
      box-shadow: 0 20px 40px rgba(0,0,0,0.1), 0 0 0 1px rgba(255,255,255,0.5) inset;
      display: flex; align-items: center; gap: 20px; z-index: 100;
      border: 1px solid rgba(226, 232, 240, 0.8);
    }

    .btn-primary {
      background: linear-gradient(135deg, #3b82f6, #6366f1);
      color: white; border: none; padding: 16px 32px; font-size: 1.1rem;
      font-weight: 700; border-radius: 40px; cursor: pointer;
      box-shadow: 0 10px 20px rgba(59,130,246,0.3);
      transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
    }
    .btn-primary:hover {
      transform: translateY(-3px) scale(1.02);
      box-shadow: 0 15px 25px rgba(59,130,246,0.4);
    }
    .btn-primary:active { transform: translateY(0); }

    .save-status {
      color: #10b981; font-weight: 700; font-size: 1.05rem;
      opacity: 0; transform: translateX(-10px); transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
    }
    .save-status.show { opacity: 1; transform: translateX(0); }
  `]
})
export class AidantMemoireAssisteeComponent implements OnInit {
  mode: 'list' | 'edit' = 'list';
  patients: Patient[] = [];
  configuredMemoires: ConfiguredMemoire[] = [];
  
  selectedPatientId: number | null = null;
  newPhotoUrl: string = '';
  saveSuccess: boolean = false;

  formData: MemoireData = {
    adresse: '',
    conjoint: '',
    infosCles: '',
    photos: []
  };

  constructor(
    private patientService: PatientService,
    private insightService: InsightService
  ) {}

  ngOnInit(): void {
    // Récupérer les patients puis charger le tableau
    this.patientService.getAll().subscribe({
      next: (data) => {
        this.patients = data;
        if (this.patients.length === 0) {
          this.loadMockPatients();
        }
        this.loadConfiguredMemoires();
      },
      error: () => {
        this.loadMockPatients();
        this.loadConfiguredMemoires();
      }
    });
  }

  private loadMockPatients() {
    this.patients = [
      { id: 1, nomComplet: 'Jean Dupont' },
      { id: 2, nomComplet: 'Marie Martin' }
    ];
  }

  // Charge toutes les fiches sauvegardées pour le tableau
  loadConfiguredMemoires(): void {
    this.patientService.getAllMemoiresAssistees().subscribe({
      next: (memoires) => {
        this.configuredMemoires = memoires.map(mem => ({
          patientId: mem.patientId,
          patientName: mem.patientName || this.patients.find(p => p.id === mem.patientId)?.nomComplet || `Patient #${mem.patientId}`,
          adresse: mem.adresse || '',
          conjoint: mem.conjoint || '',
          infosCles: mem.infosCles || '',
          photos: mem.photos || []
        }));
      },
      error: (err) => {
        console.error('Erreur chargement memoires assistees:', err);
        this.configuredMemoires = [];
      }
    });
  }

  // Basculer vers la vue liste
  goToList(): void {
    this.mode = 'list';
    this.selectedPatientId = null;
    this.resetForm();
    this.loadConfiguredMemoires(); // refreshes the table
  }

  // Créer une nouvelle fiche / switcher en vue formulaire vide
  goToCreate(): void {
    this.mode = 'edit';
    this.selectedPatientId = null;
    this.resetForm();
  }

  // Consulter / Editer depuis la liste
  editMemoire(patientId: number): void {
    this.mode = 'edit';
    this.selectedPatientId = patientId;
    this.loadData();
  }

  onPatientChange(): void {
    this.saveSuccess = false;
    if (this.selectedPatientId) {
      this.loadData();
    }
  }

  private loadData() {
    if (!this.selectedPatientId) {
      this.resetForm();
      return;
    }

    this.patientService.getMemoireAssistee(this.selectedPatientId).subscribe({
      next: (memoire) => {
        this.formData = {
          adresse: memoire.adresse || '',
          conjoint: memoire.conjoint || '',
          infosCles: memoire.infosCles || '',
          photos: memoire.photos || []
        };
      },
      error: () => {
        this.resetForm();
        const patient = this.patients.find(p => p.id === this.selectedPatientId);
        if (patient && patient.adresse) {
          this.formData.adresse = patient.adresse;
        }
      }
    });
  }

  resetForm() {
    this.formData = {
      adresse: '',
      conjoint: '',
      infosCles: '',
      photos: []
    };
  }

  addPhoto() {
    if (this.newPhotoUrl.trim()) {
      this.formData.photos.push(this.newPhotoUrl.trim());
      this.newPhotoUrl = '';
    }
  }

  // Permet d'ajouter une ou plusieurs photos depuis le disque
  onFilesSelected(event: any): void {
    const files: FileList = event.target.files;
    if (!files) return;

    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      const reader = new FileReader();
      
      reader.onload = (e: any) => {
        const img = new Image();
        img.onload = () => {
          // Compression via Canvas pour éviter que le LocalStorage explose !
          const canvas = document.createElement('canvas');
          const MAX_WIDTH = 800;
          const MAX_HEIGHT = 800;
          let width = img.width;
          let height = img.height;

          if (width > height) {
            if (width > MAX_WIDTH) {
              height *= MAX_WIDTH / width;
              width = MAX_WIDTH;
            }
          } else {
            if (height > MAX_HEIGHT) {
              width *= MAX_HEIGHT / height;
              height = MAX_HEIGHT;
            }
          }
          canvas.width = width;
          canvas.height = height;
          
          const ctx = canvas.getContext('2d');
          if (ctx) {
            ctx.drawImage(img, 0, 0, width, height);
            const compressedDataUrl = canvas.toDataURL('image/jpeg', 0.6); // Compression JPEG forte
            this.formData.photos.push(compressedDataUrl);
          }
        };
        img.src = e.target.result;
      };
      
      reader.readAsDataURL(file);
    }
    
    // Réinitialisation de l'input
    event.target.value = '';
  }

  removePhoto(index: number) {
    this.formData.photos.splice(index, 1);
  }

  saveData() {
    if (this.selectedPatientId) {
      this.patientService.saveMemoireAssistee(this.selectedPatientId, this.formData).subscribe({
        next: () => {
          this.saveSuccess = true;
          setTimeout(() => {
            this.saveSuccess = false;
          }, 3000);
          this.loadConfiguredMemoires();

          if (this.selectedPatientId) {
            this.insightService.recordInteraction(this.selectedPatientId, 'SUCCESS', 'Fiche mémoire mise à jour').subscribe();
          }
        },
        error: (err) => {
          console.error('Erreur sauvegarde memoire assistee:', err);
          alert("Erreur lors de l'enregistrement de la fiche mémoire.");
        }
      });
    }
  }

  signalMemoryDifficulty() {
    if (this.selectedPatientId) {
      this.insightService.recordInteraction(this.selectedPatientId, 'FAILURE', 'Difficulté de mémorisation signalée par l\'aidant').subscribe({
        next: () => {
          this.saveSuccess = true;
          setTimeout(() => this.saveSuccess = false, 3000);
        }
      });
    }
  }
}
