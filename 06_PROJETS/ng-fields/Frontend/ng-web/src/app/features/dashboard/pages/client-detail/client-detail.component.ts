import { Component, OnInit, DestroyRef, signal, computed, inject, ChangeDetectionStrategy } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { RouterModule } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ClientService } from '../../../../core/services/client.service';
import { InterventionService } from '../../../../core/services/intervention.service';
import { ClientResponse } from '../../../../shared/models/client.dto';
import { InterventionResponse } from '../../../../shared/models/intervention.dto';
import { ClientHeaderComponent } from './components/client-header.component';
import { ClientInfoComponent } from './components/client-info.component';
import { ClientInterventionsHistoryComponent } from './components/client-interventions-history.component';
import { ClientInterventionsUpcomingComponent } from './components/client-interventions-upcoming.component';
import { ClientContactsComponent } from './components/client-contacts.component';
import { ClientNotesComponent } from './components/client-notes.component';
import { ClientActionsComponent } from './components/client-actions.component';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-client-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ClientHeaderComponent,
    ClientInfoComponent,
    ClientInterventionsHistoryComponent,
    ClientInterventionsUpcomingComponent,
    ClientContactsComponent,
    ClientNotesComponent,
    ClientActionsComponent,
  ],
  templateUrl: './client-detail.component.html',
  styleUrl: './client-detail.component.css',
})
export class ClientDetailComponent implements OnInit {
  private destroyRef = inject(DestroyRef);
  private route = inject(ActivatedRoute);
  private clientService = inject(ClientService);
  private interventionService = inject(InterventionService);

  client = signal<ClientResponse | null>(null);
  interventions = signal<InterventionResponse[]>([]);
  notes = signal<string>('');
  isLoading = signal(true);
  error = signal<string | null>(null);

  historyInterventions = computed(() =>
    this.interventions().filter(i => i.status === 'COMPLETED'),
  );
  upcomingInterventions = computed(() =>
    this.interventions().filter(i => i.status !== 'COMPLETED'),
  );

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(params => {
      const id = params.get('id');
      if (id) this.loadClient(id);
    });
  }

  private loadClient(id: string): void {
    this.isLoading.set(true);
    this.error.set(null);

    forkJoin({
      client: this.clientService.getClient(id),
      interventions: this.interventionService.getInterventionsByClient(id),
    }).subscribe({
      next: ({ client, interventions }) => {
        this.client.set(client);
        this.interventions.set(interventions);
        this.isLoading.set(false);
      },
      error: () => {
        this.error.set('Erreur lors du chargement du client');
        this.isLoading.set(false);
      },
    });
  }

  onStatusChange(active: boolean): void {
    const current = this.client();
    if (!current) return;
    this.client.set({ ...current, active });
  }

  onNotesUpdate(newNotes: string): void {
    this.notes.set(newNotes);
  }
}
