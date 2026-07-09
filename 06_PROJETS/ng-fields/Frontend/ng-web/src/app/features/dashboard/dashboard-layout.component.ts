import { Component, inject , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { AppSidebarComponent } from './components/sidebar/app-sidebar.component';
import { SearchDialogComponent } from './components/header/search-dialog.component';
import { ThemeSwitcherComponent } from './components/header/theme-switcher.component';
import { AccountSwitcherComponent } from './components/header/account-switcher.component';
import { IconComponent } from '../../shared/ui/icon/icon.component';
import { SidebarService } from '../../core/sidebar/sidebar.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, AppSidebarComponent, SearchDialogComponent, ThemeSwitcherComponent, AccountSwitcherComponent, IconComponent],
  templateUrl: './dashboard-layout.component.html',
  styleUrl: './dashboard-layout.component.css',
})
export class DashboardLayoutComponent {
  sidebar = inject(SidebarService);
}
