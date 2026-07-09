import { Component , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

interface StatCard {
  title: string;
  value: string;
  badge: string;
  badgeVariant: 'positive' | 'negative' | 'neutral';
  icon: string;
  trendText: string;
  footerDescription: string;
}

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-section-cards',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="grid @5xl/main:grid-cols-4 @xl/main:grid-cols-2 grid-cols-1 gap-4 *:data-slot='card':bg-linear-to-t *:data-slot='card':from-primary/5 *:data-slot='card':to-card *:data-slot='card':shadow-xs dark:*:data-slot='card':bg-card">
      @for (card of cards; track card.title) {
        <div data-slot="card" class="@container/card rounded-lg border p-6 flex flex-col gap-1.5 relative">
          <div class="text-sm text-muted-foreground">{{ card.title }}</div>
          <div class="font-semibold @[250px]/card:text-3xl text-2xl tabular-nums">{{ card.value }}</div>
          <span
            class="absolute top-6 right-6 inline-flex items-center gap-1 rounded-md border px-1.5 py-0.5 text-xs font-medium text-muted-foreground"
          >
            @if (card.badgeVariant === 'positive') {
              <svg class="size-3" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/><polyline points="17 6 23 6 23 12"/></svg>
            } @else if (card.badgeVariant === 'negative') {
              <svg class="size-3" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="23 18 13.5 8.5 8.5 13.5 1 6"/><polyline points="17 18 23 18 23 12"/></svg>
            }
            {{ card.badge }}
          </span>
          <div class="flex flex-col items-start gap-1.5 text-sm mt-2">
            <div class="line-clamp-1 flex gap-2 font-medium">
              {{ card.trendText }}
              @if (card.badgeVariant === 'positive') {
                <svg class="size-4" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/><polyline points="17 6 23 6 23 12"/></svg>
              } @else if (card.badgeVariant === 'negative') {
                <svg class="size-4" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="23 18 13.5 8.5 8.5 13.5 1 6"/><polyline points="17 18 23 18 23 12"/></svg>
              }
            </div>
            <div class="text-muted-foreground">{{ card.footerDescription }}</div>
          </div>
        </div>
      }
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class SectionCardsComponent {
  cards: StatCard[] = [
    {
      title: 'Total Interventions',
      value: '1,247',
      badge: '+12.5%',
      badgeVariant: 'positive',
      icon: '',
      trendText: 'En hausse ce mois-ci',
      footerDescription: 'Interventions sur les 3 derniers mois',
    },
    {
      title: 'Clients Actifs',
      value: '89',
      badge: '+12.5%',
      badgeVariant: 'positive',
      icon: '',
      trendText: 'Forte acquisition clients',
      footerDescription: 'Nouveaux inscrits ce trimestre',
    },
    {
      title: 'Rapports en Attente',
      value: '23',
      badge: '-5.0%',
      badgeVariant: 'negative',
      icon: '',
      trendText: 'En baisse cette période',
      footerDescription: 'Traitement en cours accéléré',
    },
    {
      title: 'Taux de Couverture',
      value: '94.2%',
      badge: '+2.1%',
      badgeVariant: 'positive',
      icon: '',
      trendText: 'Progression constante',
      footerDescription: 'Atteint les projections de couverture',
    },
  ];
}
