import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent as SidebarPortalComponent } from '../../../shared/sidebar-portal/sidebar.component';

@Component({
  selector: 'app-support-network-shell',
  standalone: true,
  imports: [RouterOutlet, SidebarPortalComponent],
  template: `
    <div class="sn-shell-layout">
      <app-sidebar-portal [role]="'DOCTEUR'" [userName]="'Dr. Doctor'" [userRole]="'Médecin Référent'"></app-sidebar-portal>
      <div class="sn-shell-main">
        <router-outlet></router-outlet>
      </div>
    </div>
  `,
  styles: [`
    .sn-shell-layout {
      display: flex;
      height: 100vh;
      background: #f8fafc;
      font-family: 'Inter', -apple-system, sans-serif;
      overflow: hidden;
    }
    .sn-shell-main {
      flex: 1;
      display: flex;
      flex-direction: column;
      overflow-y: auto;
      overflow-x: hidden;
      min-width: 0;
      background: #f8fafc;
    }
  `],
})
export class SupportNetworkShellComponent {}

