import { Component, inject, ChangeDetectionStrategy, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { IconComponent } from '../../../../shared/ui/icon/icon.component';
import { InterventionService } from '../../../../core/services/intervention.service';
import { InterventionResponse } from '../../../../shared/models/intervention.dto';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-interventions-list',
  standalone: true,
  imports: [CommonModule, FormsModule, IconComponent],
  template: `
    <div class="space-y-4 p-4 md:p-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold">Interventions</h1>
          <p class="text-sm text-muted-foreground mt-1">Suivi des interventions terrain</p>
        </div>
        <button (click)="router.navigate(['/dashboard/interventions/new'])"
          class="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90">
          <app-icon name="circle-plus" /> Nouvelle intervention
        </button>
      </div>

      <div class="flex flex-wrap items-center gap-3">
        <select class="rounded-md border border-input bg-background px-3 py-2 text-sm"
          [ngModel]="statusFilter()" (ngModelChange)="statusFilter.set($event)">
          <option value="all">Tous les statuts</option>
          <option value="PLANNED">Planifiée</option>
          <option value="IN_PROGRESS">En cours</option>
          <option value="COMPLETED">Terminée</option>
          <option value="CANCELLED">Annulée</option>
        </select>
        <span class="ml-auto text-sm text-muted-foreground">{{ interventions().length }} intervention(s)</span>
      </div>

      @if (isLoading()) {
        <div class="h-64 rounded-lg bg-muted animate-pulse"></div>
      } @else if (error(); as err) {
        <div class="rounded-lg border border-red-200 bg-red-50 p-4 text-red-700">{{ err }}</div>
      } @else {
        <div class="rounded-lg border bg-card shadow-sm overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="border-b bg-muted/50 text-left">
                <th class="px-4 py-3 text-xs font-medium text-muted-foreground uppercase">Réf.</th>
                <th class="px-4 py-3 text-xs font-medium text-muted-foreground uppercase">Client</th>
                <th class="px-4 py-3 text-xs font-medium text-muted-foreground uppercase">Équipement</th>
                <th class="px-4 py-3 text-xs font-medium text-muted-foreground uppercase">Statut</th>
                <th class="px-4 py-3 text-xs font-medium text-muted-foreground uppercase">Date</th>
                <th class="px-4 py-3 text-xs font-medium text-muted-foreground uppercase">Technicien</th>
                <th class="px-4 py-3 text-xs font-medium text-muted-foreground uppercase text-right">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y">
              @for (inv of interventions(); track inv.id) {
                <tr class="hover:bg-muted/30 transition-colors cursor-pointer" (click)="router.navigate(['/dashboard/interventions', inv.id])">
                  <td class="px-4 py-3 font-medium">{{ inv.reference }}</td>
                  <td class="px-4 py-3">{{ inv.clientName || '—' }}</td>
                  <td class="px-4 py-3 text-muted-foreground">{{ inv.equipmentType || '—' }}</td>
                  <td class="px-4 py-3">
                    <span class="inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium"
                      [class.bg-blue-100]="inv.status === 'PLANNED'" [class.text-blue-800]="inv.status === 'PLANNED'"
                      [class.bg-yellow-100]="inv.status === 'IN_PROGRESS'" [class.text-yellow-800]="inv.status === 'IN_PROGRESS'"
                      [class.bg-green-100]="inv.status === 'COMPLETED'" [class.text-green-800]="inv.status === 'COMPLETED'"
                      [class.bg-gray-100]="inv.status === 'CANCELLED'" [class.text-gray-800]="inv.status === 'CANCELLED'"
                    >
                      @if (inv.status === 'PLANNED') { Planifiée }
                      @else if (inv.status === 'IN_PROGRESS') { En cours }
                      @else if (inv.status === 'COMPLETED') { Terminée }
                      @else if (inv.status === 'CANCELLED') { Annulée }
                      @else { {{ inv.status }} }
                    </span>
                  </td>
                  <td class="px-4 py-3 text-muted-foreground">{{ inv.interventionDate | date:'shortDate' }}</td>
                  <td class="px-4 py-3 text-muted-foreground">{{ inv.assignedTo || '—' }}</td>
                  <td class="px-4 py-3 text-right">
                    <button class="rounded-md border px-3 py-1.5 text-xs font-medium hover:bg-muted"
                      (click)="$event.stopPropagation(); router.navigate(['/dashboard/interventions', inv.id])">
                      Détails
                    </button>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      }
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class InterventionsListComponent implements OnInit {
  protected router = inject(Router);
  private interventionService = inject(InterventionService);

  statusFilter = signal('all');
  interventions = signal<InterventionResponse[]>([]);
  isLoading = signal(true);
  error = signal<string | null>(null);

  ngOnInit() {
    this.interventionService.getInterventions({ size: 100 }).subscribe({
      next: res => { this.interventions.set(res.content); this.isLoading.set(false); },
      error: () => { this.error.set('Erreur lors du chargement des interventions'); this.isLoading.set(false); },
    });
  }
}
