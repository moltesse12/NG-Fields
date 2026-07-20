import { Component, OnInit, signal , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { TechnicianDetailView } from '../technicians/components/schemas/technician.schema';
import { TechnicianService } from '../../../../core/services/technician.service';
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

  constructor(private route: ActivatedRoute, private technicianService: TechnicianService) {}

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

    this.technicianService.getTechnician(id).subscribe({
      next: (res) => {
        const detail: TechnicianDetailView = {
          id: 0,
          firstName: res.firstName,
          lastName: res.lastName,
          email: res.email,
          phone: res.phone || '',
          status: res.status,
          avatar: '',
          hireDate: res.createdAt,
          skills: res.skills || [],
          interventions: { thisMonth: 0, total: 0, avgDuration: 0 },
          rating: { average: 0, count: 0 },
          address: '',
          city: '',
          postalCode: '',
          country: '',
          department: '',
          manager: '',
          managerEmail: '',
          certifications: [],
          availability: {
            status: res.status as any,
            workingHours: {
              monday: '08:00-17:00', tuesday: '08:00-17:00', wednesday: '08:00-17:00',
              thursday: '08:00-17:00', friday: '08:00-17:00', saturday: '', sunday: '',
            },
          },
          interventionsThisMonth: [],
          interventionsHistory: [],
          ratings: [],
          notes: '',
        };
        this.technician.set(detail);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.error.set(err?.detail || 'Erreur lors du chargement du technicien');
        this.isLoading.set(false);
      },
    });
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
