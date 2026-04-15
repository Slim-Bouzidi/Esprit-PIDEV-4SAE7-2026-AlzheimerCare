import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface TraitementPayload {
  patient: { id: number };
  nomMedicament: string;
  dosage: string;
  frequence?: string;
  momentMatin: boolean;
  momentMidi: boolean;
  momentSoir: boolean;
  momentCoucher: boolean;
  dateDebut: string;   // YYYY-MM-DD
  dateFin: string;     // YYYY-MM-DD
  actif: boolean;
}

@Injectable({ providedIn: 'root' })
export class TraitementApiService {
  private baseUrl = `${environment.apiUrl}/traitements`;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    });
  }

  create(traitement: TraitementPayload): Observable<any> {
    return this.http.post(this.baseUrl, traitement, { headers: this.getHeaders() });
  }

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(this.baseUrl, { headers: this.getHeaders() });
  }

  getByPatient(patientId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/patient/${patientId}`, { headers: this.getHeaders() });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }
}
