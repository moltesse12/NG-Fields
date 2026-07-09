import { Component, OnInit, DestroyRef, signal, inject, ChangeDetectionStrategy } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { InterventionService } from '../../../../core/services/intervention.service';
import { InterventionResponse } from '../../../../shared/models/intervention.dto';
import { InterventionHeaderComponent } from './components/intervention-header.component';
import { InterventionDetailsComponent } from './components/intervention-details.component';
import { InterventionNotesComponent } from './components/intervention-notes.component';
import { InterventionActionsComponent } from './components/intervention-actions.component';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-intervention',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    InterventionHeaderComponent,
    InterventionDetailsComponent,
    InterventionNotesComponent,
    InterventionActionsComponent,
  ],
  templateUrl: './intervention.component.html',
  styleUrl: './intervention.component.css',
})
export class InterventionComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private route = inject(ActivatedRoute);
  private interventionService = inject(InterventionService);

  intervention = signal<InterventionResponse | null>(null);
  isLoading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(params => {
      const id = params.get('id');
      if (id) this.loadIntervention(id);
    });
  }

  private loadIntervention(id: string): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.interventionService.getIntervention(id).subscribe({
      next: data => {
        this.intervention.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.error.set("Erreur lors du chargement de l'intervention");
        this.isLoading.set(false);
      },
    });
  }

  onStatusChange(newStatus: string): void {
    const current = this.intervention();
    if (!current) return;
    this.intervention.set({ ...current, status: newStatus });
  }

  onNoteAdded(text: string): void {
    const current = this.intervention();
    if (!current) return;
    this.intervention.set({ ...current, notes: text });
  }
}
