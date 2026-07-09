import { Component, OnInit, OnDestroy, ElementRef, viewChild, signal, computed , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Chart, ChartConfiguration } from 'chart.js/auto';
import { IconComponent } from '../../../../shared/ui/icon/icon.component';

interface ClientSegment {
  name: string;
  interventions: number;
  revenue: number;
  rating: number;
  color: string;
}

interface TechPerformance {
  rank: number;
  name: string;
  interventions: number;
  avgDuration: string;
  rating: number;
  completionRate: number;
}

interface CategoryDuration {
  label: string;
  icon: string;
  duration: string;
  interventions: number;
  percentage: number;
  color: string;
}

interface QualityMetric {
  label: string;
  value: number;
  percentage: number;
  status: 'good' | 'warn' | 'bad';
  trend: string;
}

interface RegionData {
  name: string;
  interventions: number;
  percentage: number;
  duration: string;
  rating: number;
  color: string;
}

interface AlertItem {
  ref: string;
  client: string;
  issue: string;
  severity: 'high' | 'medium' | 'low';
}

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule, FormsModule, IconComponent],
  template: `
    <div class="flex flex-col gap-6 p-4 md:p-6">
      <div class="flex flex-col gap-1 md:flex-row md:items-center md:justify-between">
        <div>
          <h1 class="text-3xl font-bold tracking-tight">Analytics</h1>
          <p class="text-sm text-muted-foreground">Analyse avancée des performances opérationnelles</p>
        </div>
        <div class="flex items-center gap-2">
          <select
            [value]="period()"
            (change)="period.set($any($event.target).value)"
            class="rounded-md border border-input bg-background px-3 py-2 text-sm"
          >
            <option value="7d">7 jours</option>
            <option value="30d">30 jours</option>
            <option value="90d">90 jours</option>
            <option value="1y">Cette année</option>
          </select>
          <button class="rounded-md border bg-background px-3 py-2 text-xs font-medium hover:bg-accent">
            Exporter PDF
          </button>
        </div>
      </div>

      <!-- Section 1: Segmentation Clientèle -->
      <section class="rounded-lg border bg-card p-4 md:p-6">
        <div class="flex items-center gap-2 mb-4">
          <app-icon name="users" class="text-primary" />
          <h2 class="text-lg font-semibold">Segmentation Clientèle</h2>
        </div>
        <p class="text-sm text-muted-foreground mb-4">Top 10 clients par volume d'interventions</p>
        <div class="space-y-3">
          @for (client of clientSegments(); track client.name) {
            <div class="flex items-center gap-3">
              <span class="w-32 text-sm font-medium truncate">{{ client.name }}</span>
              <div class="flex-1 h-5 rounded-full bg-muted overflow-hidden">
                <div
                  class="h-full rounded-full flex items-center justify-end px-2 text-xs text-white font-medium"
                  [style.width.%]="(client.interventions / maxClientInterventions()) * 100"
                  [style.background]="client.color"
                >
                  {{ client.interventions }}
                </div>
              </div>
              <span class="w-20 text-xs text-right text-muted-foreground">{{ client.revenue | number }} €</span>
              <span class="w-12 text-xs text-right font-medium">{{ client.rating.toFixed(1) }} ★</span>
            </div>
          }
        </div>
        <p class="text-xs text-muted-foreground mt-3">
          Top 3 clients = {{ top3Interventions() }} interventions ({{ top3Percent() }}%)
        </p>
      </section>

      <!-- Section 2: Performance Techniciens -->
      <section class="rounded-lg border bg-card p-4 md:p-6">
        <div class="flex items-center gap-2 mb-4">
          <app-icon name="user-cog" class="text-primary" />
          <h2 class="text-lg font-semibold">Performance Techniciens</h2>
        </div>
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr class="border-b text-left">
                <th class="px-3 py-2 font-medium text-muted-foreground">Rang</th>
                <th class="px-3 py-2 font-medium text-muted-foreground cursor-pointer" (click)="toggleTechSort('name')">
                  Technicien {{ sortTechField() === 'name' ? (sortTechDir() === 'asc' ? '↑' : '↓') : '' }}
                </th>
                <th class="px-3 py-2 font-medium text-muted-foreground cursor-pointer" (click)="toggleTechSort('interventions')">
                  Int. {{ sortTechField() === 'interventions' ? (sortTechDir() === 'asc' ? '↑' : '↓') : '' }}
                </th>
                <th class="px-3 py-2 font-medium text-muted-foreground">Durée Moy</th>
                <th class="px-3 py-2 font-medium text-muted-foreground cursor-pointer" (click)="toggleTechSort('rating')">
                  Note {{ sortTechField() === 'rating' ? (sortTechDir() === 'asc' ? '↑' : '↓') : '' }}
                </th>
                <th class="px-3 py-2 font-medium text-muted-foreground cursor-pointer" (click)="toggleTechSort('completionRate')">
                  Complétion {{ sortTechField() === 'completionRate' ? (sortTechDir() === 'asc' ? '↑' : '↓') : '' }}
                </th>
              </tr>
            </thead>
            <tbody>
              @for (tech of sortedTechs(); track tech.name; let i = $index) {
                <tr class="border-b hover:bg-muted/30">
                  <td class="px-3 py-2 font-bold text-primary">{{ i + 1 }}</td>
                  <td class="px-3 py-2 font-medium">{{ tech.name }}</td>
                  <td class="px-3 py-2">{{ tech.interventions }}</td>
                  <td class="px-3 py-2 text-muted-foreground">{{ tech.avgDuration }}</td>
                  <td class="px-3 py-2">
                    <span [class.text-green-600]="tech.rating >= 4.5" [class.text-yellow-600]="tech.rating < 4.5 && tech.rating >= 4" [class.text-red-600]="tech.rating < 4">
                      {{ tech.rating.toFixed(1) }} ★
                    </span>
                  </td>
                  <td class="px-3 py-2">
                    <div class="flex items-center gap-2">
                      <div class="h-2 w-16 rounded-full bg-muted overflow-hidden">
                        <div class="h-full rounded-full" [style.width.%]="tech.completionRate" [class.bg-green-500]="tech.completionRate >= 95" [class.bg-yellow-500]="tech.completionRate >= 85 && tech.completionRate < 95" [class.bg-red-500]="tech.completionRate < 85"></div>
                      </div>
                      <span class="text-xs" [class.text-green-600]="tech.completionRate >= 95" [class.text-yellow-600]="tech.completionRate >= 85 && tech.completionRate < 95" [class.text-red-600]="tech.completionRate < 85">
                        {{ tech.completionRate }}%
                      </span>
                    </div>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      </section>

      <!-- Section 3: Analyse Durée par Catégorie -->
      <section class="rounded-lg border bg-card p-4 md:p-6">
        <div class="flex items-center gap-2 mb-4">
          <app-icon name="clock" class="text-primary" />
          <h2 class="text-lg font-semibold">Analyse Temps Passé par Catégorie</h2>
        </div>
        <div class="space-y-3">
          @for (cat of categories(); track cat.label) {
            <div class="flex items-center gap-3">
              <span class="w-8 text-center">{{ cat.icon }}</span>
              <span class="w-28 text-sm font-medium">{{ cat.label }}</span>
              <div class="flex-1 h-6 rounded-full bg-muted overflow-hidden">
                <div class="h-full rounded-full flex items-center justify-end px-2 text-xs text-white font-medium" [style.width.%]="cat.percentage" [style.background]="cat.color">
                  {{ cat.duration }}
                </div>
              </div>
              <span class="w-16 text-xs text-right text-muted-foreground">{{ cat.interventions }} int.</span>
              <span class="w-10 text-xs text-right text-muted-foreground">{{ cat.percentage }}%</span>
            </div>
          }
        </div>
        <p class="text-xs text-muted-foreground mt-3">
          Total: {{ totalCategoryInterventions() }} interventions | Durée totale: {{ totalCategoryDuration() }} | Moyenne: {{ avgCategoryDuration() }}
        </p>
      </section>

      <!-- Section 4: Qualité de Saisie -->
      <section class="rounded-lg border bg-card p-4 md:p-6">
        <div class="flex items-center gap-2 mb-4">
          <app-icon name="check-square" class="text-primary" />
          <h2 class="text-lg font-semibold">Qualité de Saisie</h2>
        </div>
        <p class="text-sm text-muted-foreground mb-4">Complétude des fiches d'intervention</p>
        <div class="grid gap-4 md:grid-cols-2">
          @for (metric of qualityMetrics(); track metric.label) {
            <div class="flex items-center gap-3 rounded-lg border p-3">
              <span class="flex-shrink-0 w-8 h-8 rounded-full flex items-center justify-center text-sm"
                [class.bg-green-100]="metric.status === 'good'"
                [class.bg-yellow-100]="metric.status === 'warn'"
                [class.bg-red-100]="metric.status === 'bad'"
              >
                @if (metric.status === 'good') { <span class="text-green-700">✓</span> }
                @if (metric.status === 'warn') { <span class="text-yellow-700">!</span> }
                @if (metric.status === 'bad') { <span class="text-red-700">✗</span> }
              </span>
              <div class="flex-1 min-w-0">
                <p class="text-xs text-muted-foreground truncate">{{ metric.label }}</p>
                <div class="flex items-center gap-2 mt-1">
                  <div class="flex-1 h-2 rounded-full bg-muted overflow-hidden">
                    <div class="h-full rounded-full" [style.width.%]="metric.percentage"
                      [class.bg-green-500]="metric.status === 'good'"
                      [class.bg-yellow-500]="metric.status === 'warn'"
                      [class.bg-red-500]="metric.status === 'bad'"
                    ></div>
                  </div>
                  <span class="text-xs font-medium">{{ metric.percentage }}%</span>
                </div>
              </div>
              <span class="text-xs text-muted-foreground">{{ metric.trend }}</span>
            </div>
          }
        </div>
      </section>

      <!-- Section 5: Couverture Géographique -->
      <section class="rounded-lg border bg-card p-4 md:p-6">
        <div class="flex items-center gap-2 mb-4">
          <app-icon name="map-pin" class="text-primary" />
          <h2 class="text-lg font-semibold">Couverture Géographique</h2>
        </div>
        <div class="grid gap-3">
          @for (region of regions(); track region.name) {
            <div class="flex items-center gap-3">
              <span class="w-32 text-sm font-medium">{{ region.name }}</span>
              <div class="flex-1 h-5 rounded-full bg-muted overflow-hidden">
                <div class="h-full rounded-full flex items-center justify-end px-2 text-xs text-white font-medium"
                  [style.width.%]="region.percentage" [style.background]="region.color">
                  {{ region.interventions }} int.
                </div>
              </div>
              <span class="w-16 text-xs text-right text-muted-foreground">{{ region.duration }}</span>
              <span class="w-10 text-xs text-right font-medium">{{ region.rating.toFixed(1) }} ★</span>
            </div>
          }
        </div>
      </section>

      <!-- Section 6: Alertes & Anomalies -->
      <section class="rounded-lg border bg-card p-4 md:p-6">
        <div class="flex items-center gap-2 mb-4">
          <app-icon name="alert-triangle" class="text-yellow-500" />
          <h2 class="text-lg font-semibold">Alertes & Anomalies</h2>
        </div>
        <p class="text-sm text-muted-foreground mb-4">Interventions nécessitant une attention</p>
        <div class="space-y-2">
          @for (alert of alerts(); track alert.ref) {
            <div class="flex items-center gap-3 rounded-lg border p-3"
              [class.border-red-200]="alert.severity === 'high'"
              [class.border-yellow-200]="alert.severity === 'medium'"
            >
              <span class="flex-shrink-0 w-8 h-8 rounded-full flex items-center justify-center text-sm"
                [class.bg-red-100]="alert.severity === 'high'"
                [class.bg-yellow-100]="alert.severity === 'medium'"
                [class.bg-blue-100]="alert.severity === 'low'"
              >
                @if (alert.severity === 'high') { <span class="text-red-700">!!</span> }
                @if (alert.severity === 'medium') { <span class="text-yellow-700">!</span> }
                @if (alert.severity === 'low') { <span class="text-blue-700">i</span> }
              </span>
              <div class="flex-1">
                <p class="text-sm font-medium">{{ alert.ref }} <span class="text-muted-foreground">— {{ alert.client }}</span></p>
                <p class="text-xs text-muted-foreground">{{ alert.issue }}</p>
              </div>
              <button class="rounded-md border px-3 py-1 text-xs font-medium hover:bg-accent whitespace-nowrap">
                Détails
              </button>
            </div>
          }
        </div>
      </section>

      <!-- Section 7: Trendline 90J -->
      <section class="rounded-lg border bg-card p-4 md:p-6">
        <div class="flex items-center gap-2 mb-4">
          <app-icon name="trending-up" class="text-primary" />
          <h2 class="text-lg font-semibold">Trendline 90 Jours</h2>
        </div>
        <div class="flex items-center gap-1 mb-4">
          @for (range of trendRanges; track range) {
            <button (click)="setTrendRange(range)" class="rounded-md px-2.5 py-1 text-xs font-medium transition-colors"
              [class.bg-primary]="activeTrendRange() === range"
              [class.text-primary-foreground]="activeTrendRange() === range"
              [class.bg-accent]="activeTrendRange() !== range"
              [class.text-foreground]="activeTrendRange() !== range"
            >{{ range }}</button>
          }
          <span class="ml-auto text-xs text-muted-foreground">Total: {{ totalTrendInterventions }}</span>
        </div>
        <div class="relative aspect-auto h-64 w-full">
          <canvas #trendCanvas></canvas>
        </div>
        <p class="text-xs text-muted-foreground mt-2">
          Projection: {{ trendProjection() }} interventions | Tendance: <span class="font-medium text-green-600">{{ trendDirection() }}</span>
        </p>
      </section>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class AnalyticsComponent implements OnInit, OnDestroy {
  period = signal('90d');

  clientSegments = signal<ClientSegment[]>([
    { name: 'Orange Business', interventions: 24, revenue: 4200, rating: 4.9, color: '#40946e' },
    { name: 'MTN Togo', interventions: 18, revenue: 3100, rating: 4.7, color: '#5cb78c' },
    { name: 'SG Togo', interventions: 15, revenue: 2450, rating: 4.5, color: '#79d0a8' },
    { name: 'ECOBANK', interventions: 11, revenue: 1800, rating: 4.3, color: '#94dbb8' },
    { name: 'ONATEL', interventions: 9, revenue: 1400, rating: 3.9, color: '#a8e5c8' },
    { name: 'SNPT', interventions: 7, revenue: 1100, rating: 4.1, color: '#bcecd5' },
    { name: 'Autres (5)', interventions: 16, revenue: 2390, rating: 4.2, color: '#d0f2e2' },
  ]);

  maxClientInterventions = computed(() => Math.max(...this.clientSegments().map(c => c.interventions)));
  top3Interventions = computed(() => this.clientSegments().slice(0, 3).reduce((s, c) => s + c.interventions, 0));
  top3Percent = computed(() => {
    const total = this.clientSegments().reduce((s, c) => s + c.interventions, 0);
    return total ? Math.round((this.top3Interventions() / total) * 100) : 0;
  });

  // Section 2: Performance Techniciens
  rawTechs = signal<TechPerformance[]>([
    { rank: 1, name: 'Alphonse DOSSA', interventions: 18, avgDuration: '12h20m', rating: 4.8, completionRate: 100 },
    { rank: 2, name: 'Kossi AMOUZOU', interventions: 16, avgDuration: '13h15m', rating: 4.7, completionRate: 100 },
    { rank: 3, name: 'Adèle TOGBA', interventions: 14, avgDuration: '11h45m', rating: 4.9, completionRate: 100 },
    { rank: 4, name: 'Jean-Marie POLI', interventions: 13, avgDuration: '14h32m', rating: 4.4, completionRate: 92 },
    { rank: 5, name: 'Amélie NAYO', interventions: 12, avgDuration: '10h50m', rating: 4.6, completionRate: 100 },
    { rank: 6, name: 'Kofi MENSAH', interventions: 11, avgDuration: '12h45m', rating: 4.3, completionRate: 91 },
    { rank: 7, name: 'Yao AGBETOGON', interventions: 9, avgDuration: '15h20m', rating: 4.0, completionRate: 89 },
    { rank: 8, name: 'Emmanuel SOGLO', interventions: 8, avgDuration: '11h55m', rating: 4.5, completionRate: 100 },
    { rank: 9, name: 'Mawunyo ATTAH', interventions: 6, avgDuration: '13h10m', rating: 4.2, completionRate: 83 },
    { rank: 10, name: 'Victoria KOSSIVI', interventions: 4, avgDuration: '12h30m', rating: 4.6, completionRate: 75 },
  ]);
  sortTechField = signal<'name' | 'interventions' | 'rating' | 'completionRate'>('interventions');
  sortTechDir = signal<'asc' | 'desc'>('desc');

  sortedTechs = computed(() => {
    const field = this.sortTechField();
    const dir = this.sortTechDir();
    const sorted = [...this.rawTechs()].sort((a, b) => {
      const aVal = a[field];
      const bVal = b[field];
      if (typeof aVal === 'string' && typeof bVal === 'string') {
        return dir === 'asc' ? aVal.localeCompare(bVal) : bVal.localeCompare(aVal);
      }
      return dir === 'asc' ? (aVal as number) - (bVal as number) : (bVal as number) - (aVal as number);
    });
    return sorted;
  });

  toggleTechSort(field: 'name' | 'interventions' | 'rating' | 'completionRate'): void {
    if (this.sortTechField() === field) {
      this.sortTechDir.set(this.sortTechDir() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortTechField.set(field);
      this.sortTechDir.set(field === 'name' ? 'asc' : 'desc');
    }
  }

  // Section 3: Durée par Catégorie
  categories = signal<CategoryDuration[]>([
    { label: 'Installation', icon: '🔧', duration: '8h 30m', interventions: 40, percentage: 28, color: '#40946e' },
    { label: 'Maintenance', icon: '🔌', duration: '6h 20m', interventions: 35, percentage: 23, color: '#5cb78c' },
    { label: 'Dépannage', icon: '🛠️', duration: '3h 45m', interventions: 28, percentage: 18, color: '#79d0a8' },
    { label: 'Support tech.', icon: '📱', duration: '2h 50m', interventions: 22, percentage: 14, color: '#94dbb8' },
    { label: 'Formation', icon: '🎓', duration: '1h 30m', interventions: 12, percentage: 8, color: '#a8e5c8' },
    { label: 'Autre', icon: '⚡', duration: '0h 45m', interventions: 8, percentage: 5, color: '#bcecd5' },
  ]);

  totalCategoryInterventions = computed(() => this.categories().reduce((s, c) => s + c.interventions, 0));
  totalCategoryDuration = computed(() => {
    const totalHours = this.categories().reduce((s, c) => {
      const parts = c.duration.match(/(\d+)h\s*(\d+)m/);
      return parts ? s + parseInt(parts[1]) + parseInt(parts[2]) / 60 : s;
    }, 0);
    return `${Math.floor(totalHours)}h ${Math.round((totalHours % 1) * 60)}m`;
  });
  avgCategoryDuration = computed(() => {
    const total = this.totalCategoryInterventions();
    if (!total) return '0h 0m';
    const totalHours = this.categories().reduce((s, c) => {
      const parts = c.duration.match(/(\d+)h\s*(\d+)m/);
      return parts ? s + parseInt(parts[1]) + parseInt(parts[2]) / 60 : s;
    }, 0);
    const avg = totalHours / total * 145;
    return `${Math.floor(avg)}h ${Math.round((avg % 1) * 60)}m`;
  });

  // Section 4: Qualité de Saisie
  qualityMetrics = signal<QualityMetric[]>([
    { label: 'Champs obligatoires', value: 245, percentage: 94.2, status: 'good', trend: '↑ +5%' },
    { label: '3 signatures (C+T+R)', value: 247, percentage: 100, status: 'good', trend: '↑ +1%' },
    { label: 'Photos (min 2)', value: 242, percentage: 92.7, status: 'good', trend: '↑ +3%' },
    { label: 'GPS tracé', value: 239, percentage: 91.5, status: 'good', trend: '→ 0%' },
    { label: 'Notes client', value: 25, percentage: 9.6, status: 'bad', trend: '↓ -4%' },
    { label: 'Temps estimé', value: 12, percentage: 4.6, status: 'warn', trend: '→ 0%' },
    { label: 'Données aberrantes', value: 3, percentage: 1.2, status: 'bad', trend: '↑ +0.5%' },
  ]);

  // Section 5: Couverture Géographique
  regions = signal<RegionData[]>([
    { name: 'Région Maritime', interventions: 120, percentage: 46, duration: '16h 45m', rating: 4.7, color: '#40946e' },
    { name: 'Région Centrale', interventions: 85, percentage: 32, duration: '11h 20m', rating: 4.4, color: '#5cb78c' },
    { name: 'Région de la Kara', interventions: 30, percentage: 11, duration: '4h 15m', rating: 4.3, color: '#79d0a8' },
    { name: 'Région des Savanes', interventions: 15, percentage: 6, duration: '1h 50m', rating: 4.1, color: '#94dbb8' },
    { name: 'Région de la Volta', interventions: 10, percentage: 4, duration: '1h 10m', rating: 4.5, color: '#a8e5c8' },
  ]);

  // Section 6: Alertes & Anomalies
  alerts = signal<AlertItem[]>([
    { ref: 'INT-2456', client: 'Orange', issue: 'Durée 8h (x2 la durée type)', severity: 'high' },
    { ref: 'INT-2401', client: 'MTN', issue: 'Photos manquantes (1 seul cliché)', severity: 'high' },
    { ref: 'INT-2398', client: 'SG', issue: 'GPS imprécis (> 500m dérive)', severity: 'medium' },
    { ref: 'INT-2350', client: 'ECOBANK', issue: 'Signatures incomplètes (2/3)', severity: 'medium' },
  ]);

  // Section 7: Trendline
  private trendCanvasRef = viewChild.required<ElementRef<HTMLCanvasElement>>('trendCanvas');
  private chart: Chart | null = null;
  activeTrendRange = signal('90j');
  totalTrendInterventions = '';
  trendRanges = ['30j', '60j', '90j'];
  private trendData: { week: string; count: number }[] = [];

  trendProjection = computed(() => {
    const data = this.getFilteredTrendData();
    if (data.length < 2) return 'N/A';
    const last = data[data.length - 1].count;
    const first = data[0].count;
    const slope = (last - first) / data.length;
    return `${Math.round(last + slope * 4)}+`;
  });

  trendDirection = computed(() => {
    const data = this.getFilteredTrendData();
    if (data.length < 2) return 'Stable';
    const last = data[data.length - 1].count;
    const first = data[0].count;
    return last > first ? 'Haussière ↑' : last < first ? 'Baissière ↓' : 'Stable →';
  });

  constructor() {
    this.generateTrendData();
  }

  ngOnInit(): void {
    this.createTrendChart();
  }

  ngOnDestroy(): void {
    this.chart?.destroy();
  }

  setTrendRange(range: string): void {
    this.activeTrendRange.set(range);
    this.updateTrendChart();
  }

  private generateTrendData(): void {
    const weeks: { week: string; count: number }[] = [];
    let base = 35;
    for (let i = 0; i < 13; i++) {
      base += Math.round(Math.random() * 10 - 2);
      const date = new Date(2026, 3 + Math.floor(i / 4), (i % 4) * 7 + 1);
      weeks.push({ week: `S${date.getMonth() + 1}-${Math.ceil((date.getDate() + 1) / 7)}`, count: Math.max(10, base) });
    }
    this.trendData = weeks;
  }

  private getFilteredTrendData() {
    const days = parseInt(this.activeTrendRange().replace('j', ''), 10);
    const pointsPerWeek = Math.max(1, Math.round(days / 7));
    return this.trendData.slice(-pointsPerWeek);
  }

  private updateTrendChart(): void {
    if (!this.chart) return;
    const data = this.getFilteredTrendData();
    this.chart.data.labels = data.map(d => d.week);
    this.chart.data.datasets[0].data = data.map(d => d.count);
    this.chart.update();
    this.totalTrendInterventions = data.reduce((s, d) => s + d.count, 0).toLocaleString();
  }

  private gradientPlugin = {
    id: 'trendGradient',
    beforeDraw: (chart: Chart) => {
      const { ctx, chartArea } = chart;
      if (!chartArea) return;
      const gradient = ctx.createLinearGradient(0, chartArea.top, 0, chartArea.bottom);
      gradient.addColorStop(0, 'rgba(64, 148, 110, 0.3)');
      gradient.addColorStop(1, 'rgba(64, 148, 110, 0)');
      chart.data.datasets[0].backgroundColor = gradient;
    },
  };

  private createTrendChart(): void {
    const data = this.getFilteredTrendData();
    const canvas = this.trendCanvasRef().nativeElement;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const config: ChartConfiguration = {
      type: 'line',
      data: {
        labels: data.map(d => d.week),
        datasets: [{
          label: 'Interventions',
          data: data.map(d => d.count),
          borderColor: '#40946e',
          backgroundColor: 'rgba(64, 148, 110, 0.3)',
          fill: true,
          tension: 0.4,
          pointRadius: 3,
          pointHoverRadius: 5,
          borderWidth: 2,
        }],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            backgroundColor: 'hsl(var(--popover))',
            titleColor: 'hsl(var(--popover-foreground))',
            bodyColor: 'hsl(var(--popover-foreground))',
            borderColor: 'hsl(var(--border))',
            borderWidth: 1,
            padding: 10,
            displayColors: false,
          },
        },
        scales: {
          x: {
            grid: { display: false },
            ticks: { color: 'hsl(var(--muted-foreground))', maxTicksLimit: 8 },
          },
          y: {
            grid: { color: 'hsl(var(--border))' },
            ticks: { color: 'hsl(var(--muted-foreground))' },
            beginAtZero: true,
          },
        },
        interaction: { intersect: false, mode: 'index' },
      },
      plugins: [this.gradientPlugin],
    };

    this.chart = new Chart(ctx, config);
    this.totalTrendInterventions = data.reduce((s, d) => s + d.count, 0).toLocaleString();
  }
}
