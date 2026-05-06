import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface MLPredictRequest {
  features: number[];
}

export interface MLPredictResponse {
  prediction: number;
  probability: number | null;
}

@Injectable({
  providedIn: 'root'
})
export class MlPredictionService {
  private apiUrl = `${environment.apiUrl}/ml`;

  constructor(private http: HttpClient) { }

  predict(features: number[]): Observable<MLPredictResponse> {
    return this.http.post<MLPredictResponse>(`${this.apiUrl}/predict`, { features });
  }

  checkHealth(): Observable<any> {
    return this.http.get(`${this.apiUrl}/health`);
  }
}
