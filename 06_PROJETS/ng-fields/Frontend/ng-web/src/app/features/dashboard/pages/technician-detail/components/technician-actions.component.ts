import { Component, Input, Output, EventEmitter , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TechnicianDetailView } from '../../technicians/components/schemas/technician.schema';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-technician-actions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="rounded-lg border bg-card p-4 md:p-6">
      <div class="text-sm font-medium mb-4">Actions</div>

      <div class="space-y-2">
        <button class="w-full rounded-md bg-blue-100 px-3 py-2 text-xs font-medium text-blue-700 hover:bg-blue-200 dark:bg-blue-900/30 dark:text-blue-400 dark:hover:bg-blue-900/50">
          Assigner une intervention
        </button>

        @if (technician.status !== 'INACTIVE') {
          <button (click)="showStatusSelect = !showStatusSelect" class="w-full rounded-md bg-purple-100 px-3 py-2 text-xs font-medium text-purple-700 hover:bg-purple-200 dark:bg-purple-900/30 dark:text-purple-400 dark:hover:bg-purple-900/50">
            Changer le statut
          </button>
          @if (showStatusSelect) {
            <select [(ngModel)]="selectedStatus" (change)="changeStatus()" class="w-full rounded-md border bg-background p-2 text-xs">
              <option value="">Sélectionner un statut...</option>
              <option value="AVAILABLE">Disponible</option>
              <option value="BUSY">Occupé</option>
              <option value="ON_LEAVE">En congé</option>
              <option value="INACTIVE">Inactif</option>
            </select>
          }
        }

        <button class="w-full rounded-md bg-green-100 px-3 py-2 text-xs font-medium text-green-700 hover:bg-green-200 dark:bg-green-900/30 dark:text-green-400 dark:hover:bg-green-900/50">
          Éditer le profil
        </button>

        <button class="w-full rounded-md bg-gray-100 px-3 py-2 text-xs font-medium text-gray-700 hover:bg-gray-200 dark:bg-gray-900/30 dark:text-gray-400 dark:hover:bg-gray-900/50">
          Télécharger historique
        </button>

        <button class="w-full rounded-md bg-red-100 px-3 py-2 text-xs font-medium text-red-700 hover:bg-red-200 dark:bg-red-900/30 dark:text-red-400 dark:hover:bg-red-900/50">
          Supprimer le technicien
        </button>
      </div>

      <div class="mt-4 rounded-md bg-muted p-2 text-xs text-muted-foreground">
        <p>Status : <strong>{{ statusLabel() }}</strong></p>
        <p>Interventions : <strong>{{ technician.interventions.total }}</strong></p>
        <p>Évaluation : <strong>{{ technician.rating.average.toFixed(1) }}/5</strong></p>
      </div>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class TechnicianActionsComponent {
  @Input() technician!: TechnicianDetailView;
  @Output() onStatusChange = new EventEmitter<string>();

  showStatusSelect = false;
  selectedStatus = '';

  statusLabel(): string {
    const map: Record<string, string> = {
      'AVAILABLE': 'Disponible', 'BUSY': 'Occupé', 'ON_LEAVE': 'En congé', 'INACTIVE': 'Inactif',
    };
    return map[this.technician.status] || 'Inconnu';
  }

  changeStatus(): void {
    if (this.selectedStatus) {
      this.onStatusChange.emit(this.selectedStatus);
      this.showStatusSelect = false;
      this.selectedStatus = '';
    }
  }
}
