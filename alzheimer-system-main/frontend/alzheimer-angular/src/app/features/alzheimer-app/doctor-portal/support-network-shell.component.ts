import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent as SidebarPortalComponent } from '../../../shared/sidebar-portal/sidebar.component';

@Component({
  selector: 'app-support-network-shell',
  standalone: true,
  imports: [RouterOutlet, SidebarPortalComponent],
  template: `
    <div class="dashboard-layout">
      <app-sidebar-portal [role]="'DOCTEUR'" [userName]="'Dr. Doctor'" [userRole]="'Médecin Référent'"></app-sidebar-portal>
      <div class="main-wrapper">
        <router-outlet></router-outlet>
      </div>
    </div>
  `,
  styleUrls: ['./doctor-patients.component.css'],
})
export class SupportNetworkShellComponent {}

