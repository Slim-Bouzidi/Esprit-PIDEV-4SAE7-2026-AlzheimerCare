import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PatientService, Patient } from '../services/patient.service';
import { SidebarComponent } from '../shared/sidebar/sidebar.component';

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
  selector: 'app-doctor-memoire-assistee',
  standalone: true,
  imports: [CommonModule, SidebarComponent],
  template: `
    <div class="page-layout">
      <app-sidebar role="DOCTEUR" userRole="Médecin" userName="Docteur"></app-sidebar>

      <div class="main-content">
        <div class="content-wrapper">

          <!-- HEADER BANNER -->
          <div class="hero-banner">
            <div class="hero-left">
              <div class="hero-icon-wrap">
                <span class="hero-icon">🧠</span>
              </div>
              <div>
                <h1 class="hero-title">Mémoire Assistée</h1>
                <p class="hero-subtitle">Consultez les fiches de souvenirs ajoutées par les aidants pour faciliter vos échanges avec les patients.</p>
              </div>
            </div>
            <div class="hero-stats" *ngIf="mode === 'list'">
              <div class="hero-stat">
                <span class="hero-stat-value">{{ configuredMemoires.length }}</span>
                <span class="hero-stat-label">Fiches actives</span>
              </div>
              <div class="hero-stat">
                <span class="hero-stat-value">{{ totalPhotos }}</span>
                <span class="hero-stat-label">Photos partagées</span>
              </div>
              <div class="hero-stat">
                <span class="hero-stat-value">{{ patients.length }}</span>
                <span class="hero-stat-label">Patients suivis</span>
              </div>
            </div>
          </div>

          <!-- VUE LISTE -->
          <div *ngIf="mode === 'list'" class="list-view fade-in-up">
            <div class="section-card">
              <div class="section-header">
                <div class="section-header-left">
                  <h2 class="section-title">📋 Fiches de souvenirs des patients</h2>
                  <span class="section-count">{{ configuredMemoires.length }} fiche(s)</span>
                </div>
              </div>

              <div class="table-wrap" *ngIf="configuredMemoires.length > 0; else noMemoires">
                <table class="data-table">
                  <thead>
                    <tr>
                      <th>Patient</th>
                      <th>Proche / Référent</th>
                      <th>Album photo</th>
                      <th>Adresse connue</th>
                      <th class="th-action">Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr *ngFor="let mem of configuredMemoires; let i = index" class="table-row" [class.row-alt]="i % 2 === 1">
                      <td>
                        <div class="cell-patient">
                          <div class="avatar" [style.background]="getAvatarColor(i)">{{ mem.patientName.charAt(0).toUpperCase() }}</div>
                          <div>
                            <span class="patient-name">{{ mem.patientName }}</span>
                            <span class="patient-id">ID #{{ mem.patientId }}</span>
                          </div>
                        </div>
                      </td>
                      <td>
                        <span class="cell-text" *ngIf="mem.conjoint">{{ mem.conjoint }}</span>
                        <span class="cell-empty" *ngIf="!mem.conjoint">Non renseigné</span>
                      </td>
                      <td>
                        <div class="photo-badge" [class.has-photos]="mem.photos.length > 0">
                          <span class="photo-badge-icon">🖼️</span>
                          <span>{{ mem.photos.length }}</span>
                        </div>
                      </td>
                      <td>
                        <span class="cell-text cell-address" *ngIf="mem.adresse" [title]="mem.adresse">📍 {{ mem.adresse }}</span>
                        <span class="cell-empty" *ngIf="!mem.adresse">—</span>
                      </td>
                      <td class="th-action">
                        <button class="btn-view" (click)="viewMemoire(mem)">
                          <span>👁️</span> Consulter
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>

              <ng-template #noMemoires>
                <div class="empty-state">
                  <div class="empty-illustration">
                    <span>📭</span>
                  </div>
                  <h3>Aucune fiche disponible</h3>
                  <p>Les aidants n'ont pas encore ajouté de données de mémoire assistée pour vos patients.</p>
                </div>
              </ng-template>
            </div>
          </div>

          <!-- VUE DETAIL (READ-ONLY) -->
          <div *ngIf="mode === 'view' && selectedMemoire" class="detail-view fade-in-up">
            <button class="btn-back" (click)="goToList()">
              ← Retour au tableau
            </button>

            <!-- Patient header card -->
            <div class="patient-header-card">
              <div class="patient-header-left">
                <div class="avatar-lg" [style.background]="'linear-gradient(135deg, #6366f1, #8b5cf6)'">
                  {{ selectedMemoire.patientName.charAt(0).toUpperCase() }}
                </div>
                <div>
                  <h2 class="patient-detail-name">{{ selectedMemoire.patientName }}</h2>
                  <div class="patient-meta-row">
                    <span class="meta-chip source">👤 Source : Aidant</span>
                    <span class="meta-chip photos">🖼️ {{ selectedMemoire.photos.length }} photo(s)</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="detail-grid">
              <!-- PHOTOS -->
              <div class="section-card">
                <div class="section-header">
                  <h2 class="section-title">📸 Album de familiarisation</h2>
                </div>
                <div class="section-body">
                  <div class="photo-grid" *ngIf="selectedMemoire.photos.length > 0; else noPhotos">
                    <div class="photo-card" *ngFor="let photo of selectedMemoire.photos">
                      <img [src]="photo" alt="Souvenir visuel" />
                    </div>
                  </div>
                  <ng-template #noPhotos>
                    <div class="empty-inline">
                      <span>🖼️</span>
                      <p>Aucune photo partagée pour ce patient.</p>
                    </div>
                  </ng-template>
                </div>
              </div>

              <!-- SIDE PANEL -->
              <div class="side-panel">
                <div class="section-card">
                  <div class="section-header">
                    <h2 class="section-title">⭐ Repères & Routines</h2>
                  </div>
                  <div class="section-body">
                    <div class="info-display">
                      {{ selectedMemoire.infosCles || 'Aucune information clé renseignée par l\\'aidant.' }}
                    </div>
                  </div>
                </div>

                <div class="section-card">
                  <div class="section-header">
                    <h2 class="section-title">🏠 Renseignements personnels</h2>
                  </div>
                  <div class="section-body">
                    <div class="field-group">
                      <label>Lieu de résidence</label>
                      <div class="field-value">
                        {{ selectedMemoire.adresse || 'Non renseigné' }}
                      </div>
                    </div>
                    <div class="field-group">
                      <label>Personne de confiance</label>
                      <div class="field-value">
                        {{ selectedMemoire.conjoint || 'Non renseigné' }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  `,
  styles: [`
    /* ===== LAYOUT ===== */
    .page-layout { display: flex; min-height: 100vh; background: #f4f6fb; }
    .main-content { flex: 1; }
    .content-wrapper {
      padding: 28px 36px;
      max-width: 1280px;
      margin: 0;
      font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
    }

    /* ===== ANIMATIONS ===== */
    .fade-in-up {
      animation: fadeInUp .45s cubic-bezier(.16,1,.3,1) forwards;
      opacity: 0;
    }
    @keyframes fadeInUp {
      from { opacity: 0; transform: translateY(12px); }
      to   { opacity: 1; transform: translateY(0); }
    }

    /* ===== HERO BANNER ===== */
    .hero-banner {
      background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
      border-radius: 20px;
      padding: 32px 36px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 28px;
      box-shadow: 0 10px 30px -5px rgba(79,70,229,.3);
      color: #fff;
      gap: 24px;
      flex-wrap: wrap;
    }
    .hero-left { display: flex; align-items: center; gap: 20px; }
    .hero-icon-wrap {
      width: 56px; height: 56px; border-radius: 16px;
      background: rgba(255,255,255,.15);
      backdrop-filter: blur(10px);
      display: flex; align-items: center; justify-content: center;
      font-size: 1.8rem;
    }
    .hero-title { font-size: 1.65rem; font-weight: 800; margin: 0 0 6px 0; }
    .hero-subtitle { font-size: .95rem; margin: 0; opacity: .85; max-width: 460px; }

    .hero-stats { display: flex; gap: 32px; }
    .hero-stat {
      display: flex; flex-direction: column; align-items: center;
      background: rgba(255,255,255,.12);
      border-radius: 14px;
      padding: 14px 22px;
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255,255,255,.2);
      min-width: 100px;
    }
    .hero-stat-value { font-size: 1.75rem; font-weight: 800; line-height: 1; }
    .hero-stat-label { font-size: .75rem; font-weight: 500; opacity: .8; margin-top: 4px; text-transform: uppercase; letter-spacing: .5px; }

    /* ===== SECTION CARDS ===== */
    .section-card {
      background: #fff;
      border-radius: 16px;
      border: 1px solid #e5e7eb;
      box-shadow: 0 1px 3px rgba(0,0,0,.04);
      overflow: hidden;
      margin-bottom: 24px;
    }
    .section-header {
      padding: 18px 24px;
      border-bottom: 1px solid #f1f3f5;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .section-header-left { display: flex; align-items: center; gap: 12px; }
    .section-title { margin: 0; font-size: 1.05rem; font-weight: 700; color: #111827; }
    .section-count {
      font-size: .8rem; font-weight: 600;
      background: #eff6ff; color: #3b82f6;
      padding: 3px 10px; border-radius: 20px;
    }
    .section-body { padding: 24px; }

    /* ===== TABLE ===== */
    .table-wrap { overflow-x: auto; }
    .data-table { width: 100%; border-collapse: collapse; }
    .data-table th {
      padding: 14px 20px;
      text-align: left;
      font-size: .78rem;
      font-weight: 700;
      color: #6b7280;
      text-transform: uppercase;
      letter-spacing: .04em;
      border-bottom: 2px solid #f1f3f5;
      background: #fafbfc;
    }
    .data-table td {
      padding: 16px 20px;
      font-size: .92rem;
      color: #374151;
      border-bottom: 1px solid #f3f4f6;
      vertical-align: middle;
    }
    .table-row { transition: background .15s; }
    .table-row:hover { background: #f9fafb; }
    .row-alt { background: #fafbfd; }
    .row-alt:hover { background: #f3f5f9; }
    .th-action { text-align: right; }

    /* Cell: patient */
    .cell-patient { display: flex; align-items: center; gap: 12px; }
    .avatar {
      width: 40px; height: 40px; border-radius: 12px;
      display: flex; align-items: center; justify-content: center;
      color: #fff; font-weight: 700; font-size: 1.1rem;
      box-shadow: 0 2px 6px rgba(0,0,0,.1);
    }
    .patient-name { display: block; font-weight: 700; color: #111827; }
    .patient-id { display: block; font-size: .75rem; color: #9ca3af; }

    /* Cell: other */
    .cell-text { color: #374151; }
    .cell-address { max-width: 200px; display: inline-block; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .cell-empty { color: #c4c9d4; font-style: italic; font-size: .88rem; }

    /* Photo badge */
    .photo-badge {
      display: inline-flex; align-items: center; gap: 6px;
      background: #f3f4f6; color: #6b7280;
      padding: 5px 14px; border-radius: 20px;
      font-size: .88rem; font-weight: 600;
    }
    .photo-badge.has-photos { background: #eff6ff; color: #2563eb; }
    .photo-badge-icon { font-size: .95rem; }

    /* Button */
    .btn-view {
      display: inline-flex; align-items: center; gap: 6px;
      background: #4f46e5; color: #fff;
      border: none; padding: 9px 18px; border-radius: 10px;
      font-weight: 600; font-size: .88rem;
      cursor: pointer; transition: all .2s;
      box-shadow: 0 2px 8px rgba(79,70,229,.2);
    }
    .btn-view:hover { background: #4338ca; transform: translateY(-1px); box-shadow: 0 4px 12px rgba(79,70,229,.3); }

    /* Empty state */
    .empty-state { text-align: center; padding: 64px 32px; }
    .empty-illustration { font-size: 3.5rem; margin-bottom: 16px; }
    .empty-state h3 { margin: 0 0 8px; font-size: 1.2rem; color: #111827; }
    .empty-state p { margin: 0; color: #6b7280; max-width: 400px; display: inline-block; }

    /* ===== DETAIL VIEW ===== */
    .btn-back {
      background: none; border: none; color: #6b7280;
      font-weight: 600; font-size: .95rem; cursor: pointer;
      padding: 0; margin-bottom: 20px; display: inline-flex; align-items: center; gap: 6px;
      transition: color .2s;
    }
    .btn-back:hover { color: #4f46e5; }

    .patient-header-card {
      background: #fff; border-radius: 16px;
      border: 1px solid #e5e7eb;
      padding: 24px 28px;
      margin-bottom: 24px;
      display: flex; align-items: center; justify-content: space-between;
      box-shadow: 0 1px 3px rgba(0,0,0,.04);
    }
    .patient-header-left { display: flex; align-items: center; gap: 20px; }
    .avatar-lg {
      width: 64px; height: 64px; border-radius: 18px;
      display: flex; align-items: center; justify-content: center;
      color: #fff; font-size: 1.8rem; font-weight: 800;
      box-shadow: 0 6px 14px rgba(99,102,241,.35);
    }
    .patient-detail-name { margin: 0 0 8px; font-size: 1.4rem; font-weight: 800; color: #111827; }
    .patient-meta-row { display: flex; gap: 10px; flex-wrap: wrap; }
    .meta-chip {
      font-size: .8rem; font-weight: 600;
      padding: 4px 12px; border-radius: 20px;
    }
    .meta-chip.source { background: #ecfdf5; color: #059669; }
    .meta-chip.photos { background: #eff6ff; color: #2563eb; }

    .detail-grid { display: grid; grid-template-columns: 1fr 380px; gap: 24px; }
    @media (max-width: 1024px) { .detail-grid { grid-template-columns: 1fr; } }

    /* Photos grid */
    .photo-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(130px, 1fr));
      gap: 14px;
    }
    .photo-card {
      aspect-ratio: 1; border-radius: 14px; overflow: hidden;
      border: 1px solid #e5e7eb; background: #f9fafb;
      box-shadow: 0 2px 4px rgba(0,0,0,.04);
    }
    .photo-card img {
      width: 100%; height: 100%; object-fit: cover;
      transition: transform .4s ease;
    }
    .photo-card:hover img { transform: scale(1.06); }

    .empty-inline {
      display: flex; flex-direction: column; align-items: center;
      padding: 40px; background: #f9fafb; border-radius: 14px;
      border: 2px dashed #e5e7eb; text-align: center;
      color: #6b7280;
    }
    .empty-inline span { font-size: 2.2rem; margin-bottom: 8px; }
    .empty-inline p { margin: 0; font-weight: 500; }

    /* Side panel */
    .side-panel { display: flex; flex-direction: column; gap: 24px; }
    .side-panel .section-card { margin-bottom: 0; }

    .info-display {
      background: #f9fafb; border-radius: 12px; padding: 18px;
      border: 1px solid #f1f3f5; line-height: 1.65;
      color: #374151; font-size: .95rem; white-space: pre-wrap;
      min-height: 80px;
    }
    .field-group { margin-bottom: 18px; }
    .field-group:last-child { margin-bottom: 0; }
    .field-group label {
      display: block; font-size: .78rem; font-weight: 700;
      color: #6b7280; text-transform: uppercase; letter-spacing: .04em;
      margin-bottom: 6px;
    }
    .field-value {
      background: #f9fafb; padding: 12px 16px; border-radius: 10px;
      border: 1px solid #f1f3f5; color: #111827; font-weight: 500;
    }

    @media (max-width: 900px) {
      .hero-banner { flex-direction: column; align-items: flex-start; }
      .hero-stats { width: 100%; justify-content: space-between; }
    }
  `]
})
export class DoctorMemoireAssisteeComponent implements OnInit {
  mode: 'list' | 'view' = 'list';
  patients: Patient[] = [];
  configuredMemoires: ConfiguredMemoire[] = [];
  selectedMemoire: ConfiguredMemoire | null = null;

  private avatarColors = [
    'linear-gradient(135deg, #6366f1, #8b5cf6)',
    'linear-gradient(135deg, #3b82f6, #06b6d4)',
    'linear-gradient(135deg, #f59e0b, #ef4444)',
    'linear-gradient(135deg, #10b981, #059669)',
    'linear-gradient(135deg, #ec4899, #f43f5e)',
    'linear-gradient(135deg, #8b5cf6, #d946ef)',
  ];

  constructor(private patientService: PatientService) {}

  ngOnInit(): void {
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

  get totalPhotos(): number {
    return this.configuredMemoires.reduce((sum, m) => sum + m.photos.length, 0);
  }

  getAvatarColor(index: number): string {
    return this.avatarColors[index % this.avatarColors.length];
  }

  loadConfiguredMemoires(): void {
    this.configuredMemoires = [];
    for (const p of this.patients) {
      if (!p.id) continue;
      const stored = localStorage.getItem('memoire_assistee_' + p.id);
      if (stored) {
        try {
          const data: MemoireData = JSON.parse(stored);
          this.configuredMemoires.push({
            patientId: p.id,
            patientName: p.nomComplet,
            adresse: data.adresse || '',
            conjoint: data.conjoint || '',
            infosCles: data.infosCles || '',
            photos: data.photos || []
          });
        } catch (e) {
          console.error("Erreur lecture cache pour le patient_id " + p.id);
        }
      }
    }
  }

  viewMemoire(memoire: ConfiguredMemoire): void {
    this.selectedMemoire = memoire;
    this.mode = 'view';
  }

  goToList(): void {
    this.mode = 'list';
    this.selectedMemoire = null;
  }
}
