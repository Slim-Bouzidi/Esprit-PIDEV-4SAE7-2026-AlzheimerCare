import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { RapportService, Rapport } from '../../services/rapport.service';

@Component({
  selector: 'app-aidant-rapports',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslateModule],
  templateUrl: './aidant-rapports.component.html',
  styleUrls: ['../aidant-pages.css', './aidant-rapports.component.css']
})
export class AidantRapportsComponent implements OnInit {

  rapports: Rapport[] = [];
  filteredRapports: Rapport[] = [];
  selectedRapport: Rapport | null = null;
  searchQuery = '';
  activeFilter = 'all';

  constructor(private rapportService: RapportService) {}

  ngOnInit(): void {
    this.rapportService.getAll().subscribe({
      next: (rapports) => {
        this.rapports = rapports.sort((a, b) =>
          new Date(b.dateGeneration || '').getTime() - new Date(a.dateGeneration || '').getTime()
        );
        this.filteredRapports = [...this.rapports];
      },
      error: () => this.loadMockRapports()
    });
  }

  private loadMockRapports(): void {
    const now = new Date();
    const weekAgo = new Date(now); weekAgo.setDate(now.getDate() - 7);
    const twoWeeksAgo = new Date(now); twoWeeksAgo.setDate(now.getDate() - 14);
    const monthAgo = new Date(now); monthAgo.setDate(now.getDate() - 30);

    this.rapports = [
      {
        id: 1, titre: 'Rapport hebdomadaire - Jean Dupont', typeRapport: 'HEBDOMADAIRE',
        dateGeneration: now.toISOString(), statut: 'NOUVEAU',
        contenuTexte: 'Le patient montre une stabilité cognitive cette semaine. L\'observance médicamenteuse est bonne (92%). Quelques épisodes de désorientation en soirée.',
        recommandations: 'Maintenir le traitement actuel. Surveiller les épisodes vespéraux. Proposer des activités de stimulation cognitive le matin.',
        tauxObservance: 92, nbAlertes: 1, nbInterventions: 3, qualiteSommeil: 7
      },
      {
        id: 2, titre: 'Suivi mensuel - Marie Martin', typeRapport: 'MENSUEL',
        dateGeneration: weekAgo.toISOString(), statut: 'LU',
        contenuTexte: 'Évolution stable. La patiente participe bien aux activités proposées. Bonne intégration des nouvelles routines de prise de médicaments.',
        recommandations: 'Continuer les exercices cognitifs quotidiens. Envisager une réévaluation de la posologie dans 2 semaines.',
        tauxObservance: 88, nbAlertes: 0, nbInterventions: 5, qualiteSommeil: 6
      },
      {
        id: 3, titre: 'Rapport post-consultation - Jean Dupont', typeRapport: 'CONSULTATION',
        dateGeneration: twoWeeksAgo.toISOString(), statut: 'LU',
        contenuTexte: 'Consultation trimestrielle effectuée. Les tests cognitifs MMSE montrent un score de 22/30, stable par rapport au trimestre précédent.',
        recommandations: 'Ajustement du traitement Donépézil : passage de 5mg à 10mg. Prévoir une prise de sang dans 15 jours.',
        tauxObservance: 85, nbAlertes: 2, nbInterventions: 4, qualiteSommeil: 5
      },
      {
        id: 4, titre: 'Bilan comportemental - Marie Martin', typeRapport: 'HEBDOMADAIRE',
        dateGeneration: monthAgo.toISOString(), statut: 'LU',
        contenuTexte: 'Semaine calme sans incident majeur. La patiente a bien dormi et a participé à toutes les activités programmées.',
        recommandations: 'Proposer des sorties accompagnées pour maintenir le lien social.',
        tauxObservance: 95, nbAlertes: 0, nbInterventions: 2, qualiteSommeil: 8
      }
    ];
    this.filteredRapports = [...this.rapports];
  }

  filterRapports(): void {
    let result = [...this.rapports];

    if (this.searchQuery.trim()) {
      const q = this.searchQuery.toLowerCase();
      result = result.filter(r =>
        (r.titre || '').toLowerCase().includes(q) ||
        (r.contenuTexte || '').toLowerCase().includes(q) ||
        (r.recommandations || '').toLowerCase().includes(q)
      );
    }

    if (this.activeFilter !== 'all') {
      result = result.filter(r => r.typeRapport === this.activeFilter);
    }

    this.filteredRapports = result;
  }

  setFilter(filter: string): void {
    this.activeFilter = filter;
    this.filterRapports();
  }

  selectRapport(rapport: Rapport): void {
    this.selectedRapport = rapport;
  }

  closeDetail(): void {
    this.selectedRapport = null;
  }

  getTypeLabel(type: string | undefined): string {
    const map: Record<string, string> = {
      HEBDOMADAIRE: 'Hebdomadaire', MENSUEL: 'Mensuel', CONSULTATION: 'Post-consultation', QUOTIDIEN: 'Quotidien'
    };
    return map[type || ''] || type || 'Rapport';
  }

  getTypeClass(type: string | undefined): string {
    const map: Record<string, string> = {
      HEBDOMADAIRE: 'badge-info', MENSUEL: 'badge-purple', CONSULTATION: 'badge-success', QUOTIDIEN: 'badge-warning'
    };
    return map[type || ''] || 'badge-info';
  }

  getStatutLabel(statut: string | undefined): string {
    return statut === 'NOUVEAU' ? 'Nouveau' : statut === 'LU' ? 'Lu' : statut || '—';
  }

  formatDate(date: string | undefined): string {
    if (!date) return '—';
    return new Date(date).toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' });
  }

  formatDateShort(date: string | undefined): string {
    if (!date) return '—';
    return new Date(date).toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' });
  }

  getObservanceClass(value: number | undefined): string {
    if (!value) return 'red';
    if (value >= 80) return 'green';
    if (value >= 60) return 'orange';
    return 'red';
  }
}
