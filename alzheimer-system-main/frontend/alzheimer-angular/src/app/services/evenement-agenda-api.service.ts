import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class EvenementAgendaApiService {
  private baseUrl = `${environment.apiUrl}/agenda`;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    });
  }

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(this.baseUrl, { headers: this.getHeaders() });
  }

  getById(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }

  getByPatient(patientId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/patient/${patientId}`, { headers: this.getHeaders() });
  }

  getByDate(date: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/date/${date}`, { headers: this.getHeaders() });
  }

  getBySemaine(debut: string, fin: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/semaine?debut=${debut}&fin=${fin}`, { headers: this.getHeaders() });
  }

  getByPatientAndSemaine(patientId: number, debut: string, fin: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/patient/${patientId}/semaine?debut=${debut}&fin=${fin}`, { headers: this.getHeaders() });
  }

  create(event: any): Observable<any> {
    return this.http.post<any>(this.baseUrl, event, { headers: this.getHeaders() });
  }

  update(id: number, event: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/${id}`, event, { headers: this.getHeaders() });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }

  updateStatut(id: number, statut: string): Observable<any> {
    return this.http.patch<any>(`${this.baseUrl}/${id}/statut?statut=${statut}`, {}, { headers: this.getHeaders() });
  }

  /** Génère automatiquement les événements agenda depuis Traitements, Rappels, RendezVous */
  generer(debut: string, fin: string): Observable<any[]> {
    return this.http.post<any[]>(`${this.baseUrl}/generer?debut=${debut}&fin=${fin}`, {}, { headers: this.getHeaders() });
  }
}
