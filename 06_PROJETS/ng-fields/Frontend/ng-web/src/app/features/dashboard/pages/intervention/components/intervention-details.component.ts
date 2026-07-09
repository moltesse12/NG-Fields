import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { InterventionResponse } from '../../../../../shared/models/intervention.dto';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-intervention-details',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="rounded-lg border bg-card p-4 md:p-6">
      <div class="text-sm font-medium mb-4">Détails de l'intervention</div>

      <div class="grid gap-4 md:grid-cols-2">
        <div class="space-y-3">
          <div>
            <span class="text-xs text-muted-foreground uppercase tracking-wide">Client</span>
            <p class="font-medium">
              <a [routerLink]="['/dashboard/clients', intervention.clientId]" class="text-blue-600 hover:underline">{{ intervention.clientName }}</a>
            </p>
          </div>
          <div>
            <span class="text-xs text-muted-foreground uppercase tracking-wide">Adresse</span>
            <p class="text-sm">{{ intervention.clientAddress || '—' }}</p>
          </div>
          <div>
            <span class="text-xs text-muted-foreground uppercase tracking-wide">Email</span>
            <p class="text-sm"><a href="mailto:{{ intervention.clientEmail }}" class="text-blue-600 hover:underline dark:text-blue-400">{{ intervention.clientEmail }}</a></p>
          </div>
          <div>
            <span class="text-xs text-muted-foreground uppercase tracking-wide">Téléphone</span>
            <p class="text-sm">{{ intervention.clientPhone || '—' }}</p>
          </div>
        </div>

        <div class="space-y-3">
          <div>
            <span class="text-xs text-muted-foreground uppercase tracking-wide">Technicien assigné</span>
            <p class="font-medium text-sm">{{ intervention.assignedTo || '—' }}</p>
          </div>
          @if (intervention.equipmentType) {
            <div>
              <span class="text-xs text-muted-foreground uppercase tracking-wide">Équipement</span>
              <p class="text-sm">{{ intervention.equipmentType }}{{ intervention.equipmentBrand ? ' - ' + intervention.equipmentBrand : '' }}{{ intervention.equipmentModel ? ' / ' + intervention.equipmentModel : '' }}</p>
            </div>
          }
        </div>
      </div>

      @if (intervention.durationMinutes) {
        <div class="border-t mt-4 pt-4 grid gap-4 md:grid-cols-2">
          <div>
            <span class="text-xs text-muted-foreground uppercase tracking-wide">Durée</span>
            <p class="text-2xl font-bold">{{ intervention.durationMinutes }} min</p>
          </div>
        </div>
      }

      <div class="border-t mt-4 pt-4">
        <span class="text-xs text-muted-foreground uppercase tracking-wide block mb-3">Chronologie</span>
        <div class="space-y-2 text-sm">
          <div class="flex justify-between">
            <span>Créée le</span>
            <span class="font-medium">{{ formatDate(intervention.createdAt) }}</span>
          </div>
          <div class="flex justify-between">
            <span>Planifiée le</span>
            <span class="font-medium">{{ formatDate(intervention.interventionDate) }}</span>
          </div>
          @if (intervention.startTime) {
            <div class="flex justify-between">
              <span>Commencée le</span>
              <span class="font-medium">{{ formatDate(intervention.startTime) }}</span>
            </div>
          }
          @if (intervention.endTime) {
            <div class="flex justify-between">
              <span>Terminée le</span>
              <span class="font-medium">{{ formatDate(intervention.endTime) }}</span>
            </div>
          }
        </div>
      </div>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class InterventionDetailsComponent {
  @Input() intervention!: InterventionResponse;

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
