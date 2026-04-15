import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DoctorDashboardSimpleComponent } from './doctor/doctor-dashboard-simple.component';
import { DoctorSettingsComponent } from './doctor/doctor-settings.component';
import { DoctorReportsComponent } from './doctor/doctor-reports.component';
import { DoctorReportCreateComponent } from './doctor/doctor-report-create.component';
import { DoctorPatientsComponent } from './doctor/doctor-patients.component';
import { DoctorAppointmentsComponent } from './doctor/doctor-appointments.component';
import { MedicalAgendaComponent } from './doctor/medical-agenda.component';
import { SoignantLayoutComponent } from './soignant/soignant-layout.component';
import { SoignantDashboardComponent } from './soignant/soignant-dashboard.component';
import { SoignantPatientsPageComponent } from './soignant/pages/soignant-patients-page.component';
import { SoignantRapportsPageComponent } from './soignant/pages/soignant-rapports-page.component';
import { SoignantAgendaPageComponent } from './soignant/pages/soignant-agenda-page.component';
import { SoignantRapportsHebdoPageComponent } from './soignant/pages/soignant-rapports-hebdo-page.component';
import { SoignantFichesPageComponent } from './soignant/pages/soignant-fiches-page.component';
import { FicheTransmissionHebdoComponent } from './soignant/pages/fiche-transmission-hebdo.component';
import { SoignantNotificationsPageComponent } from './soignant/pages/soignant-notifications-page.component';
import { SoignantParametresPageComponent } from './soignant/pages/soignant-parametres-page.component';
import { SoignantProfilPageComponent } from './soignant/pages/soignant-profil-page.component';
import { AidantLayoutComponent } from './aidant/aidant-layout.component';
import { AidantDashboardComponent } from './aidant/pages/aidant-dashboard.component';
import { AidantPatientsComponent } from './aidant/pages/aidant-patients.component';
import { AidantPlanningComponent } from './aidant/pages/aidant-planning.component';
import { AidantRapportsComponent } from './aidant/pages/aidant-rapports.component';
import { AppShellComponent } from './layout/app-shell/app-shell.component';
import { roleRedirectGuard } from './core/guards/role-redirect.guard';
import { roleGuard } from './core/guards/role.guard';

const routes: Routes = [
  // Default route: auto-redirect based on Keycloak role
  { path: '', canActivate: [roleRedirectGuard], children: [], pathMatch: 'full' },

  // ═══════ ADMIN routes (AppShellComponent layout) ═══════
  {
    path: 'admin',
    component: AppShellComponent,
    canActivate: [roleGuard],
    data: { role: 'ADMIN' },
    children: [
      { path: '', redirectTo: 'manage-users', pathMatch: 'full' },
      {
        path: 'manage-users',
        loadComponent: () =>
          import('./features/manage-users/manage-users.component').then(m => m.ManageUsersComponent),
      },
      {
        path: 'user-types',
        loadComponent: () =>
          import('./features/user-types/user-type-list/user-type-list.component').then(m => m.UserTypeListComponent),
      },
      {
        path: 'patients',
        loadComponent: () =>
          import('./features/patients/patient-list/patient-list.component').then(m => m.PatientListComponent),
      },
      {
        path: 'appointments',
        loadComponent: () =>
          import('./shared/components/placeholder/placeholder.component').then(m => m.PlaceholderComponent),
        data: { title: 'Appointments', icon: 'pi-calendar' }
      },
      {
        path: 'clinical-reports',
        children: [
          {
            path: 'new',
            loadComponent: () =>
              import('./features/clinical-reports/clinical-form/clinical-form.component').then(m => m.ClinicalFormComponent),
            data: { title: 'New Report', icon: 'pi-plus' }
          },
          {
            path: 'history',
            loadComponent: () =>
              import('./features/clinical-reports/report-list/report-list.component').then(m => m.ReportListComponent),
            data: { title: 'Report History', icon: 'pi-list' }
          }
        ]
      },
      {
        path: 'settings',
        loadComponent: () =>
          import('./shared/components/placeholder/placeholder.component').then(m => m.PlaceholderComponent),
        data: { title: 'System Settings', icon: 'pi-cog' }
      },
      {
        path: 'patient-dashboard',
        loadComponent: () =>
          import('./features/patient-dashboard/patient-dashboard.component').then(m => m.PatientDashboardComponent),
        data: { title: 'Patient Workspace', icon: 'pi-home' }
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/profile/profile.component').then(m => m.ProfileComponent),
        data: { title: 'My Profile', icon: 'pi-user' }
      }
    ]
  },

  // ═══════ SOIGNANT routes ═══════
  { path: 'soignant-dashboard', component: SoignantLayoutComponent, canActivate: [roleGuard], data: { role: 'SOIGNANT' }, children: [{ path: '', component: SoignantDashboardComponent }] },
  { path: 'soignant-patients', component: SoignantLayoutComponent, canActivate: [roleGuard], data: { role: 'SOIGNANT' }, children: [{ path: '', component: SoignantPatientsPageComponent }] },
  { path: 'soignant-rapports', component: SoignantLayoutComponent, canActivate: [roleGuard], data: { role: 'SOIGNANT' }, children: [{ path: '', component: SoignantRapportsPageComponent }] },
  { path: 'soignant-agenda', component: SoignantLayoutComponent, canActivate: [roleGuard], data: { role: 'SOIGNANT' }, children: [{ path: '', component: SoignantAgendaPageComponent }] },
  { path: 'soignant-rapports-hebdo', component: SoignantLayoutComponent, canActivate: [roleGuard], data: { role: 'SOIGNANT' }, children: [{ path: '', component: SoignantRapportsHebdoPageComponent }] },
  { path: 'soignant-fiches', component: SoignantLayoutComponent, canActivate: [roleGuard], data: { role: 'SOIGNANT' }, children: [{ path: '', component: SoignantFichesPageComponent }] },
  { path: 'soignant-fiche-transmission/:patientId', component: SoignantLayoutComponent, canActivate: [roleGuard], data: { role: 'SOIGNANT' }, children: [{ path: '', component: FicheTransmissionHebdoComponent }] },
  { path: 'soignant-notifications', component: SoignantLayoutComponent, canActivate: [roleGuard], data: { role: 'SOIGNANT' }, children: [{ path: '', component: SoignantNotificationsPageComponent }] },
  { path: 'soignant-parametres', component: SoignantLayoutComponent, canActivate: [roleGuard], data: { role: 'SOIGNANT' }, children: [{ path: '', component: SoignantParametresPageComponent }] },
  { path: 'soignant-profil', component: SoignantLayoutComponent, canActivate: [roleGuard], data: { role: 'SOIGNANT' }, children: [{ path: '', component: SoignantProfilPageComponent }] },

  // ═══════ AIDANT routes ═══════
  { path: 'aidant-dashboard', component: AidantLayoutComponent, canActivate: [roleGuard], data: { role: 'AIDANT' }, children: [{ path: '', component: AidantDashboardComponent }] },
  { path: 'aidant-patients', component: AidantLayoutComponent, canActivate: [roleGuard], data: { role: 'AIDANT' }, children: [{ path: '', component: AidantPatientsComponent }] },
  { path: 'aidant-planning', component: AidantLayoutComponent, canActivate: [roleGuard], data: { role: 'AIDANT' }, children: [{ path: '', component: AidantPlanningComponent }] },
  { path: 'aidant-rapports', component: AidantLayoutComponent, canActivate: [roleGuard], data: { role: 'AIDANT' }, children: [{ path: '', component: AidantRapportsComponent }] },

  // ═══════ MEDECIN routes ═══════
  { path: 'doctor-dashboard', component: DoctorDashboardSimpleComponent, canActivate: [roleGuard], data: { role: 'MEDECIN' } },
  { path: 'doctor-settings', component: DoctorSettingsComponent, canActivate: [roleGuard], data: { role: 'MEDECIN' } },
  { path: 'doctor-reports', component: DoctorReportsComponent, canActivate: [roleGuard], data: { role: 'MEDECIN' } },
  { path: 'doctor-report-create', component: DoctorReportCreateComponent, canActivate: [roleGuard], data: { role: 'MEDECIN' } },
  { path: 'doctor-patients', component: DoctorPatientsComponent, canActivate: [roleGuard], data: { role: 'MEDECIN' } },
  { path: 'doctor-appointments', component: DoctorAppointmentsComponent, canActivate: [roleGuard], data: { role: 'MEDECIN' } },
  { path: 'medical-agenda', component: MedicalAgendaComponent, canActivate: [roleGuard], data: { role: 'MEDECIN' } },

  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
