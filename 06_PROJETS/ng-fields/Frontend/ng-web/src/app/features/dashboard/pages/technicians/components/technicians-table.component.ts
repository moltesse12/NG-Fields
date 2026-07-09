import { Component, Input, Output, EventEmitter, signal, computed, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TechnicianResponse } from '../../../../../shared/models/technician.dto';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-technicians-table',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './technicians-table.component.html',
  styleUrl: './technicians-table.component.css',
})
export class TechniciansTableComponent {
  @Input() technicians: TechnicianResponse[] = [];
  @Output() onTechnicianClick = new EventEmitter<string>();

  sortColumn = signal<keyof TechnicianResponse>('lastName');
  sortDirection = signal<'asc' | 'desc'>('asc');
  pageSize = signal(10);
  currentPage = signal(1);
  selectedIds = signal<Set<string>>(new Set());
  visibleColumns = signal<Set<string>>(
    new Set(['firstName', 'status', 'email', 'phone'])
  );
  filterStatus = signal<string>('');

  pageSizeOptions = [10, 20, 30, 50];
  allColumns: { key: keyof TechnicianResponse; label: string }[] = [
    { key: 'firstName', label: 'Nom' },
    { key: 'email', label: 'Email' },
    { key: 'phone', label: 'Téléphone' },
    { key: 'status', label: 'Statut' },
    { key: 'active', label: 'Actif' },
    { key: 'createdAt', label: 'Créé le' },
  ];

  filteredData = computed(() => {
    const status = this.filterStatus();
    if (!status) return this.technicians;
    return this.technicians.filter(t => t.status === status);
  });

  sortedData = computed(() => {
    const col = this.sortColumn();
    const dir = this.sortDirection();
    return [...this.filteredData()].sort((a, b) => {
      const aVal = a[col];
      const bVal = b[col];
      if (col === 'active') {
        const cmp = Number(bVal) - Number(aVal);
        return dir === 'asc' ? cmp : -cmp;
      }
      const cmp = typeof aVal === 'number' && typeof bVal === 'number'
        ? aVal - bVal
        : String(aVal ?? '').localeCompare(String(bVal ?? ''));
      return dir === 'asc' ? cmp : -cmp;
    });
  });

  paginatedData = computed(() => {
    const page = this.currentPage();
    const size = this.pageSize();
    const start = (page - 1) * size;
    return this.sortedData().slice(start, start + size);
  });

  totalPages = computed(() => Math.ceil(this.sortedData().length / this.pageSize()));

  sortBy(column: string): void {
    if (this.sortColumn() === column) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortColumn.set(column as keyof TechnicianResponse);
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
    this.selectedIds.set(checked ? new Set(this.paginatedData().map(r => r.id)) : new Set());
  }

  toggleRow(id: string): void {
    const set = new Set(this.selectedIds());
    set.has(id) ? set.delete(id) : set.add(id);
    this.selectedIds.set(set);
  }

  isAllSelected(): boolean {
    const page = this.paginatedData();
    return page.length > 0 && page.every(r => this.selectedIds().has(r.id));
  }

  isSelected(id: string): boolean {
    return this.selectedIds().has(id);
  }

  toggleColumn(key: string): void {
    const set = new Set(this.visibleColumns());
    set.has(key) ? set.delete(key) : set.add(key);
    this.visibleColumns.set(set);
  }

  isColumnVisible(key: string): boolean {
    return this.visibleColumns().has(key);
  }

  statusBadgeClass(status: string): string {
    const map: Record<string, string> = {
      'AVAILABLE': 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400',
      'BUSY': 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
      'ON_LEAVE': 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400',
      'INACTIVE': 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400',
    };
    return map[status] || 'bg-gray-100 text-gray-700';
  }

  statusLabel(status: string): string {
    const map: Record<string, string> = {
      'AVAILABLE': 'Disponible',
      'BUSY': 'Occupé',
      'ON_LEAVE': 'Congé',
      'INACTIVE': 'Inactif',
    };
    return map[status] || status;
  }

  formatDate(date: string | undefined | null): string {
    if (!date) return '—';
    return new Date(date).toLocaleDateString('fr-FR', {
      year: 'numeric', month: 'short', day: 'numeric',
    });
  }

  get pageInfo(): string {
    const start = (this.currentPage() - 1) * this.pageSize() + 1;
    const end = Math.min(this.currentPage() * this.pageSize(), this.sortedData().length);
    return `${start}-${end} sur ${this.sortedData().length}`;
  }
}
