import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { supportNetworkHttpHeaders } from '../../support-network-headers';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { NetworkDashboard } from '../../models/alzheimer-app/dashboard.model';

const BASE = (environment as { supportNetworkApiUrl?: string }).supportNetworkApiUrl ?? '/api';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly baseUrl = `${BASE}/dashboard`;

  constructor(private http: HttpClient) {}

  getDashboard(): Observable<NetworkDashboard> {
    console.log('Calling API:', `${this.baseUrl}/network`);
    return this.http.get<NetworkDashboard>(`${this.baseUrl}/network`, {
      headers: supportNetworkHttpHeaders(),
    });
  }
}
