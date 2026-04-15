import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { RapportHebdomadaire } from '../../models/rapport-hebdo.model';
import { RapportHebdomadaireApiService } from '../../services/rapport-hebdomadaire-api.service';

@Component({
  selector: 'app-soignant-rapports-hebdo-page',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslateModule],
  templateUrl: './soignant-rapports-hebdo-page.component.html',
  styleUrls: ['../soignant-pages.css', './soignant-rapports-hebdo-page.component.css']
})
export class SoignantRapportsHebdoPageComponent implements OnInit {
  rapports: RapportHebdomadaire[] = [];
  rapportsToSend: RapportHebdomadaire[] = [];
  rapportsSent: RapportHebdomadaire[] = [];

  constructor(private rapportHebdoApi: RapportHebdomadaireApiService, private router: Router) {}

  ngOnInit(): void {
    this.load();
  }

  envoyer(id: string): void {
    const numId = parseInt(id, 10);
    if (isNaN(numId)) return;

    this.rapportHebdoApi.marquerEnvoye(numId).subscribe({
      next: () => this.load(),
      error: (err) => console.error('Erreur envoi rapport hebdo:', err)
    });
  }

  private load(): void {
    this.rapportHebdoApi.getAll().subscribe({
      next: (data) => {
        const all = (data || []) as any[];
        // Show ALL weekly reports stored in DB, split by send status
        this.rapports = all as RapportHebdomadaire[];
        this.rapportsToSend = all.filter((r: any) => r?.envoyeAuMedecin !== true) as RapportHebdomadaire[];
        this.rapportsSent = all.filter((r: any) => r?.envoyeAuMedecin === true) as RapportHebdomadaire[];
      },
      error: (err) => console.error('Erreur chargement rapports hebdo:', err)
    });
  }

  logout(): void { import('../../keycloak').then(m => m.default.logout({ redirectUri: window.location.origin })); }
}
