import { Component, Input , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InterventionRecord } from '../../technicians/components/schemas/technician.schema';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-technician-interventions-month',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="rounded-lg border bg-card p-4 md:p-6">
      <div class="text-sm font-medium mb-4">Interventions du mois</div>

      @if (interventions.length === 0) {
        <p class="text-xs text-muted-foreground">Aucune intervention ce mois-ci.</p>
      } @else {
        <div class="space-y-2">
          @for (intervention of interventions; track intervention.id) {
            <div class="flex items-center justify-between rounded-md border p-3 hover:bg-muted/50">
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-1">
                  <span class="text-xs rounded-full px-2 py-0.5 {{ statusBadgeClass(intervention.status) }}">
                    {{ statusLabel(intervention.status) }}
                  </span>
                </div>
                <div class="flex items-center gap-3 text-xs text-muted-foreground">
                  <span class="font-medium text-blue-600">{{ intervention.reference }}</span>
                  <span>{{ intervention.client }}</span>
                  <span>{{ intervention.type }}</span>
                  <span>{{ formatDate(intervention.date) }}</span>
                  <span>{{ intervention.duration }} min</span>
                </div>
              </div>
              @if (intervention.rating) {
                <div class="text-sm font-medium ml-2">⭐ {{ intervention.rating }}</div>
              }
            </div>
          }
        </div>
      }
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class TechnicianInterventionsMonthComponent {
  @Input() interventions: InterventionRecord[] = [];

  statusBadgeClass(status: string): string {
    const map: Record<string, string> = {
      'COMPLETED': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      'IN_PROGRESS': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
      'PENDING': 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400',
    };
    return map[status] || 'bg-gray-100 text-gray-700';
  }

  statusLabel(status: string): string {
    const map: Record<string, string> = { 'COMPLETED': 'Terminée', 'IN_PROGRESS': 'En cours', 'PENDING': 'Planifiée' };
    return map[status] || status;
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', { year: 'numeric', month: 'short', day: 'numeric' });
  }
}
