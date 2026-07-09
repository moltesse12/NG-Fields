import { Component, OnInit, OnDestroy, ElementRef, viewChild, signal , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Chart, ChartConfiguration } from 'chart.js/auto';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-chart-area-interactive',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="@container/card rounded-lg border bg-card">
      <div class="p-6 flex flex-col gap-1.5">
        <div class="text-sm font-medium">Interventions par Jour</div>
        <div class="text-2xl font-bold tabular-nums">{{ totalInterventions }}</div>
        <div class="text-sm text-muted-foreground">
          <span class="@[540px]/card:block hidden">Total des interventions sur les 3 derniers mois</span>
          <span class="@[540px]/card:hidden">3 derniers mois</span>
        </div>
        <div class="absolute top-6 right-6 flex gap-1">
          <div class="@[767px]/card:flex hidden gap-1">
            @for (range of timeRanges; track range) {
              <button (click)="setRange(range)" class="rounded-md px-2.5 py-1 text-xs font-medium transition-colors"
                [class.bg-primary]="activeRange() === range"
                [class.text-primary-foreground]="activeRange() === range"
                [class.bg-accent]="activeRange() !== range"
                [class.text-foreground]="activeRange() !== range"
              >{{ range }}</button>
            }
          </div>
          <select
            (change)="setRangeMobile($event)"
            class="@[767px]/card:hidden flex rounded-md border bg-background px-2 py-1 text-xs font-medium max-w-24"
          >
            @for (range of timeRanges; track range) {
              <option [value]="range" [selected]="activeRange() === range">{{ range }}</option>
            }
          </select>
          <div class="@[767px]/card:flex hidden">
            <button class="rounded-md border bg-background px-2.5 py-1 text-xs font-medium hover:bg-accent">Voir rapport</button>
          </div>
        </div>
      </div>
      <div class="px-2 pt-4 sm:px-6 sm:pt-6 pb-6">
        <div class="relative aspect-auto h-80 w-full">
          <canvas #chartCanvas></canvas>
        </div>
      </div>
    </div>
  `,
  styles: [':host { display: block; }'],
})
export class ChartAreaInteractiveComponent implements OnInit, OnDestroy {
  private chartRef = viewChild.required<ElementRef<HTMLCanvasElement>>('chartCanvas');
  private chart: Chart | null = null;

  activeRange = signal<string>('30j');
  totalInterventions = '';
  timeRanges = ['7j', '30j', '90j'];

  private allData: { date: string; count: number }[] = [];

  constructor() {
    this.generateData();
    this.updateTotal();
  }

  ngOnInit(): void {
    this.createChart();
  }

  ngOnDestroy(): void {
    this.chart?.destroy();
  }

  setRange(range: string): void {
    this.activeRange.set(range);
    this.updateChart();
  }

  setRangeMobile(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.setRange(value);
  }

  private generateData(): void {
    const start = new Date('2025-04-01');
    const end = new Date('2025-06-30');
    const points: { date: string; count: number }[] = [];
    let current = new Date(start);
    while (current <= end) {
      const count = Math.round(Math.random() * 16 + 2);
      points.push({
        date: current.toISOString().slice(0, 10),
        count,
      });
      current.setDate(current.getDate() + 1);
    }
    this.allData = points;
  }

  private getFilteredData() {
    const days = parseInt(this.activeRange().replace('j', ''), 10);
    return this.allData.slice(-days);
  }

  private updateTotal(): void {
    const data = this.getFilteredData();
    this.totalInterventions = data.reduce((s, d) => s + d.count, 0).toLocaleString();
  }

  private updateChart(): void {
    if (!this.chart) return;
    const data = this.getFilteredData();
    this.chart.data.labels = data.map(d => d.date.slice(5));
    this.chart.data.datasets[0].data = data.map(d => d.count);
    this.chart.update();
    this.updateTotal();
  }

  private gradientPlugin = {
    id: 'customGradient',
    beforeDraw: (chart: Chart) => {
      const { ctx, chartArea } = chart;
      if (!chartArea) return;
      const gradient = ctx.createLinearGradient(0, chartArea.top, 0, chartArea.bottom);
      gradient.addColorStop(0, 'rgba(64, 148, 110, 0.3)');
      gradient.addColorStop(1, 'rgba(64, 148, 110, 0)');
      chart.data.datasets[0].backgroundColor = gradient;
    },
  };

  private createChart(): void {
    const data = this.getFilteredData();
    const canvas = this.chartRef().nativeElement;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const config: ChartConfiguration = {
      type: 'line',
      data: {
        labels: data.map(d => d.date.slice(5)),
        datasets: [{
          label: 'Interventions',
          data: data.map(d => d.count),
          borderColor: '#40946e',
          backgroundColor: 'rgba(64, 148, 110, 0.3)',
          fill: true,
          tension: 0.4,
          pointRadius: 0,
          pointHoverRadius: 4,
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
        interaction: {
          intersect: false,
          mode: 'index',
        },
      },
      plugins: [this.gradientPlugin],
    };

    this.chart = new Chart(ctx, config);
    this.updateTotal();
  }
}
