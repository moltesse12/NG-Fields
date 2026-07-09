import { Component, inject, signal , ChangeDetectionStrategy } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { sidebarItems, type NavMainItem, type NavGroup } from '../../../../core/navigation/sidebar-items';
import { SidebarService } from '../../../../core/sidebar/sidebar.service';
import { IconComponent } from '../../../../shared/ui/icon/icon.component';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-nav-main',
  standalone: true,
  imports: [RouterModule, IconComponent],
  templateUrl: './nav-main.component.html',
  styleUrl: './nav-main.component.css',
})
export class NavMainComponent {
  groups = sidebarItems;
  expanded = signal<Record<string, boolean>>({});
  router = inject(Router);
  sidebar = inject(SidebarService);

  isActive(url: string | undefined): boolean {
    if (!url || url === '#') return false;
    return this.router.isActive(url, { paths: 'exact', fragment: 'ignored', matrixParams: 'ignored', queryParams: 'ignored' });
  }

  isAnySubItemActive(item: NavMainItem): boolean {
    if (!item.subItems) return false;
    return item.subItems.some(sub => this.isActive(sub.url));
  }

  toggleSubmenu(id: string): void {
    this.expanded.update(v => ({ ...v, [id]: !v[id] }));
  }

  isExpanded(id: string): boolean {
    return this.expanded()[id] ?? (this.isAnySubItemActive(this.findItem(id)));
  }

  private findItem(id: string): NavMainItem {
    for (const group of this.groups) {
      const item = group.items.find(i => i.id === id || i.subItems?.some(s => s.id === id));
      if (item) return item;
    }
    return {} as NavMainItem;
  }
}
