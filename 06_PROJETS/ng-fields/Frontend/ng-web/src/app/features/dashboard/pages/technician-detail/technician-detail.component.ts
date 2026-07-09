import { Component, OnInit, signal , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { TechnicianDetailView } from '../technicians/components/schemas/technician.schema';
import mockDetailData from './components/mocks/technician-detail-mock.json';
import { TechnicianHeaderComponent } from './components/technician-header.component';
import { TechnicianInfoComponent } from './components/technician-info.component';
import { TechnicianSkillsComponent } from './components/technician-skills.component';
import { TechnicianAvailabilityComponent } from './components/technician-availability.component';
import { TechnicianInterventionsMonthComponent } from './components/technician-interventions-month.component';
import { TechnicianInterventionsHistoryComponent } from './components/technician-interventions-history.component';
import { TechnicianRatingsComponent } from './components/technician-ratings.component';
import { TechnicianNotesComponent } from './components/technician-notes.component';
import { TechnicianActionsComponent } from './components/technician-actions.component';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-technician-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    TechnicianHeaderComponent,
    TechnicianInfoComponent,
    TechnicianSkillsComponent,
    TechnicianAvailabilityComponent,
    TechnicianInterventionsMonthComponent,
    TechnicianInterventionsHistoryComponent,
    TechnicianRatingsComponent,
    TechnicianNotesComponent,
    TechnicianActionsComponent,
  ],
  templateUrl: './technician-detail.component.html',
  styleUrl: './technician-detail.component.css',
})
export class TechnicianDetailComponent implements OnInit {
  technician = signal<TechnicianDetailView | null>(null);
  isLoading = signal(true);
  error = signal<string | null>(null);

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      this.loadTechnician(id);
    });
  }

  private loadTechnician(id: string | null): void {
    if (!id) {
      this.error.set('ID technicien manquant');
      this.isLoading.set(false);
      return;
    }

    setTimeout(() => {
      try {
        this.technician.set(mockDetailData as unknown as TechnicianDetailView);
        this.isLoading.set(false);
      } catch {
        this.error.set('Erreur lors du chargement du technicien');
        this.isLoading.set(false);
      }
    }, 300);
  }

  onStatusChange(newStatus: string): void {
    const current = this.technician();
    if (!current) return;
    current.status = newStatus as any;
    this.technician.set({ ...current });
  }

  onNotesUpdate(newNotes: string): void {
    const current = this.technician();
    if (!current) return;
    current.notes = newNotes;
    this.technician.set({ ...current });
  }
}
