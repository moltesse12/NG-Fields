import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InterventionResponse } from '../../../../../shared/models/intervention.dto';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-client-interventions-upcoming',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="rounded-lg border bg-card p-4 md:p-6">
      <div class="text-sm font-medium mb-4">Interventions prévues</div>

      @if (interventions.length === 0) {
        <p class="text-xs text-muted-foreground">Aucune intervention prévue.</p>
      } @else {
        <div class="space-y-2">
          @for (intervention of interventions; track intervention.id) {
            <div class="flex items-center justify-between rounded-md border p-3 hover:bg-muted/50 bg-yellow-50/30 dark:bg-yellow-900/5">
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-1">
                  <span class="text-xs rounded-full px-2 py-0.5 bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400">
                    {{ intervention.status }}
                  </span>
                </div>
                <div class="flex items-center gap-3 text-xs text-muted-foreground">
                  <span class="font-medium text-blue-600">{{ intervention.reference }}</span>
                  <span>{{ intervention.assignedTo }}</span>
                  <span>{{ formatDate(intervention.interventionDate) }}</span>
                  @if (intervention.durationMinutes) {
                    <span>{{ intervention.durationMinutes }} min</span>
                  }
                </div>
              </div>
            </div>
          }
        </div>
      }
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class ClientInterventionsUpcomingComponent {
  @Input() interventions: InterventionResponse[] = [];

  formatDate(date: string | null): string {
    if (!date) return '—';
    return new Date(date).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }
}
