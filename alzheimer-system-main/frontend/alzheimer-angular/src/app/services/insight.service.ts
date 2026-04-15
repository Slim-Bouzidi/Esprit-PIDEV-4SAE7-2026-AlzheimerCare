import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Insight {
  id?: number;
  message: string;
  type: 'INFO' | 'WARNING' | 'CRITICAL';
  dateCreation: string;
}

@Injectable({
  providedIn: 'root'
})
export class InsightService {
  private apiUrl = `${environment.apiUrl}/insights`;

  constructor(private http: HttpClient) { }

  /**
   * Récupère les insights pour un patient donné.
   */
  getInsights(patientId: number): Observable<Insight[]> {
    return this.http.get<Insight[]>(`${this.apiUrl}/patient/${patientId}`);
  }

  /**
   * Enregistre une interaction (Succès/Échec) avec la mémoire assistée.
   */
  recordInteraction(patientId: number, type: 'SUCCESS' | 'FAILURE', details?: string): Observable<any> {
    let params = new HttpParams()
      .set('patientId', patientId.toString())
      .set('type', type);
    
    if (details) {
      params = params.set('details', details);
    }

    return this.http.post(`${this.apiUrl}/interaction`, {}, { params });
  }

  /**
   * Déclenche une analyse globale pour tous les patients.
   */
  triggerGlobalAnalysis(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/analyze/all`, {});
  }
}
