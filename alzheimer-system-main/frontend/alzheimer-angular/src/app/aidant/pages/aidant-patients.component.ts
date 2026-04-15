import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { PatientService, Patient } from '../../services/patient.service';

@Component({
  selector: 'app-aidant-patients',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslateModule],
  templateUrl: './aidant-patients.component.html',
  styleUrls: ['../aidant-pages.css', './aidant-patients.component.css']
})
export class AidantPatientsComponent implements OnInit {

  patients: Patient[] = [];
  filteredPatients: Patient[] = [];
  selectedPatient: Patient | null = null;
  searchQuery = '';

  constructor(private patientService: PatientService) {}

  ngOnInit(): void {
    this.patientService.getAll().subscribe({
      next: (patients) => {
        this.patients = patients;
        this.filteredPatients = patients;
      },
      error: () => this.loadMockPatients()
    });
  }

  private loadMockPatients(): void {
    this.patients = [
      {
        id: 1, nomComplet: 'Jean Dupont', actif: true,
        numeroDeTelephone: '06 12 34 56 78', adresse: '12 rue des Lilas, Paris',
        dateNaissance: '1948-03-15', antecedents: 'Alzheimer diagnostiqué en 2020, diabète type 2',
        allergies: 'Pénicilline',
        derniereVisite: '2026-02-20'
      },
      {
        id: 2, nomComplet: 'Marie Martin', actif: true,
        numeroDeTelephone: '06 98 76 54 32', adresse: '45 avenue Victor Hugo, Lyon',
        dateNaissance: '1944-07-22', antecedents: 'Alzheimer stade léger, hypertension',
        allergies: 'Aucune',
        derniereVisite: '2026-02-18'
      }
    ];
    this.filteredPatients = [...this.patients];
  }

  filterPatients(): void {
    const q = this.searchQuery.toLowerCase().trim();
    if (!q) {
      this.filteredPatients = [...this.patients];
      return;
    }
    this.filteredPatients = this.patients.filter(p =>
      p.nomComplet.toLowerCase().includes(q) ||
      (p.antecedents || '').toLowerCase().includes(q)
    );
  }

  selectPatient(patient: Patient): void {
    this.selectedPatient = this.selectedPatient?.id === patient.id ? null : patient;
  }

  getPatientInitials(patient: Patient): string {
    return (patient.nomComplet || 'P')
      .split(' ')
      .map(n => n.charAt(0))
      .join('')
      .substring(0, 2)
      .toUpperCase();
  }

  getAge(dateNaissance: string | undefined): number {
    if (!dateNaissance) return 0;
    const birth = new Date(dateNaissance);
    const diff = Date.now() - birth.getTime();
    return Math.floor(diff / (365.25 * 24 * 60 * 60 * 1000));
  }

  formatDate(date: string | undefined): string {
    if (!date) return '—';
    return new Date(date).toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' });
  }

  callPatient(phone: string | undefined): void {
    if (phone) window.open(`tel:${phone.replace(/\s/g, '')}`);
  }
}
