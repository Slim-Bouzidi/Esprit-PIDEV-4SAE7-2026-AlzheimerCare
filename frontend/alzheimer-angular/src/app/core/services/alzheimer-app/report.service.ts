import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { supportNetworkHttpHeaders } from '../../support-network-headers';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { InterventionReport } from '../../models/alzheimer-app/report.model';
import { ReportCreateRequest } from '../../models/alzheimer-app/report-create-request.model';

const BASE = (environment as { supportNetworkApiUrl?: string }).supportNetworkApiUrl ?? '/api';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private readonly baseUrl = `${BASE}/reports`;

  constructor(private http: HttpClient) {}

  createReport(payload: ReportCreateRequest): Observable<InterventionReport> {
    return this.http.post<InterventionReport>(`${this.baseUrl}/create`, payload, {
      headers: supportNetworkHttpHeaders(),
    });
  }

  getReportsByMission(missionId: number): Observable<InterventionReport[]> {
    return this.http.get<InterventionReport[]>(`${this.baseUrl}/mission/${missionId}`, {
      headers: supportNetworkHttpHeaders(),
    });
  }
}
