import { Component, Input, Output, EventEmitter , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TechnicianDetailView } from '../../technicians/components/schemas/technician.schema';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-technician-header',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="rounded-lg border bg-card p-4 md:p-6">
      <div class="flex items-start justify-between gap-4 mb-4">
        <div class="flex items-center gap-4">
          <img [src]="technician.avatar" alt="{{ technician.firstName }}" class="size-20 rounded-full" />
          <div>
            <h1 class="text-3xl font-bold">{{ technician.firstName }} {{ technician.lastName }}</h1>
            <p class="text-sm text-muted-foreground mt-1">{{ technician.email }}</p>
          </div>
        </div>
        <span class="inline-block rounded-full px-3 py-1 text-xs font-medium {{ statusBadgeClass() }}">
          {{ statusLabel() }}
        </span>
      </div>

      <div class="grid gap-4 md:grid-cols-5 text-sm">
        <div>
          <span class="text-muted-foreground">Téléphone</span>
          <p class="font-medium"><a href="tel:{{ technician.phone }}" class="text-blue-600 hover:underline">{{ technician.phone }}</a></p>
        </div>
        <div>
          <span class="text-muted-foreground">Ville</span>
          <p class="font-medium">{{ technician.city }}</p>
        </div>
        <div>
          <span class="text-muted-foreground">Embauché le</span>
          <p class="font-medium">{{ formatDate(technician.hireDate) }}</p>
        </div>
        <div>
          <span class="text-muted-foreground">Interventions</span>
          <p class="font-medium">{{ technician.interventions.total }}</p>
        </div>
        <div>
          <span class="text-muted-foreground">Évaluation</span>
          <p class="font-medium">⭐ {{ technician.rating.average.toFixed(1) }}/5</p>
        </div>
      </div>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class TechnicianHeaderComponent {
  @Input() technician!: TechnicianDetailView;
  @Output() onStatusChange = new EventEmitter<string>();

  statusLabel(): string {
    const map: Record<string, string> = {
      'AVAILABLE': 'Disponible', 'BUSY': 'Occupé', 'ON_LEAVE': 'En congé', 'INACTIVE': 'Inactif',
    };
    return map[this.technician.status] || 'Inconnu';
  }

  statusBadgeClass(): string {
    const map: Record<string, string> = {
      'AVAILABLE': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      'BUSY': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
      'ON_LEAVE': 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400',
      'INACTIVE': 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400',
    };
    return map[this.technician.status] || 'bg-gray-100 text-gray-700';
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', { year: 'numeric', month: 'long', day: 'numeric' });
  }
}
