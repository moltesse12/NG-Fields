import { Component, Input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-client-contacts',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="rounded-lg border bg-card p-4 md:p-6">
      <div class="text-sm font-medium mb-4">Contact principal</div>

      @if (contactName) {
        <p class="text-sm">{{ contactName }}</p>
      } @else {
        <p class="text-xs text-muted-foreground">Aucun contact renseigné.</p>
      }
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class ClientContactsComponent {
  @Input() contactName: string | null = null;
}
