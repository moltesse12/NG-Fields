import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClientResponse } from '../../../../../shared/models/client.dto';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-client-header',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="rounded-lg border bg-card p-4 md:p-6">
      <div class="flex items-start justify-between gap-4 mb-4">
        <div>
          <h1 class="text-3xl font-bold">{{ client.companyName }}</h1>
        </div>
        <span class="inline-block rounded-full px-3 py-1 text-xs font-medium {{ statusBadgeClass() }}">
          {{ statusLabel() }}
        </span>
      </div>

      <div class="grid gap-4 md:grid-cols-4 text-sm">
        <div>
          <span class="text-muted-foreground">Email</span>
          <p class="font-medium"><a href="mailto:{{ client.email }}" class="text-blue-600 hover:underline">{{ client.email }}</a></p>
        </div>
        <div>
          <span class="text-muted-foreground">Téléphone</span>
          <p class="font-medium"><a href="tel:{{ client.phone }}" class="text-blue-600 hover:underline">{{ client.phone }}</a></p>
        </div>
        <div>
          <span class="text-muted-foreground">Client depuis</span>
          <p class="font-medium">{{ formatDate(client.createdAt) }}</p>
        </div>
        <div>
          <span class="text-muted-foreground">Référence</span>
          <p class="font-medium font-mono text-xs">{{ client.reference }}</p>
        </div>
      </div>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class ClientHeaderComponent {
  @Input() client!: ClientResponse;
  @Output() onStatusChange = new EventEmitter<boolean>();

  statusLabel(): string {
    return this.client.active ? 'Actif' : 'Inactif';
  }

  statusBadgeClass(): string {
    return this.client.active
      ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400'
      : 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  }
}
