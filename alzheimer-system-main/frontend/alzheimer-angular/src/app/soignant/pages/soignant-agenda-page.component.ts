import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { SoignantService } from '../soignant.service';
import { EvenementAgenda, StatutAgenda } from '../../models/agenda.model';
import { PatientSoignant } from '../../models/patient-soignant.model';
import { AgendaDailyViewComponent } from '../components/agenda-daily-view/agenda-daily-view.component';
import { AgendaWeeklyViewComponent } from '../components/agenda-weekly-view/agenda-weekly-view.component';
import { FicheTransmissionPanelComponent } from '../components/fiche-transmission-panel/fiche-transmission-panel.component';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-soignant-agenda-page',
  standalone: true,
  imports: [CommonModule, TranslateModule, AgendaDailyViewComponent, AgendaWeeklyViewComponent, FicheTransmissionPanelComponent],
  templateUrl: './soignant-agenda-page.component.html',
  styleUrls: ['./soignant-agenda-page.component.css']
})
export class SoignantAgendaPageComponent implements OnInit, OnDestroy {
  currentView: 'week' | 'day' = 'week';
  currentDate: Date = new Date();
  events: EvenementAgenda[] = [];

  // Patient panel and filtering properties
  patients: PatientSoignant[] = [];
  agendaFilterPatientId: string | null = null;
  selectedPatientId: string | null = null;
  selectedEventId: string | null = null;
  isPanelOpen: boolean = false;

  private sub: Subscription = new Subscription();

  constructor(private soignantService: SoignantService, private translate: TranslateService) { }

  ngOnInit(): void {
    this.patients = this.soignantService.getPatientsAssignes();
    // Charger dynamiquement l'agenda pour la semaine courante
    this.loadWeekEvents();
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  /** Charge les événements pour la semaine de currentDate depuis le backend */
  private loadWeekEvents(): void {
    this.soignantService.loadAgendaSemaine(this.currentDate);
    // Attendre un peu que le subject soit mis à jour, puis rafraîchir
    // On s'abonne au subject pour être réactif
    this.sub.unsubscribe();
    this.sub = new Subscription();
    const eventSub = this.soignantService.getEvenementsAgenda$().subscribe(events => {
      if (this.agendaFilterPatientId) {
        this.events = events.filter(e => e.patientId === this.agendaFilterPatientId);
      } else {
        this.events = events;
      }
    });
    this.sub.add(eventSub);
  }

  refreshEvents(): void {
    let allEvents = this.soignantService.getEvenementsSemaine();
    if (this.agendaFilterPatientId) {
      this.events = allEvents.filter(e => e.patientId === this.agendaFilterPatientId);
    } else {
      this.events = allEvents;
    }
  }

  filterByPatient(id: string | null): void {
    this.agendaFilterPatientId = id;
    this.refreshEvents();
  }

  onStatusChange(event: { eventId: string, status: StatutAgenda }): void {
    this.soignantService.marquerEvenementStatut(event.eventId, event.status).subscribe(() => {
      this.refreshEvents();
    });
  }

  switchView(view: 'week' | 'day'): void {
    this.currentView = view;
  }

  previous(): void {
    if (this.currentView === 'week') {
      this.currentDate.setDate(this.currentDate.getDate() - 7);
    } else {
      this.currentDate.setDate(this.currentDate.getDate() - 1);
    }
    this.currentDate = new Date(this.currentDate);
    this.loadWeekEvents();
  }

  next(): void {
    if (this.currentView === 'week') {
      this.currentDate.setDate(this.currentDate.getDate() + 7);
    } else {
      this.currentDate.setDate(this.currentDate.getDate() + 1);
    }
    this.currentDate = new Date(this.currentDate);
    this.loadWeekEvents();
  }

  get currentPeriodLabel(): string {
    // Simple label logic
    if (this.currentView === 'day') {
      return this.currentDate.toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long' });
    } else {
      const start = new Date(this.currentDate); // approximate logic for week start label
      // Logic to find monday
      const day = start.getDay();
      const diff = start.getDate() - day + (day === 0 ? -6 : 1);
      const monday = new Date(start.setDate(diff));
      return this.translate.instant('SOIGNANT.WEEK_OF') + ' ' + monday.toLocaleDateString('fr-FR', { day: 'numeric', month: 'long' });
    }
  }

  /**
   * Handle event click to select patient and open panel
   */
  onEventClick(event: EvenementAgenda): void {
    this.selectedPatientId = event.patientId;
    this.selectedEventId = event.id;
    this.isPanelOpen = true; // For tablet drawer mode
  }

  /**
   * Close panel drawer (for tablet/mobile)
   */
  closePanelDrawer(): void {
    this.isPanelOpen = false;
  }
}
