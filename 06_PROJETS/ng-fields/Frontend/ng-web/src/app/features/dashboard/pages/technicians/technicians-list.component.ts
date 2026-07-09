import { Component, OnInit, signal, ChangeDetectionStrategy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { TechnicianResponse } from '../../../../shared/models/technician.dto';
import { TechnicianService } from '../../../../core/services/technician.service';
import { TechniciansTableComponent } from './components/technicians-table.component';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-technicians-list',
  standalone: true,
  imports: [CommonModule, TechniciansTableComponent],
  templateUrl: './technicians-list.component.html',
  styleUrl: './technicians-list.component.css',
})
export class TechniciansListComponent implements OnInit {
  private technicianService = inject(TechnicianService);
  private router = inject(Router);

  technicians = signal<TechnicianResponse[]>([]);
  isLoading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.technicianService.getTechnicians(0, 100).subscribe({
      next: res => { this.technicians.set(res.content); this.isLoading.set(false); },
      error: () => { this.error.set('Erreur lors du chargement des techniciens'); this.isLoading.set(false); },
    });
  }

  onTechnicianClick(technicianId: string): void {
    this.router.navigate(['/dashboard/technicians', technicianId]);
  }

  onCreateTechnician(): void {
    this.router.navigate(['/dashboard/technicians/new']);
  }
}
