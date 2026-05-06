import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { translateOrFallback } from '../../../i18n-fallback';
import { DashboardService } from '../dashboard.service';
import { NetworkDashboard } from '../../../models/alzheimer-app/dashboard.model';
import { getSupportNetworkHttpErrorMessage } from '../../../support-network-http-error';
import { WebSocketService } from '../websocket.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-network-dashboard-page',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslateModule],
  templateUrl: './network-dashboard-page.component.html',
  styleUrls: [
    '../../../../features/alzheimer-app/doctor-portal/doctor-patients.component.css',
    '../../../../features/alzheimer-app/doctor-portal/doctor-dashboard.component.css',
    './network-dashboard-page.component.scss',
  ],
})
export class NetworkDashboardPageComponent implements OnInit, OnDestroy {
  loading = true;
  error: string | null = null;
  data: NetworkDashboard | null = null;
  private wsSubscriptions: Subscription[] = [];

  constructor(
    private dashboardService: DashboardService,
    private translate: TranslateService,
    private websocketService: WebSocketService
  ) {}

  ngOnInit(): void {
    this.refresh();
    this.wsSubscriptions.push(
      this.websocketService.onMissionUpdate().subscribe((event) => {
        console.log('🔄 WS Mission update:', event);
        console.log('Dashboard updated');
        this.refresh();
      })
    );
    this.wsSubscriptions.push(
      this.websocketService.onNotification().subscribe((event) => {
        console.log('📩 WS notification:', event);
        const txt =
          event && typeof event === 'object' && 'message' in (event as Record<string, unknown>)
            ? String((event as Record<string, unknown>)['message'] ?? 'New notification')
            : 'New notification';
        console.log(txt || 'New notification');
      })
    );
    this.wsSubscriptions.push(
      this.websocketService.onDispatchUpdate().subscribe((event) => {
        console.log('📩 WS dispatch update:', event);
        console.log('Dispatch updated');
        this.refresh();
      })
    );
  }

  ngOnDestroy(): void {
    this.wsSubscriptions.forEach((s) => s.unsubscribe());
    this.wsSubscriptions = [];
  }

  refresh(): void {
    this.loading = true;
    this.error = null;
    this.dashboardService.getDashboard().subscribe({
      next: (d) => {
        this.data = d;
        this.bindRealtimeFromDashboardData(d);
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.data = null;
        this.loading = false;
        this.error = getSupportNetworkHttpErrorMessage(
          err,
          this.translate,
          translateOrFallback(this.translate, 'NETWORK_DASHBOARD.ERR_LOAD_GENERIC', 'Unable to load data.')
        );
      },
    });
  }

  zoneEntries(): { zone: string; count: number }[] {
    if (!this.data?.missionsPerZone) {
      return [];
    }
    return Object.entries(this.data.missionsPerZone).map(([zone, count]) => ({ zone, count }));
  }

  private bindRealtimeFromDashboardData(d: NetworkDashboard): void {
    (d.topIntervenants ?? []).forEach((row) => {
      if (row.memberId != null) {
        this.websocketService.watchMissions(row.memberId);
        this.websocketService.watchNotifications(row.memberId);
      }
    });
  }
}
