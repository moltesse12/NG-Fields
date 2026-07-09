import { Component, Input, Output, EventEmitter, signal, computed, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClientResponse } from '../../../../../shared/models/client.dto';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-clients-table',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './clients-table.component.html',
  styleUrl: './clients-table.component.css',
})
export class ClientsTableComponent {
  @Input() clients: ClientResponse[] = [];
  @Output() onClientClick = new EventEmitter<string>();

  sortColumn = signal<keyof ClientResponse>('companyName');
  sortDirection = signal<'asc' | 'desc'>('asc');
  pageSize = signal(10);
  currentPage = signal(1);
  selectedIds = signal<Set<string>>(new Set());
  visibleColumns = signal<Set<keyof ClientResponse>>(
    new Set(['companyName', 'active', 'contactName', 'email', 'phone', 'createdAt'])
  );
  filterActive = signal<string>('');

  pageSizeOptions = [10, 20, 30, 50];
  allColumns: { key: keyof ClientResponse; label: string }[] = [
    { key: 'companyName', label: 'Raison sociale' },
    { key: 'reference', label: 'Référence' },
    { key: 'active', label: 'Statut' },
    { key: 'contactName', label: 'Contact' },
    { key: 'email', label: 'Email' },
    { key: 'phone', label: 'Téléphone' },
    { key: 'address', label: 'Adresse' },
    { key: 'createdAt', label: 'Créé le' },
  ];

  filteredData = computed(() => {
    const filter = this.filterActive();
    if (!filter) return this.clients;
    return this.clients.filter(c => filter === 'active' ? c.active : !c.active);
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

  sortBy(column: keyof ClientResponse): void {
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

  toggleColumn(key: keyof ClientResponse): void {
    const set = new Set(this.visibleColumns());
    set.has(key) ? set.delete(key) : set.add(key);
    this.visibleColumns.set(set);
  }

  isColumnVisible(key: keyof ClientResponse): boolean {
    return this.visibleColumns().has(key);
  }

  statusBadgeClass(active: boolean): string {
    return active
      ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400'
      : 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
  }

  statusLabel(active: boolean): string {
    return active ? 'Actif' : 'Inactif';
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
