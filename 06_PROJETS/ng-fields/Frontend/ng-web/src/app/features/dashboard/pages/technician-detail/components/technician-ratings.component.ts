import { Component, Input , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClientRating } from '../../technicians/components/schemas/technician.schema';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-technician-ratings',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="rounded-lg border bg-card p-4 md:p-6">
      <div class="text-sm font-medium mb-4">Évaluations clients</div>

      <div class="rounded-md bg-blue-50 dark:bg-blue-900/10 p-4 mb-4 border border-blue-200 dark:border-blue-900">
        <div class="text-center">
          <div class="text-4xl font-bold">{{ average.toFixed(1) }}/5</div>
          <div class="flex items-center justify-center gap-1 mt-2">
            @for (i of [1, 2, 3, 4, 5]; track i) {
              <span [class.text-yellow-400]="i <= Math.round(average)" [class.text-gray-300]="i > Math.round(average)">★</span>
            }
          </div>
          <p class="text-xs text-muted-foreground mt-2">{{ count }} avis</p>
        </div>
      </div>

      @if (ratings.length === 0) {
        <p class="text-xs text-muted-foreground">Aucun avis.</p>
      } @else {
        <div class="space-y-3">
          @for (rating of ratings.slice(0, 5); track rating.id) {
            <div class="rounded-md border p-3">
              <div class="flex items-center justify-between mb-1">
                <p class="font-medium text-sm">{{ rating.clientName }}</p>
                <span class="text-sm">
                  @for (i of [1, 2, 3, 4, 5]; track i) {
                    <span [class.text-yellow-400]="i <= rating.rating" [class.text-gray-300]="i > rating.rating">★</span>
                  }
                </span>
              </div>
              <p class="text-xs text-muted-foreground">{{ rating.comment }}</p>
              <p class="text-xs text-muted-foreground mt-2">{{ formatDate(rating.date) }}</p>
            </div>
          }
        </div>
      }
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class TechnicianRatingsComponent {
  @Input() average: number = 0;
  @Input() count: number = 0;
  @Input() ratings: ClientRating[] = [];

  Math = Math;

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('fr-FR', { year: 'numeric', month: 'short', day: 'numeric' });
  }
}
