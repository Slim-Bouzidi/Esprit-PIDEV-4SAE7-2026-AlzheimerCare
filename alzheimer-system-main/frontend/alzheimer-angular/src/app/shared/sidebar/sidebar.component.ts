import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslateModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit, OnChanges {
  @Input() role: string = 'SOIGNANT';
  @Input() userName: string = 'Dr. Marie Martin';
  @Input() userRole: string = 'Médecin Chef';

  /** Badges pour le rôle SOIGNANT */
  @Input() alertesCount: number = 0;
  @Input() rapportsNonLusCount: number = 0;
  @Input() rapportHebdoNonEnvoye: boolean = false;
  @Input() notificationsCount: number = 0;

  menuItems: { label: string; icon: string; route: string }[] = [];
  currentLang = 'fr';

  constructor(private router: Router, private translate: TranslateService) { }

  ngOnInit() {
    this.currentLang = this.translate.currentLang || this.translate.defaultLang || 'fr';
    this.updateMenu();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['role']) {
      this.updateMenu();
    }
  }

  getBadgeCount(route: string): number {
    if (this.role !== 'SOIGNANT') return 0;
    if (route === '/soignant-rapports') return this.rapportsNonLusCount;
    return 0;
  }

  getBadgeType(route: string): 'default' | 'blue' | 'orange' {
    if (route === '/soignant-rapports') return 'blue';
    if (route === '/soignant-rapports-hebdo') return 'orange';
    return 'default';
  }

  getBadgeDot(route: string): boolean {
    if (this.role !== 'SOIGNANT') return false;
    if (route === '/soignant-rapports-hebdo') return this.rapportHebdoNonEnvoye;
    return false;
  }

  toggleLang(): void {
    this.currentLang = this.currentLang === 'fr' ? 'en' : 'fr';
    this.translate.use(this.currentLang);
    localStorage.setItem('lang', this.currentLang);
  }

  updateMenu() {
    switch (this.role) {
      case 'SOIGNANT':
        this.menuItems = [
          { label: 'SIDEBAR.HOME', icon: '🏠', route: '/soignant-dashboard' },
          { label: 'SIDEBAR.MY_PATIENTS', icon: '👥', route: '/soignant-patients' },
          { label: 'SIDEBAR.MEDICAL_REPORTS', icon: '📨', route: '/soignant-rapports' },
          { label: 'SIDEBAR.VISUAL_AGENDA', icon: '📅', route: '/soignant-agenda' },
          { label: 'SIDEBAR.WEEKLY_REPORTS', icon: '📊', route: '/soignant-rapports-hebdo' },
          { label: 'SIDEBAR.FICHES_TRANSMISSION', icon: '📋', route: '/soignant-fiches' },
        ];
        break;

      case 'DOCTEUR':
        this.menuItems = [
          { label: 'SIDEBAR.OVERVIEW', icon: '🩺', route: '/doctor-dashboard' },
          { label: 'SIDEBAR.MY_PATIENTS', icon: '👥', route: '/doctor-patients' },
          { label: 'SIDEBAR.APPOINTMENTS', icon: '📅', route: '/doctor-appointments' },
          { label: 'SIDEBAR.REPORTS_ASSESSMENTS', icon: '📝', route: '/doctor-reports' },
          { label: 'SIDEBAR.CREATE_FOLLOW_UP_REPORT', icon: '📋', route: '/doctor-report-create' },
          { label: 'SIDEBAR.SETTINGS', icon: '⚙️', route: '/doctor-settings' }
        ];
        break;
      case 'AIDANT':
        this.menuItems = [
          { label: 'SIDEBAR.HOME', icon: '🏠', route: '/aidant-dashboard' },
          { label: 'SIDEBAR.MY_PATIENTS', icon: '👥', route: '/aidant-patients' },
          { label: 'SIDEBAR.PLANNING', icon: '📅', route: '/aidant-planning' },
          { label: 'SIDEBAR.REPORTS', icon: '📝', route: '/aidant-rapports' }
        ];
        break;
      default:
        this.menuItems = [];
        break;
    }
  }
}
