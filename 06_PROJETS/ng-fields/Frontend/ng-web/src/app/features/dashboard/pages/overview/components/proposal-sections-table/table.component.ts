import { Component, signal, computed, ElementRef, viewChild , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { Chart, ChartConfiguration } from 'chart.js/auto';
import { InterventionRow } from './schema';
import rawData from './data.json';

type TabId = 'all' | 'in-progress' | 'completed' | 'scheduled';

interface TabDef {
  id: TabId;
  label: string;
  badge?: number;
}

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-proposal-sections-table',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './table.component.html',
  styleUrl: './table.component.css',
})
export class ProposalSectionsTableComponent {
  data = signal<InterventionRow[]>(rawData as InterventionRow[]);

  tabs: TabDef[] = [
    { id: 'all', label: 'Toutes' },
    { id: 'in-progress', label: 'En Cours', badge: this.data().filter(r => r.status === 'En Cours').length },
    { id: 'completed', label: 'Terminées', badge: this.data().filter(r => r.status === 'Complété').length },
    { id: 'scheduled', label: 'Planifiées', badge: this.data().filter(r => r.status === 'Planifié').length },
  ];

  drawerChartCanvas = viewChild<ElementRef<HTMLCanvasElement>>('drawerChartCanvas');

  activeTab = signal<TabId>('all');
  searchQuery = signal('');
  dateFilter = signal<'all' | '30' | '90'>('all');
  selectedRow = signal<InterventionRow | null>(null);
  drawerOpen = signal(false);
  private drawerChart: Chart | null = null;
  openDropdownId = signal<number | null>(null);
  columnDropdownOpen = signal(false);

  filteredData = computed(() => {
    const tab = this.activeTab();
    const query = this.searchQuery().toLowerCase();
    const dateF = this.dateFilter();
    const all = this.data();
    let result = all;
    if (tab === 'in-progress') result = result.filter(r => r.status === 'En Cours');
    else if (tab === 'completed') result = result.filter(r => r.status === 'Complété');
    else if (tab === 'scheduled') result = result.filter(r => r.status === 'Planifié');
    if (query) {
      result = result.filter(r => r.client.toLowerCase().includes(query) || r.type.toLowerCase().includes(query) || r.assignedTo.toLowerCase().includes(query));
    }
    if (dateF !== 'all') {
      const cutoff = new Date();
      cutoff.setDate(cutoff.getDate() - parseInt(dateF, 10));
      result = result.filter(r => new Date(r.date) >= cutoff);
    }
    return result;
  });

  sortColumn = signal<keyof InterventionRow>('id');
  sortDirection = signal<'asc' | 'desc'>('asc');
  sortOption = signal<string>('newest');
  pageSize = signal(10);
  currentPage = signal(1);
  selectedIds = signal<Set<number>>(new Set());
  visibleColumns = signal<Set<keyof InterventionRow>>(
    new Set(['id', 'client', 'type', 'status', 'zone', 'assignedTo', 'date', 'priority'])
  );

  pageSizeOptions = [10, 20, 30, 40, 50];
  allColumns: { key: keyof InterventionRow; label: string }[] = [
    { key: 'id', label: '#' },
    { key: 'client', label: 'Client' },
    { key: 'type', label: 'Type' },
    { key: 'status', label: 'Statut' },
    { key: 'zone', label: 'Zone' },
    { key: 'assignedTo', label: 'Assigné à' },
    { key: 'date', label: 'Date' },
    { key: 'priority', label: 'Priorité' },
  ];

  sortedData = computed(() => {
    const col = this.sortColumn();
    const dir = this.sortDirection();
    const sorted = [...this.filteredData()].sort((a, b) => {
      const aVal = a[col];
      const bVal = b[col];
      const cmp = typeof aVal === 'number' && typeof bVal === 'number'
        ? aVal - bVal
        : String(aVal).localeCompare(String(bVal));
      return dir === 'asc' ? cmp : -cmp;
    });
    return sorted;
  });

  paginatedData = computed(() => {
    const page = this.currentPage();
    const size = this.pageSize();
    const start = (page - 1) * size;
    return this.sortedData().slice(start, start + size);
  });

  totalPages = computed(() => Math.ceil(this.filteredData().length / this.pageSize()));

  selectedCount = computed(() => this.selectedIds().size);
  totalFilteredCount = computed(() => this.filteredData().length);

  setSearchQuery(value: string): void {
    this.searchQuery.set(value);
    this.currentPage.set(1);
  }

  setDateFilter(value: string): void {
    this.dateFilter.set(value as 'all' | '30' | '90');
    this.currentPage.set(1);
  }

  setSortOption(value: string): void {
    this.sortOption.set(value);
    switch (value) {
      case 'newest': this.sortColumn.set('date'); this.sortDirection.set('desc'); break;
      case 'oldest': this.sortColumn.set('date'); this.sortDirection.set('asc'); break;
      case 'client-asc': this.sortColumn.set('client'); this.sortDirection.set('asc'); break;
      case 'client-desc': this.sortColumn.set('client'); this.sortDirection.set('desc'); break;
    }
  }

  switchTab(tab: TabId): void {
    this.activeTab.set(tab);
    this.currentPage.set(1);
    this.selectedIds.set(new Set());
  }

  sortBy(column: keyof InterventionRow): void {
    if (this.sortColumn() === column) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortColumn.set(column);
      this.sortDirection.set('asc');
    }
  }

  setPageSize(size: number): void {
    this.pageSize.set(size);
    this.currentPage.set(1);
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages()) {
      this.currentPage.set(page);
    }
  }

  toggleAllRows(event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
    if (checked) {
      this.selectedIds.set(new Set(this.paginatedData().map(r => r.id)));
    } else {
      this.selectedIds.set(new Set());
    }
  }

  toggleRow(id: number): void {
    const set = new Set(this.selectedIds());
    if (set.has(id)) set.delete(id);
    else set.add(id);
    this.selectedIds.set(set);
  }

  isAllSelected(): boolean {
    const page = this.paginatedData();
    return page.length > 0 && page.every(r => this.selectedIds().has(r.id));
  }

  isSelected(id: number): boolean {
    return this.selectedIds().has(id);
  }

  toggleColumn(key: keyof InterventionRow): void {
    const set = new Set(this.visibleColumns());
    if (set.has(key)) set.delete(key);
    else set.add(key);
    this.visibleColumns.set(set);
  }

  isColumnVisible(key: keyof InterventionRow): boolean {
    return this.visibleColumns().has(key);
  }

  get pageInfo(): string {
    const total = this.filteredData().length;
    if (total === 0) return '0-0 / 0';
    const start = (this.currentPage() - 1) * this.pageSize() + 1;
    const end = Math.min(this.currentPage() * this.pageSize(), total);
    return `${start}-${end} / ${total}`;
  }

  private drawerChartData = [
    { month: 'Janvier', count: 186 },
    { month: 'Février', count: 305 },
    { month: 'Mars', count: 237 },
    { month: 'Avril', count: 73 },
    { month: 'Mai', count: 209 },
    { month: 'Juin', count: 214 },
  ];

  openDrawer(row: InterventionRow): void {
    this.selectedRow.set({ ...row });
    this.drawerOpen.set(true);
    setTimeout(() => this.createDrawerChart(), 0);
  }

  closeDrawer(): void {
    this.drawerChart?.destroy();
    this.drawerChart = null;
    this.drawerOpen.set(false);
    this.selectedRow.set(null);
  }

  private createDrawerChart(): void {
    const canvasEl = this.drawerChartCanvas();
    if (!canvasEl) return;
    const ctx = canvasEl.nativeElement.getContext('2d');
    if (!ctx) return;

    const gradient = ctx.createLinearGradient(0, 0, 0, 100);
    gradient.addColorStop(0, 'rgba(64, 148, 110, 0.3)');
    gradient.addColorStop(1, 'rgba(64, 148, 110, 0)');

    const config: ChartConfiguration = {
      type: 'line',
      data: {
        labels: this.drawerChartData.map(d => d.month.slice(0, 3)),
        datasets: [{
          label: 'Interventions',
          data: this.drawerChartData.map(d => d.count),
          borderColor: '#40946e',
          backgroundColor: gradient,
          fill: true,
          tension: 0.4,
          pointRadius: 2,
          pointHoverRadius: 4,
          borderWidth: 2,
        }],
      },
      options: {
        responsive: true,
        maintainAspectRatio: true,
        aspectRatio: 4,
        plugins: {
          legend: { display: false },
          tooltip: { enabled: false },
        },
        scales: {
          x: { display: false },
          y: { display: false, beginAtZero: true },
        },
      },
    };

    this.drawerChart = new Chart(ctx, config);
  }

  updateDrawerField(key: string, value: string): void {
    const current = this.selectedRow();
    if (current) {
      this.selectedRow.set({ ...current, [key]: value });
    }
  }

  saveDrawer(): void {
    const updated = this.selectedRow();
    if (!updated) return;
    this.data.update(arr => arr.map(r => r.id === updated.id ? updated : r));
    this.closeDrawer();
  }

  toggleDropdown(id: number): void {
    this.openDropdownId.set(this.openDropdownId() === id ? null : id);
  }

  closeDropdown(): void {
    this.openDropdownId.set(null);
  }

  toggleColumnDropdown(): void {
    this.columnDropdownOpen.update(v => !v);
  }

  closeColumnDropdown(): void {
    this.columnDropdownOpen.set(false);
  }

  statusClass(status: string): string {
    const map: Record<string, string> = {
      'Complété': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      'En Cours': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
      'Planifié': 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400',
      'Annulé': 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400',
      'Brouillon': 'bg-gray-100 text-gray-700 dark:bg-gray-800 dark:text-gray-400',
    };
    return map[status] || 'bg-gray-100 text-gray-700';
  }

  priorityClass(priority: string): string {
    const map: Record<string, string> = {
      'Haute': 'text-red-600 dark:text-red-400 font-semibold',
      'Moyenne': 'text-yellow-600 dark:text-yellow-400 font-semibold',
      'Basse': 'text-green-600 dark:text-green-400 font-semibold',
    };
    return map[priority] || '';
  }
}
