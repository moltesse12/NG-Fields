import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InterventionResponse } from '../../../../../shared/models/intervention.dto';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-intervention-actions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="rounded-lg border bg-card p-4 md:p-6">
      <div class="text-sm font-medium mb-4">Actions</div>

      <div class="space-y-2">
        @if (intervention.status !== 'COMPLETED' && intervention.status !== 'CANCELLED') {
          <button (click)="showStatusSelect = !showStatusSelect" class="w-full rounded-md bg-blue-100 px-3 py-2 text-xs font-medium text-blue-700 hover:bg-blue-200 dark:bg-blue-900/30 dark:text-blue-400 dark:hover:bg-blue-900/50">
            Changer le statut
          </button>
          @if (showStatusSelect) {
            <select [(ngModel)]="selectedStatus" (change)="changeStatus()" class="w-full rounded-md border bg-background p-2 text-xs">
              <option value="">Sélectionner un statut...</option>
              @if (canChangeTo('PENDING')) { <option value="PENDING">Planifiée</option> }
              @if (canChangeTo('IN_PROGRESS')) { <option value="IN_PROGRESS">En cours</option> }
              @if (canChangeTo('COMPLETED')) { <option value="COMPLETED">Terminée</option> }
              @if (canChangeTo('CANCELLED')) { <option value="CANCELLED">Annulée</option> }
            </select>
          }
        }

        <button class="w-full rounded-md bg-green-100 px-3 py-2 text-xs font-medium text-green-700 hover:bg-green-200 dark:bg-green-900/30 dark:text-green-400 dark:hover:bg-green-900/50">
          Télécharger rapport
        </button>

        <button class="w-full rounded-md bg-gray-100 px-3 py-2 text-xs font-medium text-gray-700 hover:bg-gray-200 dark:bg-gray-900/30 dark:text-gray-400 dark:hover:bg-gray-900/50">
          Dupliquer
        </button>

        <button class="w-full rounded-md bg-red-100 px-3 py-2 text-xs font-medium text-red-700 hover:bg-red-200 dark:bg-red-900/30 dark:text-red-400 dark:hover:bg-red-900/50">
          Supprimer
        </button>
      </div>

      <div class="mt-4 rounded-md bg-muted p-2 text-xs text-muted-foreground">
        <p>Statut : <strong>{{ statusLabel() }}</strong></p>
        <p>Technicien : <strong>{{ intervention.assignedTo || '—' }}</strong></p>
      </div>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class InterventionActionsComponent {
  @Input() intervention!: InterventionResponse;
  @Output() onStatusChange = new EventEmitter<string>();

  showStatusSelect = false;
  selectedStatus = '';

  canChangeTo(status: string): boolean {
    return this.intervention.status !== status;
  }

  statusLabel(): string {
    const map: Record<string, string> = {
      'PENDING': 'Planifiée',
      'IN_PROGRESS': 'En cours',
      'COMPLETED': 'Terminée',
      'CANCELLED': 'Annulée',
    };
    return map[this.intervention.status] || 'Inconnu';
  }

  changeStatus(): void {
    if (this.selectedStatus) {
      this.onStatusChange.emit(this.selectedStatus);
      this.showStatusSelect = false;
      this.selectedStatus = '';
    }
  }
}
