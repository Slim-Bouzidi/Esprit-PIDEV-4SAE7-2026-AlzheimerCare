import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface Patient {
  id?: number;
  nomComplet: string;
  dateNaissance?: string; // "YYYY-MM-DD"
  adresse?: string;
  numeroDeTelephone?: string;
  antecedents?: string;
  allergies?: string;
  nbInterventionsMois?: number;
  derniereVisite?: string; // "YYYY-MM-DD"
  actif?: boolean;
  soignant?: { id?: number; nom?: string; email?: string; role?: string; telephone?: string };
  // Relations (chargées côté front)
  treatments?: any[];
  medicalRecords?: any[];
  emergencyContacts?: any[];
  // Champs UI (non envoyés au backend)
  showStatusDropdown?: boolean;
}

@Injectable({ providedIn: 'root' })
export class PatientService {
  // Aligne l'URL avec le backend Spring Boot (/api/patients)
  private baseUrl = `${environment.apiUrl}/patients`;

  private _refresh$ = new Subject<void>();
  refresh$ = this._refresh$.asObservable();

  constructor(private http: HttpClient) {}

  triggerRefresh(): void {
    this._refresh$.next();
  }

  // Headers pour les requêtes
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    });
  }

  // CRUD Operations

  // Lire tous les patients
  getAll(): Observable<Patient[]> {
    return this.http.get<Patient[]>(this.baseUrl, {
      headers: this.getHeaders()
    });
  }

  // Lire tous les patients depuis le service assistance-quotidienne (users avec role PATIENT)
  getAllFromUsers(): Observable<Patient[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/users`).pipe(
      map((users: any[]) => {
        // Filtrer uniquement les patients (role = PATIENT)
        return users
          .filter(u => u.role === 'PATIENT')
          .map(u => ({
            id: u.patient?.id || u.id,
            nomComplet: u.nom || `${u.patient?.prenom || ''} ${u.patient?.nom || ''}`.trim(),
            nom: u.patient?.nom || u.nom,
            prenom: u.patient?.prenom || '',
            userId: u.id,
            actif: u.actif
          } as Patient));
      })
    );
  }

  // Lire un patient par ID
  getById(id: number): Observable<Patient> {
    return this.http.get<Patient>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }

  // Créer un nouveau patient (objet adapté au backend Spring)
  create(patient: any): Observable<any> {
    // Le backend attend directement les champs français, pas besoin d'adaptation
    return this.http.post<any>(this.baseUrl, patient, {
      headers: this.getHeaders()
    });
  }

  // Mettre à jour un patient
  update(id: number, patient: Patient): Observable<Patient> {
    return this.http.put<Patient>(`${this.baseUrl}/${id}`, patient, { headers: this.getHeaders() });
  }

  // Supprimer un patient
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }

  // Méthodes supplémentaires utiles

  // Rechercher des patients par nom
  searchByName(nom: string): Observable<Patient[]> {
    return this.http.get<Patient[]>(`${this.baseUrl}/search?nom=${nom}`, { headers: this.getHeaders() });
  }

  // Obtenir les patients actifs
  getActivePatients(): Observable<Patient[]> {
    return this.http.get<Patient[]>(`${this.baseUrl}/actifs`, { headers: this.getHeaders() });
  }

  // Obtenir les patients par statut
  getByStatus(status: string): Observable<Patient[]> {
    return this.http.get<Patient[]>(`${this.baseUrl}/status/${status}`, { headers: this.getHeaders() });
  }

  // Mettre à jour le statut d'un patient
  updateStatus(id: number, status: string): Observable<Patient> {
    return this.http.patch<Patient>(`${this.baseUrl}/${id}/status`, { status }, { headers: this.getHeaders() });
  }

  // Obtenir le nombre d'interventions du mois
  getInterventionsMois(id: number): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/${id}/interventions-mois`, { headers: this.getHeaders() });
  }

  assignerSoignant(patientId: number, soignantId: number | null): Observable<Patient> {
    return this.http.patch<Patient>(`${this.baseUrl}/${patientId}/assigner-soignant`,
      { soignantId }, { headers: this.getHeaders() });
  }

  getBySoignant(soignantId: number): Observable<Patient[]> {
    return this.http.get<Patient[]>(`${this.baseUrl}/soignant/${soignantId}`, { headers: this.getHeaders() });
  }

  // Gestion des erreurs
  private handleError(error: any): Observable<never> {
    console.error('PatientService Error:', error);
    throw error;
  }
}
