import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { RendezVousService, RendezVous } from '../../services/rendez-vous.service';

interface CalendarDay {
  date: number;
  fullDate: Date;
  isCurrentMonth: boolean;
  isToday: boolean;
  isSelected: boolean;
  isWeekend: boolean;
  hasAppointment: boolean;
  appointments: RendezVous[];
}

@Component({
  selector: 'app-aidant-planning',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './aidant-planning.component.html',
  styleUrls: ['../aidant-pages.css', './aidant-planning.component.css']
})
export class AidantPlanningComponent implements OnInit {

  rendezVous: RendezVous[] = [];
  calendarDays: CalendarDay[] = [];
  currentMonth = '';
  displayMonth = 0;
  displayYear = 0;
  selectedDate: Date | null = null;
  selectedDayAppointments: RendezVous[] = [];

  dayNames = ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim'];

  get todayFormatted(): string {
    return new Date().toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long' });
  }

  get upcomingAppointments(): RendezVous[] {
    const now = new Date();
    return this.rendezVous
      .filter(r => r.dateHeure && new Date(r.dateHeure) >= now)
      .sort((a, b) => new Date(a.dateHeure!).getTime() - new Date(b.dateHeure!).getTime())
      .slice(0, 8);
  }

  constructor(private rendezVousService: RendezVousService) {}

  ngOnInit(): void {
    const now = new Date();
    this.displayMonth = now.getMonth();
    this.displayYear = now.getFullYear();

    this.rendezVousService.getAll().subscribe({
      next: (rdvs) => {
        this.rendezVous = rdvs;
        this.buildCalendar();
      },
      error: () => {
        this.loadMockRdv();
        this.buildCalendar();
      }
    });
  }

  private loadMockRdv(): void {
    const today = new Date();
    const tomorrow = new Date(today); tomorrow.setDate(today.getDate() + 1); tomorrow.setHours(10, 0);
    const nextWeek = new Date(today); nextWeek.setDate(today.getDate() + 5); nextWeek.setHours(14, 30);
    const nextWeek2 = new Date(today); nextWeek2.setDate(today.getDate() + 7); nextWeek2.setHours(9, 0);

    this.rendezVous = [
      { id: 1, type: 'CONSULTATION', statut: 'CONFIRME', dateHeure: today.toISOString(), lieu: 'Cabinet Dr. Lefebvre', motif: 'Suivi trimestriel', duree: 30 },
      { id: 2, type: 'SUIVI', statut: 'PLANIFIE', dateHeure: tomorrow.toISOString(), lieu: 'Domicile', motif: 'Visite soignant', duree: 45 },
      { id: 3, type: 'EVALUATION', statut: 'CONFIRME', dateHeure: nextWeek.toISOString(), lieu: 'Hôpital Saint-Louis', motif: 'Test cognitif', duree: 60 },
      { id: 4, type: 'CONSULTATION', statut: 'PLANIFIE', dateHeure: nextWeek2.toISOString(), lieu: 'Cabinet Dr. Moreau', motif: 'Renouvellement ordonnance', duree: 20 }
    ];
  }

  buildCalendar(): void {
    this.currentMonth = new Date(this.displayYear, this.displayMonth)
      .toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' });
    this.currentMonth = this.currentMonth.charAt(0).toUpperCase() + this.currentMonth.slice(1);

    const today = new Date(); today.setHours(0, 0, 0, 0);
    const firstDay = new Date(this.displayYear, this.displayMonth, 1);
    let startOffset = firstDay.getDay() - 1;
    if (startOffset < 0) startOffset = 6;
    const startDate = new Date(firstDay);
    startDate.setDate(startDate.getDate() - startOffset);

    this.calendarDays = [];
    for (let i = 0; i < 42; i++) {
      const d = new Date(startDate);
      d.setDate(startDate.getDate() + i);
      const dayOfWeek = d.getDay();
      const appointments = this.getAppointmentsForDate(d);

      this.calendarDays.push({
        date: d.getDate(),
        fullDate: new Date(d),
        isCurrentMonth: d.getMonth() === this.displayMonth,
        isToday: d.toDateString() === today.toDateString(),
        isSelected: false,
        isWeekend: dayOfWeek === 0 || dayOfWeek === 6,
        hasAppointment: appointments.length > 0,
        appointments
      });
    }
  }

  private getAppointmentsForDate(date: Date): RendezVous[] {
    return this.rendezVous.filter(r => {
      if (!r.dateHeure) return false;
      const rd = new Date(r.dateHeure);
      return rd.getFullYear() === date.getFullYear() &&
             rd.getMonth() === date.getMonth() &&
             rd.getDate() === date.getDate();
    });
  }

  prevMonth(): void {
    this.displayMonth--;
    if (this.displayMonth < 0) { this.displayMonth = 11; this.displayYear--; }
    this.buildCalendar();
  }

  nextMonth(): void {
    this.displayMonth++;
    if (this.displayMonth > 11) { this.displayMonth = 0; this.displayYear++; }
    this.buildCalendar();
  }

  goToToday(): void {
    const now = new Date();
    this.displayMonth = now.getMonth();
    this.displayYear = now.getFullYear();
    this.buildCalendar();
    this.selectToday();
  }

  selectDay(day: CalendarDay): void {
    this.calendarDays.forEach(d => d.isSelected = false);
    day.isSelected = true;
    this.selectedDate = day.fullDate;
    this.selectedDayAppointments = day.appointments;
  }

  private selectToday(): void {
    const todayCell = this.calendarDays.find(d => d.isToday);
    if (todayCell) this.selectDay(todayCell);
  }

  getTypeLabel(type: string): string {
    const map: Record<string, string> = {
      CONSULTATION: 'Consultation', SUIVI: 'Suivi', EVALUATION: 'Évaluation',
      URGENCE: 'Urgence', REEDUCATION: 'Rééducation'
    };
    return map[type] || type;
  }

  getStatutLabel(statut: string): string {
    const map: Record<string, string> = {
      PLANIFIE: 'Planifié', CONFIRME: 'Confirmé', TERMINE: 'Terminé', ANNULE: 'Annulé'
    };
    return map[statut] || statut;
  }

  getStatutClass(statut: string): string {
    const map: Record<string, string> = {
      PLANIFIE: 'badge-info', CONFIRME: 'badge-success', TERMINE: 'badge-purple', ANNULE: 'badge-danger'
    };
    return map[statut] || 'badge-info';
  }

  formatTime(dateHeure: string | undefined): string {
    if (!dateHeure) return '—';
    return new Date(dateHeure).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
  }

  formatDateShort(dateHeure: string | undefined): string {
    if (!dateHeure) return '—';
    return new Date(dateHeure).toLocaleDateString('fr-FR', { weekday: 'short', day: 'numeric', month: 'short' });
  }
}
