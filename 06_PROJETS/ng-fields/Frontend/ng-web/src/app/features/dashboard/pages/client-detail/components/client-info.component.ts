import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClientResponse } from '../../../../../shared/models/client.dto';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-client-info',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="rounded-lg border bg-card p-4 md:p-6">
      <div class="text-sm font-medium mb-4">Informations générales</div>

      <div class="grid gap-4 md:grid-cols-2">
        <div>
          <span class="text-xs text-muted-foreground uppercase tracking-wide">Adresse</span>
          <p class="text-sm mt-1">{{ client.address || '—' }}</p>
        </div>
        <div>
          <span class="text-xs text-muted-foreground uppercase tracking-wide">Email</span>
          <p class="text-sm mt-1"><a [href]="'mailto:' + client.email" class="text-blue-600 hover:underline">{{ client.email }}</a></p>
        </div>
        <div>
          <span class="text-xs text-muted-foreground uppercase tracking-wide">Téléphone</span>
          <p class="text-sm mt-1">{{ client.phone || '—' }}</p>
        </div>
        <div>
          <span class="text-xs text-muted-foreground uppercase tracking-wide">Contact</span>
          <p class="text-sm mt-1">{{ client.contactName || '—' }}</p>
        </div>
      </div>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class ClientInfoComponent {
  @Input() client!: ClientResponse;
}
