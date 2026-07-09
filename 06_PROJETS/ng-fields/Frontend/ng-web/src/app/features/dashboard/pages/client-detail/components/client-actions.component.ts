import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClientResponse } from '../../../../../shared/models/client.dto';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-client-actions',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="rounded-lg border bg-card p-4 md:p-6">
      <div class="text-sm font-medium mb-4">Actions</div>

      <div class="space-y-2">
        <button
          class="w-full rounded-md bg-blue-100 px-3 py-2 text-xs font-medium text-blue-700 hover:bg-blue-200 dark:bg-blue-900/30 dark:text-blue-400 dark:hover:bg-blue-900/50"
        >
          + Nouvelle intervention
        </button>

        <button
          (click)="toggleActive()"
          class="w-full rounded-md bg-purple-100 px-3 py-2 text-xs font-medium text-purple-700 hover:bg-purple-200 dark:bg-purple-900/30 dark:text-purple-400 dark:hover:bg-purple-900/50"
        >
          {{ client.active ? 'Désactiver' : 'Activer' }} le client
        </button>

        <button
          class="w-full rounded-md bg-green-100 px-3 py-2 text-xs font-medium text-green-700 hover:bg-green-200 dark:bg-green-900/30 dark:text-green-400 dark:hover:bg-green-900/50"
        >
          Éditer les informations
        </button>

        <button
          class="w-full rounded-md bg-gray-100 px-3 py-2 text-xs font-medium text-gray-700 hover:bg-gray-200 dark:bg-gray-900/30 dark:text-gray-400 dark:hover:bg-gray-900/50"
        >
          Télécharger historique
        </button>

        <button
          class="w-full rounded-md bg-red-100 px-3 py-2 text-xs font-medium text-red-700 hover:bg-red-200 dark:bg-red-900/30 dark:text-red-400 dark:hover:bg-red-900/50"
        >
          Supprimer le client
        </button>
      </div>

      <div class="mt-4 rounded-md bg-muted p-2 text-xs text-muted-foreground">
        <p>Statut : <strong>{{ client.active ? 'Actif' : 'Inactif' }}</strong></p>
      </div>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class ClientActionsComponent {
  @Input() client!: ClientResponse;
  @Output() onStatusChange = new EventEmitter<boolean>();

  toggleActive(): void {
    this.onStatusChange.emit(!this.client.active);
  }
}
