import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { InterventionResponse } from '../../../../../shared/models/intervention.dto';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-intervention-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="rounded-lg border bg-card p-4 md:p-6">
      <div class="flex items-start justify-between gap-4 mb-4">
        <div>
          <h1 class="text-3xl font-bold">{{ intervention.reference }}</h1>
          <p class="text-sm text-muted-foreground mt-1">{{ intervention.reportedIssue }}</p>
        </div>
        <span class="inline-block rounded-full px-3 py-1 text-xs font-medium {{ statusBadgeClass() }}">
          {{ statusLabel() }}
        </span>
      </div>

      <div class="grid gap-4 md:grid-cols-4 text-sm">
        <div>
          <span class="text-muted-foreground">Client</span>
          <p class="font-medium">
            <a [routerLink]="['/dashboard/clients', intervention.clientId]" class="text-blue-600 hover:underline">{{ intervention.clientName }}</a>
          </p>
        </div>
        <div>
          <span class="text-muted-foreground">Créée le</span>
          <p class="font-medium">{{ formatDate(intervention.createdAt) }}</p>
        </div>
        <div>
          <span class="text-muted-foreground">Planifiée le</span>
          <p class="font-medium">{{ formatDate(intervention.interventionDate) }}</p>
        </div>
        <div>
          <span class="text-muted-foreground">Technicien</span>
          <p class="font-medium">{{ intervention.assignedTo || '—' }}</p>
        </div>
      </div>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class InterventionHeaderComponent {
  @Input() intervention!: InterventionResponse;
  @Output() onStatusChange = new EventEmitter<string>();

  statusLabel(): string {
    const map: Record<string, string> = {
      'PENDING': 'Planifiée',
      'IN_PROGRESS': 'En cours',
      'COMPLETED': 'Terminée',
      'CANCELLED': 'Annulée',
    };
    return map[this.intervention.status] || 'Inconnu';
  }

  statusBadgeClass(): string {
    const map: Record<string, string> = {
      'PENDING': 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400',
      'IN_PROGRESS': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
      'COMPLETED': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      'CANCELLED': 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400',
    };
    return map[this.intervention.status] || 'bg-gray-100 text-gray-700';
  }

  formatDate(date: string | null): string {
    if (!date) return '—';
    return new Date(date).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }
}
