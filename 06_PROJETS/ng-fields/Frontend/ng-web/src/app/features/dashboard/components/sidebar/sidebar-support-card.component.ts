import { Component , ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-sidebar-support-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar-support-card.component.html',
  styleUrl: './sidebar-support-card.component.css',
})
export class SidebarSupportCardComponent {}
