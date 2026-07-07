import { Component, inject } from '@angular/core';
import { SidebarService } from '../../../../core/sidebar/sidebar.service';
import { NavMainComponent } from './nav-main.component';
import { NavUserComponent } from './nav-user.component';
import { SidebarSupportCardComponent } from './sidebar-support-card.component';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [NavMainComponent, NavUserComponent, SidebarSupportCardComponent],
  templateUrl: './app-sidebar.component.html',
  styleUrl: './app-sidebar.component.css',
  host: { 'display': 'block' },
})
export class AppSidebarComponent {
  sidebar = inject(SidebarService);
}
