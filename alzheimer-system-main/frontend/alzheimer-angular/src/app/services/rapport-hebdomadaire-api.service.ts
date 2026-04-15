import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class RapportHebdomadaireApiService {
  private baseUrl = `${environment.apiUrl}/rapports-hebdo`;

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

  create(rapport: any): Observable<any> {
    return this.http.post<any>(this.baseUrl, rapport, { headers: this.getHeaders() });
  }

  update(id: number, rapport: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/${id}`, rapport, { headers: this.getHeaders() });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers: this.getHeaders() });
  }

  marquerEnvoye(id: number): Observable<any> {
    return this.http.patch<any>(`${this.baseUrl}/${id}/envoyer`, {}, { headers: this.getHeaders() });
  }

  marquerConsulte(id: number): Observable<any> {
    return this.http.patch<any>(`${this.baseUrl}/${id}/consulte`, {}, { headers: this.getHeaders() });
  }

  consolider(patientId: number, debut: string, fin: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/consolider/${patientId}?debut=${debut}&fin=${fin}`, {}, { headers: this.getHeaders() });
  }
}
