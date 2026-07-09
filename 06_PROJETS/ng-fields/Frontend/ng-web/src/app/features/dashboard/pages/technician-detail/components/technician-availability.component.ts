import { Component, Input , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Availability } from '../../technicians/components/schemas/technician.schema';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-technician-availability',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="rounded-lg border bg-card p-4 md:p-6">
      <div class="text-sm font-medium mb-4">Disponibilité</div>

      <div class="mb-4 rounded-md bg-blue-50 dark:bg-blue-900/10 p-3 border border-blue-200 dark:border-blue-900">
        <div class="flex items-center justify-between">
          <span class="text-xs text-muted-foreground uppercase tracking-wide">Statut actuel</span>
          <span class="text-sm font-medium" [class.text-green-600]="availability.status === 'AVAILABLE'" [class.text-blue-600]="availability.status === 'BUSY'" [class.text-yellow-600]="availability.status === 'ON_LEAVE'">
            {{ statusLabel() }}
          </span>
        </div>
        @if (availability.currentTask) {
          <p class="text-xs mt-2 text-muted-foreground">Tâche actuelle : {{ availability.currentTask }}</p>
        }
        @if (availability.backUntil) {
          <p class="text-xs mt-2 text-muted-foreground">De retour : {{ formatDate(availability.backUntil) }}</p>
        }
      </div>

      <h3 class="text-xs font-medium text-muted-foreground uppercase mb-3">Horaires de travail</h3>
      <div class="space-y-1 text-sm">
        @for (day of weekDays; track day) {
          <div class="flex justify-between">
            <span>{{ dayLabel(day) }}</span>
            <span class="font-medium">{{ getHour(day) }}</span>
          </div>
        }
      </div>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class TechnicianAvailabilityComponent {
  @Input() availability!: Availability;

  weekDays = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday'];

  statusLabel(): string {
    const map: Record<string, string> = {
      'AVAILABLE': 'Disponible', 'BUSY': 'En intervention', 'ON_LEAVE': 'En congé',
    };
    return map[this.availability.status] || 'Inconnu';
  }

  dayLabel(day: string): string {
    const map: Record<string, string> = {
      'monday': 'Lundi', 'tuesday': 'Mardi', 'wednesday': 'Mercredi',
      'thursday': 'Jeudi', 'friday': 'Vendredi', 'saturday': 'Samedi', 'sunday': 'Dimanche',
    };
    return map[day] || day;
  }

  getHour(day: string): string {
    const hours = this.availability.workingHours as Record<string, string>;
    return hours[day] || '';
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', { year: 'numeric', month: 'short', day: 'numeric' });
  }
}
