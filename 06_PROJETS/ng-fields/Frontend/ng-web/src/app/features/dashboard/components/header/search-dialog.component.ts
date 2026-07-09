import { Component, HostListener, signal , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { sidebarItems } from '../../../../core/navigation/sidebar-items';
import { FormsModule } from '@angular/forms';
import { A11yModule } from '@angular/cdk/a11y';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-search-dialog',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, A11yModule],
  templateUrl: './search-dialog.component.html',
  styleUrl: './search-dialog.component.css',
})
export class SearchDialogComponent {
  isOpen = signal(false);
  query = signal('');
  allItems: { title: string; url: string; group: string }[] = [];

  constructor() {
    this.allItems = sidebarItems.flatMap(g =>
      (g.items || []).flatMap(item =>
        item.subItems
          ? item.subItems.map(sub => ({ title: sub.title, url: sub.url, group: g.label || '' }))
          : [{ title: item.title, url: item.url || '', group: g.label || '' }]
      )
    );
  }

  get filteredItems() {
    const q = this.query().toLowerCase();
    return q ? this.allItems.filter(i => i.title.toLowerCase().includes(q)) : this.allItems;
  }

  @HostListener('document:keydown', ['$event'])
  handleKeydown(event: KeyboardEvent): void {
    if ((event.ctrlKey || event.metaKey) && event.key === 'j') {
      event.preventDefault();
      this.isOpen.set(true);
    }
    if (event.key === 'Escape') {
      this.isOpen.set(false);
    }
  }

  close(): void {
    this.isOpen.set(false);
    this.query.set('');
  }
}
