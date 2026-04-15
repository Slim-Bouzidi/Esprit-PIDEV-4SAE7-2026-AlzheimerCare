import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface NotificationApi {
  id?: number;
  destinataire?: any;
  expediteur?: any;
  patient?: any;
  type?: string;
  titre?: string;
  message?: string;
  lu?: boolean;
  dateCreation?: string;
  dateLecture?: string;
  referenceId?: number;
  referenceType?: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationApiService {
  private baseUrl = `${environment.apiUrl}/notifications`;

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    });
  }

  getByUser(userId: number): Observable<NotificationApi[]> {
    return this.http.get<NotificationApi[]>(`${this.baseUrl}/user/${userId}`, { headers: this.getHeaders() });
  }

  getUnreadByUser(userId: number): Observable<NotificationApi[]> {
    return this.http.get<NotificationApi[]>(`${this.baseUrl}/user/${userId}/unread`, { headers: this.getHeaders() });
  }

  getUnreadCount(userId: number): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.baseUrl}/user/${userId}/count`, { headers: this.getHeaders() });
  }

  marquerLu(id: number): Observable<NotificationApi> {
    return this.http.patch<NotificationApi>(`${this.baseUrl}/${id}/lu`, {}, { headers: this.getHeaders() });
  }

  marquerToutLu(userId: number): Observable<void> {
    return this.http.patch<void>(`${this.baseUrl}/user/${userId}/lu-all`, {}, { headers: this.getHeaders() });
  }

  getAll(): Observable<NotificationApi[]> {
    return this.http.get<NotificationApi[]>(this.baseUrl, { headers: this.getHeaders() });
  }
}
